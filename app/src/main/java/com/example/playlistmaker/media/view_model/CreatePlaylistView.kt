package com.example.playlistmaker.media.view_model

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.models.Playlist

class CreatePlaylistView(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    suspend fun createPlaylist(name: String, description: String, imageUrl: String) {
        var playlist = Playlist(null, name, description, imageUrl, null)
        playlistInteractor.createPlaylist(playlist)
    }
}