package com.example.playlistmaker.player.view_model

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.data.TrackPlayer
import com.example.playlistmaker.player.domain.TrackPlayerInteractor
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrackViewModel(tracksInteractor: TrackInteractor, private val trackPlayer: TrackPlayerInteractor) : ViewModel() {

    private val screenStateLiveData = MutableLiveData<TrackScreenState>(TrackScreenState.Loading)
    private val handler = Handler(Looper.getMainLooper())

    private var isPaused = false
    private var track: Track? = tracksInteractor.readTrackForAudioPlayer()

    init {
        if (track != null) {
            screenStateLiveData.postValue(TrackScreenState.Content(track!!))
        }
    }

    companion object {
        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val interactor = Creator.provideTrackInteractor(context)
                val player = Creator.provideTrackPlayerInteractor()

                TrackViewModel(interactor, player)
            }
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
                handler.removeCallbacks(updateTimeRunnable)
                isPaused = false
            }

            override fun onPlay() {
                trackPlayer.startPlayer()
                updatePlayerState { it.copy(isPlaying = true) }
                handler.post(updateTimeRunnable)
                isPaused = false
            }

            override fun onResume() {
                updatePlayerState { it.copy(isPlaying = true) }
                handler.post(updateTimeRunnable)
                isPaused = false
            }
        })
        trackPlayer.startPlayer()
    }

    fun pause() {
        trackPlayer.pausePlayer()
        handler.removeCallbacks(updateTimeRunnable)
        updatePlayerState { it.copy(isPlaying = false, progress = trackPlayer.getCurrentPosition()) }
        isPaused = true
    }

    fun resumePlayer() {
        if (isPaused) {
            isPaused = false
            trackPlayer.resumePlayer()
            handler.post(updateTimeRunnable)
            updatePlayerState { it.copy(isPlaying = true, progress = trackPlayer.getCurrentPosition()) }
        }
    }

    private fun updatePlayerState(update: (PlayerState) -> PlayerState) {
        val currentState = screenStateLiveData.value
        if (currentState is TrackScreenState.Content) {
            screenStateLiveData.postValue(currentState.copy(playerState = update(currentState.playerState)))
        }
    }

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            val currentPosition = trackPlayer.getCurrentPosition()
            val timeString = SimpleDateFormat("m:ss", Locale.getDefault()).format(Date(currentPosition.toLong()))
            updatePlayerState { it.copy(currentTime = timeString) }

            val currentState = screenStateLiveData.value
            if (currentState is TrackScreenState.Content && currentState.playerState.isPlaying) {
                handler.postDelayed(this, 500)
            }
        }
    }

    override fun onCleared() {
        handler.removeCallbacks(updateTimeRunnable)
        trackPlayer.releasePlayer()
    }
}