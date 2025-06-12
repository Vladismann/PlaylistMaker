package com.example.playlistmaker.media.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_playlist_table")
data class TrackPlaylistEntity(@PrimaryKey val trackId: Long?,
                               val trackName: String?,
                               val artistName: String?,
                               val trackTime: String?,
                               val pictureUrl: String?,
                               val collectionName: String?,
                               val releaseDate: String?,
                               val genreName: String?,
                               val country: String?,
                               val previewUrl: String?,
                               @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP") val createdAt: Long = System.currentTimeMillis())