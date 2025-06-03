package com.example.playlistmaker.player.ui

import com.example.playlistmaker.media.domain.models.Playlist


fun interface OnSmallPlaylistClickListener {
    fun onItemClick(playlist: Playlist)
}