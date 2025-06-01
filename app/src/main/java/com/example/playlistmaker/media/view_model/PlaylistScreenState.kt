package com.example.playlistmaker.media.view_model

import com.example.playlistmaker.media.domain.models.Playlist

sealed class PlaylistScreenState {

    data object Loading : PlaylistScreenState()
    data class Content(
        val tracks: List<Playlist>,
    ) : PlaylistScreenState()
}