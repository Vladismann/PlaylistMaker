package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.TrackSearchResult
import kotlinx.coroutines.flow.Flow

interface TrackInteractor {
    fun searchTracks(expression: String) : Flow<TrackSearchResult>

    fun getTracksHistory(): List<Track>

    fun writeTracksHistory(tracks: List<Track>)

    fun clearTracksHistory()

    fun writeTrackForAudioPlayer(track: Track)

    fun readTrackForAudioPlayer(): Track?
}