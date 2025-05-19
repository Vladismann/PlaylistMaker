package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTrackRepo {

    fun favoriteTracks(): Flow<List<Track>>

    suspend fun addTrackToFavorite(track: Track)

    suspend fun deleteTrackFromFavorite(track: Track)

    suspend fun isTrackInFavorites(trackId: Long): Boolean
}