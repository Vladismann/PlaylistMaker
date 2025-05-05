package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.TrackSearchResult

interface TrackInteractor {
    suspend fun searchTracks(expression: String) : TrackSearchResult

    fun getTracksHistory(): List<Track>

    fun writeTracksHistory(tracks: List<Track>)

    fun clearTracksHistory()

    fun writeTrackForAudioPlayer(track: Track)

    fun readTrackForAudioPlayer(): Track?
}