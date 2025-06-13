package com.example.playlistmaker.media.ui

import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.view_model.CreatePlaylistUiState
import com.example.playlistmaker.media.view_model.CreatePlaylistViewModel
import kotlinx.coroutines.launch

class EditPlaylistViewModel(private val plInteractor: PlaylistInteractor) : CreatePlaylistViewModel(plInteractor) {

    private var actualPlaylistId = 0L
    var actualImageUri = ""

    fun init (playlistId: Long) {
        if (playlistId != -1L) {
            viewModelScope.launch {
                actualPlaylistId = playlistId
                var playlist = plInteractor.getPlaylist(playlistId)
                if (playlist != null) {
                    if (!playlist.playlistImageUrl.isNullOrBlank()) {
                        actualImageUri = playlist.playlistImageUrl!!
                    }
                    val current = _uiState.value ?: CreatePlaylistUiState()
                    _uiState.value = current.copy(playlist.playlistName, playlist.playlistDescr!!, playlist.playlistImageUrl!!, true)
                }
            }
        }
    }

    override suspend fun createPlaylist() {
        val state = _uiState.value ?: return
        val playlist = Playlist(actualPlaylistId, state.name, state.description, state.imagePath, null)
        plInteractor.createPlaylist(playlist)
    }
}