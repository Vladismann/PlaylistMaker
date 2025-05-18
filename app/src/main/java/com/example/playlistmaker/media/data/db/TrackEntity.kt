package com.example.playlistmaker.media.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_track_table")
data class TrackEntity(
    @PrimaryKey
    val trackId: Long?,
    val trackName: String?,
    val artistName: String?,
    val trackTime: String?,
    val pictureUrl: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val genreName: String?,
    val country: String?,
    val previewUrl: String?
)