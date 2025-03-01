package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.TrackSearchResult
import java.util.concurrent.Executors

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TrackInteractor.TrackConsumer) {
        executor.execute {
            try {
                val actualTracks = repository.searchTracks(expression)
                consumer.consume(TrackSearchResult(actualTracks, false))
            } catch (e: Exception) {
                e.printStackTrace()
                consumer.consume(TrackSearchResult(emptyList(), true))
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