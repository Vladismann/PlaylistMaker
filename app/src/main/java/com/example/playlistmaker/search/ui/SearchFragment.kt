package com.example.playlistmaker.search.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.view_model.SearchScreenState
import com.example.playlistmaker.search.view_model.SearchViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private var isClickAllowed = true
    private val clickDebounceDelay = 1000L
    private val viewModel by viewModel<SearchViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity()
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as? AppCompatActivity)?.setSupportActionBar(binding.seToolbar)
        binding.rvTrack.adapter = TrackAdapter(emptyList())
        binding.rvTrackHistory.adapter = TrackAdapter(emptyList())

        findNavController().addOnDestinationChangedListener { _, destination, _ ->
            Log.d("NAV_DEBUG", "Navigated to ${destination.label}")
        }
        Log.d("NAV_DEBUG", "Current destination: ${findNavController().currentDestination?.label}")

        setupObservers()
        setupListeners()

        binding.inputSearch.requestFocus()
    }

    private fun setupObservers() {
        viewModel.searchScreenState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchScreenState.Loading -> {
                    binding.searchProgressBar.visibility = View.VISIBLE
                    binding.errorMessageElement.visibility = View.GONE
                    binding.rvTrack.visibility = View.GONE
                    binding.historyElement.visibility = View.GONE
                }

                is SearchScreenState.Content -> {
                    binding.searchProgressBar.visibility = View.GONE
                    binding.errorMessageElement.visibility = View.GONE

                    if (state.tracks.isEmpty() && state.query.isNotBlank()) {
                        showError(R.drawable.track_not_found, R.string.nothing_found, false)
                    } else {
                        (binding.rvTrack.adapter as TrackAdapter).updateData(state.tracks)
                        binding.rvTrack.visibility = View.VISIBLE
                    }

                    if (binding.inputSearch.hasFocus() && !binding.inputSearch.text.isNullOrEmpty()) {
                        binding.historyElement.visibility = View.GONE
                    } else {
                        if (binding.inputSearch.hasFocus()) {
                            (binding.rvTrackHistory.adapter as TrackAdapter).updateData(state.historyTracks)
                            binding.historyElement.visibility =
                                if (state.historyTracks.isEmpty()) View.GONE else View.VISIBLE
                        }
                    }
                }

                is SearchScreenState.Error -> {
                    showError(R.drawable.track_search_error, R.string.connection_error, state.showRefresh)
                }
            }
        }
    }

    private fun setupListeners() {
        binding.clearIcon.setOnClickListener {
            binding.inputSearch.setText("")
            (binding.rvTrack.adapter as TrackAdapter).clear()
            binding.rvTrack.visibility = View.GONE
            binding.errorMessageElement.visibility = View.GONE
            hideKeyboard()
        }

        binding.inputSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.isVisible = !s.isNullOrEmpty()
                binding.rvTrack.visibility = View.GONE
                binding.historyElement.visibility = View.GONE
                binding.errorMessageElement.visibility = View.GONE

                if (s.isNullOrEmpty()) {
                    binding.rvTrack.visibility = View.GONE
                    (binding.rvTrack.adapter as TrackAdapter).clear()
                    binding.errorMessageElement.visibility = View.GONE
                    viewModel.searchDebounce(s.toString())
                    return

                } else {
                    viewModel.searchDebounce(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.refreshTracks.setOnClickListener {
            binding.errorMessageElement.visibility = View.GONE
            binding.searchProgressBar.visibility = View.VISIBLE
            viewModel.searchDebounce(binding.inputSearch.text.toString())
        }

        (binding.rvTrack.adapter as TrackAdapter).setOnItemClickListener { track ->
            if (clickDebounce()) {
                binding.rvTrack.visibility = View.GONE
                binding.historyElement.visibility = View.GONE
                binding.searchProgressBar.visibility = View.VISIBLE
                viewModel.saveTrackToHistory(track)
                startTrackFragment(track)
            }
        }

        (binding.rvTrackHistory.adapter as TrackAdapter).setOnItemClickListener { track ->
            if (clickDebounce()) {
                binding.historyElement.visibility = View.GONE
                binding.searchProgressBar.visibility = View.VISIBLE
                viewModel.saveTrackToHistory(track)
                startTrackFragment(track)
            }
        }

        binding.clearSearchHistory.setOnClickListener {
            viewModel.clearHistory()
        }
    }

    private fun showError(imageRes: Int, textRes: Int, showRefresh: Boolean) {
        binding.searchProgressBar.visibility = View.GONE
        (binding.rvTrack.adapter as TrackAdapter).clear()
        binding.rvTrack.visibility = View.GONE
        binding.errorMessageElement.visibility = View.VISIBLE
        binding.errorImage.setImageResource(imageRes)
        binding.errorText.setText(textRes)
        binding.refreshTracks.visibility = if (showRefresh) View.VISIBLE else View.GONE
    }

    private fun startTrackFragment(track: Track) {
        viewModel.saveForAudioPlayer(track)
        viewLifecycleOwner.lifecycleScope.launch {
            delay(1000)
            val navController = findNavController()
            navController.navigate(R.id.action_global_to_trackFragment)
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(clickDebounceDelay)
                isClickAllowed = true
            }
        }
        return current
    }
}