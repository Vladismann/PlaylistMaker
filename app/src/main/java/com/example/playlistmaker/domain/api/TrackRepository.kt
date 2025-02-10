package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TrackRepository {
    fun searchTracks(expression: String): List<Track>

    fun saveTrackHistory(tracks: Array<Track>)

    fun getTrackHistory(): List<Track>

    fun clearTrackHistory()

    fun saveTrackForAudioPlayer(track: Track)

    fun getTrackForAudioPlayer(): Track?
}