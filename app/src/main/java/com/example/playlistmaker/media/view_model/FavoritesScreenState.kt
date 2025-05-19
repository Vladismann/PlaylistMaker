package com.example.playlistmaker.media.view_model

import com.example.playlistmaker.search.domain.models.Track

sealed class FavoritesScreenState {

    data object Loading : FavoritesScreenState()
    data class Content(
        val tracks: List<Track>,
    ) : FavoritesScreenState()
}