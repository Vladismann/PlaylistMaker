package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAudioplayerBinding
import com.example.playlistmaker.player.view_model.PlayerState
import com.example.playlistmaker.player.view_model.TrackScreenState
import com.example.playlistmaker.player.view_model.TrackViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class TrackFragment : Fragment() {

    private lateinit var binding: FragmentAudioplayerBinding
    private val viewModel by viewModel<TrackViewModel>()
    private var isClickAllowed = true
    private val clickDebounceDelay = 200L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAudioplayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })

        binding.rvPlaylist.adapter = PlaylistSmallAdapter(emptyList())
        binding.apToolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        viewModel.getScreenStateLiveData().observe(viewLifecycleOwner) { screenState ->
            when (screenState) {
                is TrackScreenState.Content -> {
                    loadTrackInfo(screenState)
                    updatePlayerUI(screenState.playerState)
                }

                else -> {
                }
            }
        }

        binding.apPlayAudioButton.setOnPlaybackToggleListener { shouldPlay ->
            if (!clickDebounce()) return@setOnPlaybackToggleListener
            val state = viewModel.getScreenStateLiveData().value as? TrackScreenState.Content
                ?: return@setOnPlaybackToggleListener

            when {
                shouldPlay && state.playerState.progress > 0 -> {
                    viewModel.resumePlayer()
                }
                shouldPlay -> {
                    viewModel.play()
                }
                else -> {
                    viewModel.pause()
                }
            }
        }

        binding.apAddToFavoritesButton.setOnClickListener {
            if (clickDebounce()) {
                lifecycleScope.launch {
                    viewModel.onFavoriteClicked()
                }
            }
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isHideable = true
            skipCollapsed = true
            isFitToContents = true
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            binding.overlay.visibility = View.GONE
                        }

                        BottomSheetBehavior.STATE_EXPANDED -> {
                            binding.overlay.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    binding.overlay.apply {
                        visibility = View.VISIBLE
                        alpha = slideOffset
                    }
                }
            })
        }

        binding.apAddToPlaylistButton.setOnClickListener {
            lifecycleScope.launch {
                (binding.rvPlaylist.adapter as PlaylistSmallAdapter).updateData(viewModel.getPlaylists())
            }
            binding.playlistsBottomSheet.visibility = View.VISIBLE
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.overlay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        (binding.rvPlaylist.adapter as PlaylistSmallAdapter).setOnItemClickListener { playlist ->
            var success = false
            if (clickDebounce()) {
                lifecycleScope.launch {
                    success = viewModel.addTrackToPlaylist(playlist.playlistId!!.toLong())
                    withContext(Dispatchers.Main) {
                        if (success) {
                            Toast.makeText(
                                requireContext(), "Добавлено в плейлист ${playlist.playlistName}", Toast.LENGTH_SHORT
                            ).show()
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                            binding.overlay.visibility = View.GONE
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Трек уже добавлен в плейлист ${playlist.playlistName}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        binding.createPlaylist.setOnClickListener {
            viewModel.pause()
            findNavController().navigate(R.id.action_track_to_create_playlist)
        }

    }

    private fun loadTrackInfo(screenState: TrackScreenState.Content) {
        Glide.with(requireContext()).load(screenState.track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg"))
            .transform(RoundedCorners(8)).placeholder(R.drawable.placeholder_full_size).into(binding.apTrackImage)

        binding.apTrackName.text = screenState.track.trackName
        binding.apTrackArtistName.text = screenState.track.artistName
        binding.apActualDuration.text = screenState.track.trackTime
        binding.apActualAlbum.text = screenState.track.collectionName
        binding.apActualYear.text = screenState.track.releaseDate
        binding.apActualGenre.text = screenState.track.primaryGenreName
        binding.apActualCountry.text = screenState.track.country
        if (screenState.track.isFavorite) {
            binding.apAddToFavoritesButton.setImageResource(R.drawable.in_favorites_button)
        } else {
            binding.apAddToFavoritesButton.setImageResource(R.drawable.add_to_favorites_button)
        }
    }

    private fun updatePlayerUI(playerState: PlayerState) {
        binding.apPlayingTime.text = playerState.currentTime
        binding.apPlayAudioButton.setPlaying(playerState.isPlaying)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            lifecycleScope.launch {
                delay(clickDebounceDelay)
                isClickAllowed = true
            }
        }
        return current
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.apPlayAudioButton.clearListener()
    }
}