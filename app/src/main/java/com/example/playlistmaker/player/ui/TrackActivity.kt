package com.example.playlistmaker.player.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioplayerBinding
import com.example.playlistmaker.player.view_model.TrackScreenState
import com.example.playlistmaker.player.view_model.TrackViewModel

class TrackActivity : ComponentActivity() {

    private lateinit var binding: ActivityAudioplayerBinding
    private val viewModel by viewModels<TrackViewModel> {
        TrackViewModel.getViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        viewModel.getScreenStateLiveData().observe(this) { screenState ->
            if (screenState is TrackScreenState.Content) {
                loadTrackInfo(screenState)
            }
        }

        viewModel.getPlayStatusLiveData().observe(this) { playStatus ->
            if (playStatus.isPlaying) {
                binding.apPlayAudioButton.setBackgroundResource(R.drawable.pause_audio_button)
            } else {
                binding.apPlayAudioButton.setBackgroundResource(R.drawable.play_audio_button)
            }
        }

        viewModel.getCurrentTimeLiveData().observe(this) { currentTime ->
            binding.apPlayingTime.text = currentTime
        }

        binding.apPlayAudioButton.setOnClickListener {
            val playStatus = viewModel.getPlayStatusLiveData().value
            if (playStatus?.isPlaying == true) {
                viewModel.pause()  // Если воспроизведение идет, ставим на паузу
            } else {
                if ((playStatus?.progress ?: 0) > 0) {
                    viewModel.resumePlayer()  // Если прогресс больше 0, возобновляем воспроизведение
                } else {
                    viewModel.play()  // Если трек не был запущен, начинаем с начала
                }
            }
        }
    }

    private fun loadTrackInfo(screenState: TrackScreenState.Content) {
        Glide.with(applicationContext)
            .load(screenState.track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.placeholder_full_size).into(binding.apTrackImage)

        binding.apTrackName.text = screenState.track.trackName
        binding.apTrackArtistName.text = screenState.track.artistName
        binding.apActualDuration.text = screenState.track.trackTime
        binding.apActualAlbum.text = screenState.track.collectionName
        binding.apActualYear.text = screenState.track.releaseDate
        binding.apActualGenre.text = screenState.track.primaryGenreName
        binding.apActualCountry.text = screenState.track.country
    }
}