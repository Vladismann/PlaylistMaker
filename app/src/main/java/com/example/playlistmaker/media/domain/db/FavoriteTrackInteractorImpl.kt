package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTrackInteractorImpl (
    private val favoriteRepo: FavoriteTrackRepo
) : FavoriteTrackInteractor {

    override fun favoriteTracks(): Flow<List<Track>> {
        return favoriteRepo.favoriteTracks()
    }

    override suspend fun addTrackToFavorites(track: Track) {
        favoriteRepo.addTrackToFavorite(track)
    }

    override suspend fun deleteTrackFromFavorites(track: Track) {
        favoriteRepo.deleteTrackFromFavorite(track)
    }

    override suspend fun isTrackInFavorites(trackId: Long): Boolean {
        return favoriteRepo.isTrackInFavorites(trackId)
    }

}