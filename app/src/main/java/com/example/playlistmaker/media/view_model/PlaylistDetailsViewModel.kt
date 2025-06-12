package com.example.playlistmaker.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class PlaylistDetailsViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val screenStateLiveData = MutableLiveData<PlaylistDetailsScreenState>(PlaylistDetailsScreenState.Loading)

    fun getScreenStateLiveData(): LiveData<PlaylistDetailsScreenState> = screenStateLiveData

    val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    fun init(playlistId: Long) {
        viewModelScope.launch {
            if (playlistId != -1L) {
                var playlist = playlistInteractor.getPlaylist(playlistId)
                var tracks = playlistInteractor.playlistTracks(playlistId).first()
                var totalTime = 0L
                var totalCount = 0
                if (tracks.isNotEmpty()) {
                    tracks.map { track ->
                        totalTime = totalTime.plus(dateFormat.parse(track.trackTime).time)
                    }
                    totalCount = tracks.size
                    totalTime = totalTime / 60000
                }
                screenStateLiveData.postValue(PlaylistDetailsScreenState.Content(playlist, tracks, totalTime, totalCount))
            }
        }
    }
}