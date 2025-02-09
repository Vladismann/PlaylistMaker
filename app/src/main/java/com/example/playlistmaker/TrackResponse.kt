package com.example.playlistmaker

import com.example.playlistmaker.domain.models.Track

data class TrackResponse(
    val resultCount: Int,
    val results: MutableList<Track>
)