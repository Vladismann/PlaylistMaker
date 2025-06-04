package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.media.view_model.FavoritesScreenState
import com.example.playlistmaker.media.view_model.FavoritesViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {
    companion object {
        fun newInstance(): FavoritesFragment {
            return FavoritesFragment()
        }
    }

    private val viewModel: FavoritesViewModel by viewModel()
    private lateinit var binding: FragmentFavoritesBinding
    private var isClickAllowed = true
    private val clickDebounceDelay = 1000L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fRvTrack.adapter = TrackAdapter(emptyList())

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.screenState.collect { state ->
                    when (state) {
                        is FavoritesScreenState.Loading -> {
                            binding.searchProgressBar.visibility = View.VISIBLE
                            binding.fErrorMessageElement.visibility = View.GONE
                            binding.fRvTrack.visibility = View.GONE
                        }

                        is FavoritesScreenState.Content -> {
                            binding.searchProgressBar.visibility = View.GONE
                            if (state.tracks.isEmpty()) {
                                binding.fErrorMessageElement.visibility = View.VISIBLE
                                binding.fRvTrack.visibility = View.GONE
                            } else {
                                (binding.fRvTrack.adapter as TrackAdapter).updateData(state.tracks)
                                binding.fRvTrack.visibility = View.VISIBLE
                                binding.fErrorMessageElement.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }

        (binding.fRvTrack.adapter as TrackAdapter).setOnItemClickListener { track ->
            if (clickDebounce()) {
                binding.searchProgressBar.visibility = View.VISIBLE
                startTrackFragment(track)
            }
        }

    }

    private fun startTrackFragment(track: Track) {
        viewModel.saveForAudioPlayer(track)
        viewLifecycleOwner.lifecycleScope.launch {
            delay(1000)
            val navController = findNavController()
            navController.navigate(R.id.action_global_to_trackFragment)
        }
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