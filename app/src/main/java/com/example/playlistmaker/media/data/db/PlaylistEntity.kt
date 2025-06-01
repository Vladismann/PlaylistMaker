package com.example.playlistmaker.media.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(@PrimaryKey val playlistId: Long?,
                          val playlistName: String,
                          val playlistDescr: String?,
                          val playlistImageUrl: String?)