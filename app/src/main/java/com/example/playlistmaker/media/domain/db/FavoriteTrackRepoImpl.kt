package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.media.data.converters.TrackDbConverter
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.TrackEntity
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTrackRepoImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter,
) : FavoriteTrackRepo {

    override fun favoriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.favoriteTrackDao().getTracks()
        emit(convertFromTrackEntity(tracks))
    }

    override suspend fun addTrackToFavorite(track: Track) {
        appDatabase.favoriteTrackDao().insertFavoriteTrack(convertFromTrackEntity(track))
    }

    override suspend fun deleteTrackFromFavorite(track: Track) {
        appDatabase.favoriteTrackDao().deleteTrackEntity(convertFromTrackEntity(track))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConverter.map(track) }
    }

    private fun convertFromTrackEntity(track: Track): TrackEntity {
        return trackDbConverter.map(track)
    }
}