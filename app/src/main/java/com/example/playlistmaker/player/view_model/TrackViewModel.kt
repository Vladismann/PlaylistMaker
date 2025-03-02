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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrackViewModel(
    tracksInteractor: TrackInteractor, private val trackPlayer: TrackPlayerInteractor
) : ViewModel() {

    private val screenStateLiveData = MutableLiveData<TrackScreenState>(TrackScreenState.Loading)
    private val playStatusLiveData = MutableLiveData<PlayStatus>()
    private val currentTimeLiveData = MutableLiveData<String>()
    private val handler = Handler(Looper.getMainLooper())

    private var isPaused = false

    init {
        val track = tracksInteractor.readTrackForAudioPlayer()
        if (track != null) {
            screenStateLiveData.postValue(TrackScreenState.Content(track))
        }
    }

    fun getScreenStateLiveData(): LiveData<TrackScreenState> = screenStateLiveData
    fun getPlayStatusLiveData(): LiveData<PlayStatus> = playStatusLiveData
    fun getCurrentTimeLiveData(): LiveData<String> = currentTimeLiveData

    companion object {
        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val interactor = Creator.provideTrackInteractor(context)
                val player = Creator.provideTrackPlayerInteractor()

                TrackViewModel(interactor, player)
            }
        }
    }

    fun play(url: String) {
        if (!isPaused) {
            trackPlayer.preparePlayer(url)
        }
        trackPlayer.setStatusObserver(object : TrackPlayer.StatusObserver {
            override fun onStop() {
                playStatusLiveData.postValue(
                    getCurrentPlayStatus().copy(
                        progress = 0, isPlaying = false
                    )
                )
                handler.removeCallbacks(updateTimeRunnable)
                currentTimeLiveData.postValue("0:00")
                isPaused = false
            }

            override fun onPlay() {
                trackPlayer.startPlayer()
                playStatusLiveData.postValue(getCurrentPlayStatus().copy(isPlaying = true))
                handler.post(updateTimeRunnable)
                isPaused = false
            }

            override fun onResume() {
                playStatusLiveData.postValue(getCurrentPlayStatus().copy(isPlaying = true))
                handler.post(updateTimeRunnable)
                isPaused = false
            }
        })
        trackPlayer.startPlayer()
    }

    fun pause() {
        trackPlayer.pausePlayer()
        handler.removeCallbacks(updateTimeRunnable)
        playStatusLiveData.postValue(
            getCurrentPlayStatus().copy(
                progress = trackPlayer.getCurrentPosition(), isPlaying = false
            )
        )
        isPaused = true
    }

    fun resumePlayer() {
        if (isPaused) {
            isPaused = false
            trackPlayer.resumePlayer()
            handler.post(updateTimeRunnable)
            playStatusLiveData.postValue(
                getCurrentPlayStatus().copy(
                    progress = trackPlayer.getCurrentPosition(), isPlaying = true
                )
            )
        }
    }

    private fun getCurrentPlayStatus(): PlayStatus {
        return playStatusLiveData.value ?: PlayStatus(
            progress = trackPlayer.getCurrentPosition(), isPlaying = false
        )
    }

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            val currentPosition = trackPlayer.getCurrentPosition()
            val timeString =
                SimpleDateFormat("m:ss", Locale.getDefault()).format(Date(currentPosition.toLong()))
            currentTimeLiveData.postValue(timeString)

            if (playStatusLiveData.value?.isPlaying == true) {
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCleared() {
        handler.removeCallbacks(updateTimeRunnable)
        trackPlayer.releasePlayer()
    }
}