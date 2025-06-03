package com.example.playlistmaker.media.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistTrack(playlistTrackEntity: PlaylistTrackEntity) : Long

    @Query("SELECT trackId FROM playlist_track_table WHERE playlistId = :playlistId")
    suspend fun getPlaylistTracksIds(playlistId: Long?): List<Long>

    @Query("SELECT EXISTS(SELECT 1 FROM playlist_track_table WHERE playlistId = :playlistId AND trackId = :trackId)")
    suspend fun isTracksInPlayList(playlistId: Long?, trackId: Long?): Boolean
}