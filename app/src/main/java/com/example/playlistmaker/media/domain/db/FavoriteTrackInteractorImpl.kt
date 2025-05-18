package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTrackInteractorImpl (
    private val historyRepository: FavoriteTrackRepo
) : FavoriteTrackInteractor {

    override fun favoriteTracks(): Flow<List<Track>> {
        return historyRepository.favoriteTracks()
    }

    override suspend fun addTrackToFavorites(track: Track) {
        historyRepository.addTrackToFavorite(track)
    }

    override suspend fun deleteTrackFromFavorites(track: Track) {
        historyRepository.deleteTrackFromFavorite(track)
    }

}