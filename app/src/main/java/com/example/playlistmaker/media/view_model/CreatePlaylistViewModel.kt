package com.example.playlistmaker.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.models.Playlist

class CreatePlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val _uiState = MutableLiveData(CreatePlaylistUiState())
    val uiState: LiveData<CreatePlaylistUiState> = _uiState

    fun onNameChanged(name: String) {
        val current = _uiState.value ?: CreatePlaylistUiState()
        _uiState.value = current.copy(
            name = name,
            isCreateEnabled = name.isNotEmpty()
        )
    }

    fun onDescriptionChanged(description: String) {
        val current = _uiState.value ?: CreatePlaylistUiState()
        _uiState.value = current.copy(description = description)
    }

    fun onImagePathChanged(path: String) {
        val current = _uiState.value ?: CreatePlaylistUiState()
        _uiState.value = current.copy(imagePath = path)
    }

    suspend fun createPlaylist() {
        val state = _uiState.value ?: return
        val playlist = Playlist(null, state.name, state.description, state.imagePath, null)
        playlistInteractor.createPlaylist(playlist)
    }
}