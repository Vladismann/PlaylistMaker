package com.example.playlistmaker.media.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteTrackDao {

    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteTrack(trackEntity: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun deleteTrackEntity(trackEntity: TrackEntity)

    @Query("SELECT * FROM favorite_track_table")
    suspend fun getTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM favorite_track_table")
    suspend fun getTracksIds(): List<Long>
}