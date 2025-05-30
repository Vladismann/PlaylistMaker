package com.example.playlistmaker.player.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioplayerBinding
import com.example.playlistmaker.player.view_model.PlayerState
import com.example.playlistmaker.player.view_model.TrackScreenState
import com.example.playlistmaker.player.view_model.TrackViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TrackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioplayerBinding
    private val viewModel by viewModel<TrackViewModel>()
    private var isClickAllowed = true
    private val clickDebounceDelay = 200L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        viewModel.getScreenStateLiveData().observe(this) { screenState ->
            when (screenState) {
                is TrackScreenState.Content -> {
                    loadTrackInfo(screenState)
                    updatePlayerUI(screenState.playerState)
                }

                else -> {
                }
            }
        }

        binding.apPlayAudioButton.setOnClickListener {
            if (clickDebounce()) {
                val screenState = viewModel.getScreenStateLiveData().value
                if (screenState is TrackScreenState.Content) {
                    if (screenState.playerState.isPlaying) {
                        viewModel.pause()
                    } else {
                        if (screenState.playerState.progress > 0) {
                            viewModel.resumePlayer()
                        } else {
                            viewModel.play()
                        }
                    }
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
    }

    private fun loadTrackInfo(screenState: TrackScreenState.Content) {
        Glide.with(applicationContext).load(screenState.track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg"))
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
        if (playerState.isPlaying) {
            binding.apPlayAudioButton.setBackgroundResource(R.drawable.pause_audio_button)
        } else {
            binding.apPlayAudioButton.setBackgroundResource(R.drawable.play_audio_button)
        }
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
}