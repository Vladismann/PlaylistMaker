package com.example.playlistmaker.search.view_model

import com.example.playlistmaker.search.domain.models.Track

sealed class SearchScreenState {
    data object Loading : SearchScreenState()
    data class Content(
        val tracks: List<Track>,
    ) : SearchScreenState()

    data object Error : SearchScreenState()
}