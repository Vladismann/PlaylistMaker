package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTrackInteractor {

    fun favoriteTracks(): Flow<List<Track>>

    suspend fun addTrackToFavorites(track: Track)

    suspend fun deleteTrackFromFavorites(track: Track)
}