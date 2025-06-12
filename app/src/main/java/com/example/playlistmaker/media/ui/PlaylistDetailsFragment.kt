package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.example.playlistmaker.media.view_model.PlaylistDetailsScreenState
import com.example.playlistmaker.media.view_model.PlaylistDetailsViewModel
import com.example.playlistmaker.search.ui.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistDetailsFragment : Fragment() {

    private lateinit var binding: FragmentPlaylistDetailsBinding
    private val viewModel by viewModel<PlaylistDetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val playlistId = arguments?.getLong("playlistId") ?: -1L
        viewModel.init(playlistId)
        binding.rvPlaylist.adapter = TrackAdapter(emptyList())

        viewModel.getScreenStateLiveData().observe(viewLifecycleOwner) { screenState ->
            when (screenState) {
                is PlaylistDetailsScreenState.Content -> {
                    loadTrackInfo(screenState)
                }

                else -> {
                }
            }
        }

        binding.apToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })


    }

    private fun loadTrackInfo(screenState: PlaylistDetailsScreenState.Content) {
        if (!screenState.playlist?.playlistImageUrl.isNullOrEmpty()) {
            Glide.with(this@PlaylistDetailsFragment)
                .load(screenState.playlist?.playlistImageUrl?.toUri())
                .placeholder(R.drawable.placeholder_full_size).into(binding.plPlaylistImage)
        } else {
            binding.plPlaylistImage.setImageResource(R.drawable.placeholder)
        }

        binding.plPlaylistName.text = screenState.playlist?.playlistName
        binding.plPlaylistDescr.text = screenState.playlist?.playlistDescr
        (binding.rvPlaylist.adapter as TrackAdapter).updateData(screenState.tracks)
        binding.plPlaylistTime.text = this@PlaylistDetailsFragment.resources.getQuantityString(
            R.plurals.minute_count, screenState.totalTime.toInt(), screenState.totalTime,
        )
        binding.plPlaylistCount.text = this@PlaylistDetailsFragment.resources.getQuantityString(
            R.plurals.track_count, screenState.trackCount, screenState.trackCount
        )

    }
}