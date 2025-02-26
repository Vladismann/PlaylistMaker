package com.example.playlistmaker.player.view_model

import com.example.playlistmaker.search.domain.models.Track

sealed class TrackScreenState {
    data object Loading: TrackScreenState()
    data class Content(
        val track: Track,
    ): TrackScreenState()
}