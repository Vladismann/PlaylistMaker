package com.example.playlistmaker.domain.api

import android.content.Context
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.TrackSearchResult

interface TrackInteractor {
    fun searchTracks(expression: String, consumer: TrackConsumer)

    fun getTracksHistory(context: Context): List<Track>

    fun writeTracksHistory(tracks: List<Track>, context: Context)

    fun clearTracksHistory()

    fun writeTrackForAudioPlayer(track: Track)

    fun readTrackForAudioPlayer(): Track?

     interface TrackConsumer {
        fun consume(actualResult: TrackSearchResult)
    }
}