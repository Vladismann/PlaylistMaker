package com.example.playlistmaker.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class PlaylistDetailsViewModel(private val playlistInteractor: PlaylistInteractor, private val trackInteractor: TrackInteractor) : ViewModel() {

    private val screenStateLiveData = MutableLiveData<PlaylistDetailsScreenState>(PlaylistDetailsScreenState.Loading)

    fun getScreenStateLiveData(): LiveData<PlaylistDetailsScreenState> = screenStateLiveData

    val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    var actualPlaylistId = 0L

    fun updateDetails(playlistId: Long) {
        viewModelScope.launch {
            if (playlistId != -1L) {
                actualPlaylistId = playlistId
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

    fun saveForAudioPlayer(track: Track) {
        trackInteractor.writeTrackForAudioPlayer(track)
    }

    fun deleteTrackFromPlaylist(trackId: Long?) {
        viewModelScope.launch {
            playlistInteractor.deleteTrackFromPlaylist(actualPlaylistId, trackId)
            updateDetails(actualPlaylistId)
        }
    }
}