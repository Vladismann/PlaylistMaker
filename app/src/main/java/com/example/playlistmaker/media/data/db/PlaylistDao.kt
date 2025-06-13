package com.example.playlistmaker.media.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlistEntity: PlaylistEntity) : Long

    @Query("SELECT * FROM playlist_table")
    fun getPlayLists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlist_table WHERE playlistId = :playlistId")
    suspend fun getPlayList(playlistId: Long?): PlaylistEntity?

    @Query("DELETE FROM playlist_table WHERE playlistId = :playlistId")
    suspend fun deletePlayList(playlistId: Long?) : Int
}