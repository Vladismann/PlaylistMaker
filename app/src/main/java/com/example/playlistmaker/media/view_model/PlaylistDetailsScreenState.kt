package com.example.playlistmaker.media.view_model

import com.example.playlistmaker.media.domain.models.Playlist

sealed class PlaylistDetailsScreenState {
    data object Loading : PlaylistDetailsScreenState()
    data class Content(
        val playlist: Playlist?
    ) : PlaylistDetailsScreenState()

}