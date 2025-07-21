package com.example.playlistmaker.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.FavoriteTrackInteractor
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.domain.models.PlaylistTrack
import com.example.playlistmaker.player.IAudioPlayerService
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TrackViewModel(
    tracksInteractor: TrackInteractor,
    private val favoriteTrackInteractor: FavoriteTrackInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val screenStateLiveData = MutableLiveData<TrackScreenState>(TrackScreenState.Loading)

    private var isPaused = false
    private val track: Track? = tracksInteractor.readTrackForAudioPlayer()

    private var audioService: IAudioPlayerService? = null

    init {
        val currentTrack = track

        viewModelScope.launch {
            if (currentTrack != null) {
                currentTrack.isFavorite = favoriteTrackInteractor.isTrackInFavorites(currentTrack.trackId!!)
                screenStateLiveData.postValue(TrackScreenState.Content(track!!))
            }
        }
    }

    val currentTrack: Track?
        get() = track

    fun bindService(service: IAudioPlayerService) {
        audioService = service
        audioService?.setObserver { playerState ->
            updatePlayerState { oldState ->
                oldState.copy(
                    isPlaying = playerState.isPlaying,
                    progress = playerState.progress,
                    currentTime = playerState.currentTime
                )
            }
        }
    }

    fun getScreenStateLiveData(): LiveData<TrackScreenState> = screenStateLiveData

    fun play() {
        audioService?.prepareAndPlay(track?.previewUrl)
        isPaused = false
    }

    fun pause() {
        audioService?.pause()
        isPaused = true
    }

    fun resumePlayer() {
        if (isPaused) {
            isPaused = false
            audioService?.resume()
        }
    }

    private fun updatePlayerState(update: (PlayerState) -> PlayerState) {
        val currentState = screenStateLiveData.value
        if (currentState is TrackScreenState.Content) {
            screenStateLiveData.postValue(currentState.copy(playerState = update(currentState.playerState)))
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioService = null
    }

    suspend fun onFavoriteClicked() {
        val currentState = screenStateLiveData.value
        if (currentState is TrackScreenState.Content) {
            val currentTrack = currentState.track
            val updatedTrack = if (currentTrack.isFavorite) {
                favoriteTrackInteractor.deleteTrackFromFavorites(currentTrack)
                currentTrack.copy(isFavorite = false)
            } else {
                favoriteTrackInteractor.addTrackToFavorites(currentTrack)
                currentTrack.copy(isFavorite = true)
            }
            screenStateLiveData.postValue(currentState.updateTrack(updatedTrack))
        }
    }

    suspend fun addTrackToPlaylist(playlistId: Long): Boolean {
        if (playlistInteractor.isTrackInPlaylist(playlistId, track?.trackId)) {
            return false
        } else {
            if (track != null) {
                playlistInteractor.insertPlaylistTrack(PlaylistTrack(playlistId, track.trackId))
                playlistInteractor.addTrackToPlaylist(track)
            }
            return true
        }
    }

    suspend fun getPlaylists(): List<Playlist> {
        return playlistInteractor.getPlaylists().first()
    }
}