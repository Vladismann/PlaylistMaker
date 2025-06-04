package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.media.data.converters.PlaylistDbConverter
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.PlaylistEntity
import com.example.playlistmaker.media.data.db.PlaylistTrackEntity
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.domain.models.PlaylistTrack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepoImpl(private val appDatabase: AppDatabase, private val playlistDbConverter: PlaylistDbConverter) :
    PlaylistRepo {

    override suspend fun createPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getPlayLists().map { playlist ->
            convertFromPlaylistEntity(playlist)
        }
    }

    override suspend fun isTrackInPlaylist(playlistId: Long?, trackId: Long?): Boolean {
        return appDatabase.playlistTrackDao().isTracksInPlayList(playlistId, trackId)
    }

    override suspend fun insertPlaylistTrack(playlistTrackEntity: PlaylistTrack) {
        appDatabase.playlistTrackDao()
            .insertPlaylistTrack(PlaylistTrackEntity(null, playlistTrackEntity.playlistId, playlistTrackEntity.trackId))
    }

    private suspend fun convertFromPlaylistEntity(playlist: List<PlaylistEntity>): List<Playlist> {
        return playlist.map { playlist ->
            var tracks = appDatabase.playlistTrackDao().getPlaylistTracksIds(playlist.playlistId)
            playlistDbConverter.map(playlist, tracks)
        }
    }
}