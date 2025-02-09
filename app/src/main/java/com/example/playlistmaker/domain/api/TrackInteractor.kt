package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.TrackSearchResult

interface TrackInteractor {
    fun searchTracks(expression: String, consumer: TrackConsumer)

     interface TrackConsumer {
        fun consume(actualResult: TrackSearchResult)
    }
}