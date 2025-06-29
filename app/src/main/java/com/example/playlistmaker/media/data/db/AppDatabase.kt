package com.example.playlistmaker.media.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    version = 5,
    entities = [TrackEntity::class, PlaylistEntity::class, PlaylistTrackEntity::class, TrackPlaylistEntity::class],
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteTrackDao(): FavoriteTrackDao

    abstract fun playlistDao(): PlaylistDao

    abstract fun playlistTrackDao(): PlaylistTrackDao

    abstract fun trackPlaylistDao(): TrackPlaylistDao

    companion object {
        fun build(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "app-database")
                .fallbackToDestructiveMigration() // Удаляет и пересоздает БД при любой миграции
                .build()
        }
    }
}