package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.domain.models.PlaylistTrack
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepo: PlaylistRepo): PlaylistInteractor {

    override suspend fun createPlaylist(playlist: Playlist) {
        playlistRepo.createPlaylist(playlist)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepo.getPlaylists()
    }

    override suspend fun isTrackInPlaylist(playlistId: Long?, trackId: Long?): Boolean {
       return playlistRepo.isTrackInPlaylist(playlistId, trackId)
    }

    override suspend fun insertPlaylistTrack(playlistTrackEntity: PlaylistTrack) {
        playlistRepo.insertPlaylistTrack(playlistTrackEntity)
    }

    override suspend fun getPlaylist(playlistId: Long?): Playlist? {
        return playlistRepo.getPlaylist(playlistId)
    }
}