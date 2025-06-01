package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepo: PlaylistRepo): PlaylistInteractor {

    override suspend fun createPlaylist(playlist: Playlist) {
        playlistRepo.createPlaylist(playlist)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepo.getPlaylists()
    }
}