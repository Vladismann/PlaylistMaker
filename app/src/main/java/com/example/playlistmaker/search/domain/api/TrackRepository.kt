package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface TrackRepository {
    fun searchTracks(expression: String): List<Track>

    fun saveTrackHistory(tracks: List<Track>)

    fun getTrackHistory(): List<Track>

    fun clearTrackHistory()

    fun saveTrackForAudioPlayer(track: Track)

    fun getTrackForAudioPlayer(): Track?
}