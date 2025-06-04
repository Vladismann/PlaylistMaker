package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.domain.models.PlaylistTrack
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun createPlaylist(playlist: Playlist)

    suspend fun getPlaylists(): Flow<List<Playlist>>

    suspend fun isTrackInPlaylist(playlistId: Long?, trackId: Long?) : Boolean

    suspend fun insertPlaylistTrack(playlistTrackEntity: PlaylistTrack)
}