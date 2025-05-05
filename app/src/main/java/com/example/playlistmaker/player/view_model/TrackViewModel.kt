package com.example.playlistmaker.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.TrackPlayer
import com.example.playlistmaker.player.domain.TrackPlayerInteractor
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrackViewModel(tracksInteractor: TrackInteractor, private val trackPlayer: TrackPlayerInteractor) : ViewModel() {

    private val screenStateLiveData = MutableLiveData<TrackScreenState>(TrackScreenState.Loading)
    private var updateTimeJob: Job? = null

    private var isPaused = false
    private var track: Track? = tracksInteractor.readTrackForAudioPlayer()

    init {
        if (track != null) {
            screenStateLiveData.postValue(TrackScreenState.Content(track!!))
        }
    }

    fun getScreenStateLiveData(): LiveData<TrackScreenState> = screenStateLiveData

    fun play() {
        if (!isPaused) {
            trackPlayer.preparePlayer(track?.previewUrl)
        }
        trackPlayer.setStatusObserver(object : TrackPlayer.StatusObserver {
            override fun onStop() {
                updatePlayerState { it.copy(isPlaying = false, progress = 0, currentTime = "0:00") }
                updateTimeJob?.cancel()
                isPaused = false
            }

            override fun onPlay() {
                trackPlayer.startPlayer()
                updatePlayerState { it.copy(isPlaying = true) }
                startUpdateTimeJob()
                isPaused = false
            }

            override fun onResume() {
                updatePlayerState { it.copy(isPlaying = true) }
                startUpdateTimeJob()
                isPaused = false
            }
        })
        trackPlayer.startPlayer()
    }

    fun pause() {
        trackPlayer.pausePlayer()
        updateTimeJob?.cancel()
        updatePlayerState { it.copy(isPlaying = false, progress = trackPlayer.getCurrentPosition()) }
        isPaused = true
    }

    fun resumePlayer() {
        if (isPaused) {
            isPaused = false
            trackPlayer.resumePlayer()
            startUpdateTimeJob()
            updatePlayerState { it.copy(isPlaying = true, progress = trackPlayer.getCurrentPosition()) }
        }
    }

    private fun updatePlayerState(update: (PlayerState) -> PlayerState) {
        val currentState = screenStateLiveData.value
        if (currentState is TrackScreenState.Content) {
            screenStateLiveData.postValue(currentState.copy(playerState = update(currentState.playerState)))
        }
    }

    private fun startUpdateTimeJob() {
        updateTimeJob?.cancel()
        updateTimeJob = viewModelScope.launch(Dispatchers.Main) {
            while (true) {
                val currentPosition = trackPlayer.getCurrentPosition()
                val timeString = SimpleDateFormat("m:ss", Locale.getDefault()).format(Date(currentPosition.toLong()))
                updatePlayerState { it.copy(currentTime = timeString) }

                delay(500)
            }
        }
    }

    override fun onCleared() {
        updateTimeJob?.cancel()
        trackPlayer.releasePlayer()
    }
}