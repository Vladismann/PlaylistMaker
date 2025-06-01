package com.example.playlistmaker.media.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_track_table")
class PlaylistTrackEntity(@PrimaryKey val id: Long?,
                          val playlistId: Long?,
                          val trackId: Long?)