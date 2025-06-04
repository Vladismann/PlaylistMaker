package com.example.playlistmaker.media.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val _screenState = MutableStateFlow<PlaylistScreenState>(PlaylistScreenState.Loading)
    val screenState: StateFlow<PlaylistScreenState> = _screenState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadPlaylists()
    }

    fun loadPlaylists() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            playlistInteractor.getPlaylists().collect { playlists ->
                _screenState.value = if (playlists.isEmpty()) {
                    PlaylistScreenState.Content(emptyList())
                } else {
                    PlaylistScreenState.Content(playlists)
                }
            }
        }
    }
}