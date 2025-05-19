package com.example.playlistmaker.media.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.media.view_model.FavoritesScreenState
import com.example.playlistmaker.media.view_model.FavoritesViewModel
import com.example.playlistmaker.player.ui.TrackActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentFavorites : Fragment() {
    companion object {
        fun newInstance(): FragmentFavorites {
            return FragmentFavorites()
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
        binding.rvTrack.adapter = TrackAdapter(emptyList())

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.screenState.collect { state ->
                    when (state) {
                        is FavoritesScreenState.Loading -> {
                            binding.searchProgressBar.visibility = View.VISIBLE
                            binding.errorMessageElement.visibility = View.GONE
                            binding.rvTrack.visibility = View.GONE
                        }
                        is FavoritesScreenState.Content -> {
                            binding.searchProgressBar.visibility = View.GONE
                            if (state.tracks.isEmpty()) {
                                binding.errorMessageElement.visibility = View.VISIBLE
                                binding.rvTrack.visibility = View.GONE
                            } else {
                                (binding.rvTrack.adapter as TrackAdapter).updateData(state.tracks)
                                binding.rvTrack.visibility = View.VISIBLE
                                binding.errorMessageElement.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }

        (binding.rvTrack.adapter as TrackAdapter).setOnItemClickListener { track ->
            if (clickDebounce()) {
                binding.searchProgressBar.visibility = View.VISIBLE
                startTrackActivity(track)
            }
        }

    }

    private fun startTrackActivity(track: Track) {
        viewModel.saveForAudioPlayer(track)
        val intent = Intent(requireContext(), TrackActivity::class.java)
        startActivity(intent)
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