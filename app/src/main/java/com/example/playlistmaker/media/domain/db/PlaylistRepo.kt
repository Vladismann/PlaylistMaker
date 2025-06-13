package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.domain.models.PlaylistTrack
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepo {

    suspend fun createPlaylist(playlist: Playlist)

    suspend fun getPlaylists(): Flow<List<Playlist>>

    suspend fun isTrackInPlaylist(playlistId: Long?, trackId: Long?) : Boolean

    suspend fun insertPlaylistTrack(playlistTrackEntity : PlaylistTrack)

    suspend fun getPlaylist(playlistId: Long?): Playlist?

    suspend fun deletePlaylist(playlistId: Long?)

    suspend fun playlistTracks(playlistId: Long?): Flow<List<Track>>

    suspend fun addTrackToPlaylist(track: Track)

    suspend fun deleteTrackFromPlaylist(playlistId: Long?, trackId: Long?)
}