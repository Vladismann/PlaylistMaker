package com.example.playlistmaker.media.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteTrack(trackEntity: TrackEntity) : Long

    @Delete
    suspend fun deleteTrackEntity(trackEntity: TrackEntity) : Int

    @Query("SELECT * FROM favorite_track_table")
    fun getTracks(): Flow<List<TrackEntity>>

    @Query("SELECT trackId FROM favorite_track_table")
    suspend fun getTracksIds(): List<Long>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_track_table WHERE trackId = :trackId)")
    suspend fun isTrackInFavorites(trackId: Long): Boolean
}