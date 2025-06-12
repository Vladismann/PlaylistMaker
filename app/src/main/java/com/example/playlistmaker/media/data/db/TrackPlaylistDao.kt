package com.example.playlistmaker.media.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackPlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistTrack(trackEntity: TrackPlaylistEntity) : Long

    @Delete
    suspend fun deleteTrackEntity(trackEntity: TrackPlaylistEntity) : Int

    @Query("SELECT * FROM track_playlist_table WHERE trackId IN (:trackIds) ORDER BY createdAt DESC")
    suspend fun getPlaylistTracks(trackIds: List<Long>): Flow<List<TrackPlaylistEntity>>
}