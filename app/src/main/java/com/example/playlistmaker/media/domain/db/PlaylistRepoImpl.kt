package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.media.data.converters.PlaylistDbConverter
import com.example.playlistmaker.media.data.converters.TrackDbConverter
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.PlaylistEntity
import com.example.playlistmaker.media.data.db.PlaylistTrackEntity
import com.example.playlistmaker.media.data.db.TrackPlaylistEntity
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.domain.models.PlaylistTrack
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepoImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val trackDbConverter: TrackDbConverter
) : PlaylistRepo {

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

    override suspend fun getPlaylist(playlistId: Long?): Playlist? {
        var tracks = appDatabase.playlistTrackDao().getPlaylistTracksIds(playlistId)
        return playlistDbConverter.map(appDatabase.playlistDao().getPlayList(playlistId), tracks)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().deletePlayList(playlistDbConverter.map(playlist))
    }

    override suspend fun playlistTracks(playlistId: Long?): Flow<List<Track>> {
        var tracksIds = appDatabase.playlistTrackDao().getPlaylistTracksIds(playlistId)
        return appDatabase.trackPlaylistDao().getPlaylistTracks(tracksIds).map { tracks -> convertFromTrackEntity(tracks) }
    }


    override suspend fun addTrackToPlaylist(track: Track) {
        appDatabase.trackPlaylistDao().insertPlaylistTrack(trackDbConverter.mapForPlaylist(track))
    }

    override suspend fun deleteTrackFromPlaylist(playlistId: Long?, trackId: Long?) {
        appDatabase.playlistTrackDao().deletePlaylistTrackEntity(playlistId, trackId)
        if (!appDatabase.playlistTrackDao().isTrackHaveAnyPlaylist(trackId)) {
            appDatabase.trackPlaylistDao().deleteTrackEntity(trackId)
        }
    }

    private suspend fun convertFromPlaylistEntity(playlist: List<PlaylistEntity>): List<Playlist> {
        return playlist.map { playlist ->
            var tracks = appDatabase.playlistTrackDao().getPlaylistTracksIds(playlist.playlistId)
            playlistDbConverter.map(playlist, tracks)
        }
    }

    private fun convertFromTrackEntity(tracks: List<TrackPlaylistEntity>): List<Track> {
        return tracks.map { track -> trackDbConverter.mapForPlaylist(track) }
    }
}