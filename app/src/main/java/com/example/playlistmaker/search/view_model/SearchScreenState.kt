package com.example.playlistmaker.search.view_model

import com.example.playlistmaker.search.domain.models.Track

sealed class SearchScreenState {
    data object Loading : SearchScreenState()
    data class Content(
        val tracks: List<Track>,
        val historyTracks: List<Track>,
        val query: String
    ) : SearchScreenState()

    data class Error(
        val showRefresh: Boolean
    ) : SearchScreenState()
}