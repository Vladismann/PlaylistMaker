package com.example.playlistmaker.media.view_model

import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track

sealed class PlaylistDetailsScreenState {
    data object Loading : PlaylistDetailsScreenState()
    data class Content(
        val playlist: Playlist?,
        val tracks: List<Track>,
        val totalTime: Long,
        val trackCount: Int
    ) : PlaylistDetailsScreenState()

}