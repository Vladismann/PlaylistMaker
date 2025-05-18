package com.example.playlistmaker.media.data.converters

import com.example.playlistmaker.media.data.db.TrackEntity
import com.example.playlistmaker.search.domain.models.Track

class TrackDbConverter {

    fun map(track: Track): TrackEntity {
        return TrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTime,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl)
    }

    fun map(track: TrackEntity): Track {
        return Track(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTime,
            track.pictureUrl,
            track.collectionName,
            track.releaseDate,
            track.genreName,
            track.country,
            track.previewUrl)
    }
}