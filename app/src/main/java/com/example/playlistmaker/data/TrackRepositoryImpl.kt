package com.example.playlistmaker.data

import com.example.playlistmaker.data.network.TrackSearchRequest
import com.example.playlistmaker.data.network.TrackSearchResponse
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {
    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        if (response.resultCode == 200) {
            return (response as TrackSearchResponse).results.mapNotNull { track ->
                //у аудиокниг нет trackId, фильтруем только треки без аудиокниг
                Track(
                    track.trackId,
                    track.trackName,
                    track.artistName,
                    track.trackTimeMillis,
                    track.artworkUrl100,
                    track.collectionName,
                    track.releaseDate,
                    track.primaryGenreName,
                    track.country,
                    track.previewUrl,
                )
            }
        }
        if (response.resultCode == 400 || response.resultCode == 0 || response.resultCode == 503) {
            throw Exception()
        } else {
            return emptyList()
        }
    }
}