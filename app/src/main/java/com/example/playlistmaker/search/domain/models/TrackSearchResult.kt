package com.example.playlistmaker.search.domain.models

data class TrackSearchResult(
    val tracks: List<Track> = emptyList(),
    val isError: Boolean
)
