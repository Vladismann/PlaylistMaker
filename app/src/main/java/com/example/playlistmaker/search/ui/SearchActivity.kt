package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.TrackActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.view_model.SearchViewModel

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding

    private val viewModel by viewModels<SearchViewModel> {
        SearchViewModel.getViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.seToolbar)
        binding.rvTrack.adapter = TrackAdapter(emptyList())
        binding.rvTrackHistory.adapter = TrackAdapter(emptyList())

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.searchResults.observe(this) { tracks ->
            if (viewModel.errorState.value == true) {
                showError(R.drawable.track_search_error, R.string.connection_error, true)
            } else if (tracks.isEmpty()) {
                showError(R.drawable.track_not_found, R.string.nothing_found, false)
            } else {
                (binding.rvTrack.adapter as TrackAdapter).updateData(tracks)
                binding.rvTrack.visibility = View.VISIBLE
                binding.errorMessageElement.visibility = View.GONE
            }
            binding.searchProgressBar.visibility = View.GONE
        }

        viewModel.loadingState.observe(this) { isLoading ->
            binding.searchProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.historyTracks.observe(this) { history ->
            if (binding.inputSearch.hasFocus() && !binding.inputSearch.text.isNullOrEmpty()) {
                binding.historyElement.visibility = View.GONE
            } else {
                (binding.rvTrackHistory.adapter as TrackAdapter).updateData(history)
                binding.historyElement.visibility =
                    if (history.isEmpty()) View.GONE else View.VISIBLE
            }
        }
    }

    private fun setupListeners() {
        binding.seToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

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

        (binding.rvTrack.adapter as TrackAdapter).setOnItemClickListener { position ->
            if (viewModel.clickDebounce()) {
                val clickedTrack = (binding.rvTrack.adapter as TrackAdapter).getItem(position)
                viewModel.saveTrackToHistory(clickedTrack)
                startTrackActivity(clickedTrack)
            }
        }

        (binding.rvTrackHistory.adapter as TrackAdapter).setOnItemClickListener { position ->
            if (viewModel.clickDebounce()) {
                val clickedTrack =
                    (binding.rvTrackHistory.adapter as TrackAdapter).getItem(position)
                startTrackActivity(clickedTrack)
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

    private fun startTrackActivity(track: Track) {
        viewModel.saveForAudioPlayer(track)
        val intent = Intent(this, TrackActivity::class.java)
        startActivity(intent)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}

