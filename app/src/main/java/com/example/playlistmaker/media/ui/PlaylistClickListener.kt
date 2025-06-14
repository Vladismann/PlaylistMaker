package com.example.playlistmaker.media.ui

import com.example.playlistmaker.media.domain.models.Playlist


fun interface PlaylistClickListener {
    fun onItemClick(playlist: Playlist)
}