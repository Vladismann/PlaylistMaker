package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.TrackSearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {

    override fun searchTracks(expression: String): Flow<TrackSearchResult> {
        return repository.searchTracks(expression)
            .map { tracks -> TrackSearchResult(tracks, false) }
            .catch { e ->
                e.printStackTrace()
                emit(TrackSearchResult(emptyList(), true))
            }
            .flowOn(Dispatchers.IO)
    }

    override fun getTracksHistory(): List<Track> {
        return repository.getTrackHistory()
    }

    override fun writeTracksHistory(tracks: List<Track>) {
        repository.saveTrackHistory(tracks)
    }

    override fun clearTracksHistory() {
        repository.clearTrackHistory()
    }

    override fun writeTrackForAudioPlayer(track: Track) {
        repository.saveTrackForAudioPlayer(track)
    }

    override fun readTrackForAudioPlayer(): Track? {
        return repository.getTrackForAudioPlayer()
    }

}