package com.example.playlistmaker.media.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.FavoriteTrackInteractor
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(private val favoriteTrackInteractor: FavoriteTrackInteractor,
                         private val trackInteractor: TrackInteractor) : ViewModel() {

    private val _screenState = MutableStateFlow<FavoritesScreenState>(FavoritesScreenState.Loading)
    val screenState: StateFlow<FavoritesScreenState> = _screenState.asStateFlow()

    init {
        viewModelScope.launch {
            favoriteTrackInteractor.favoriteTracks().collect { tracks ->
                _screenState.value = if (tracks.isEmpty()) {
                    FavoritesScreenState.Content(emptyList())
                } else {
                    FavoritesScreenState.Content(tracks)
                }
            }
        }
    }

    fun saveForAudioPlayer(track: Track) {
        trackInteractor.writeTrackForAudioPlayer(track)
    }
}