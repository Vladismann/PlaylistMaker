package com.example.playlistmaker.media.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackPlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistTrack(trackEntity: TrackPlaylistEntity) : Long

    @Query("DELETE FROM track_playlist_table WHERE trackId = :trackId")
    suspend fun deleteTrackEntity(trackId: Long?) : Int

    @Query("SELECT * FROM track_playlist_table WHERE trackId IN (:trackIds) ORDER BY createdAt DESC")
    fun getPlaylistTracks(trackIds: List<Long>): Flow<List<TrackPlaylistEntity>>
}