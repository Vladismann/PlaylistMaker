package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.TrackSearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {

    override suspend fun searchTracks(expression: String): TrackSearchResult {
        return withContext(Dispatchers.IO) {
            try {
                val actualTracks = repository.searchTracks(expression)
                TrackSearchResult(actualTracks, false)
            } catch (e: Exception) {
                e.printStackTrace()
                TrackSearchResult(emptyList(), true)
            }
        }
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