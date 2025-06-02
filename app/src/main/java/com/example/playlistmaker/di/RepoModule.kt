package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.media.MediaPlayer
import androidx.room.Room
import com.example.playlistmaker.media.data.converters.PlaylistDbConverter
import com.example.playlistmaker.media.data.converters.TrackDbConverter
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.domain.db.FavoriteTrackRepo
import com.example.playlistmaker.media.domain.db.FavoriteTrackRepoImpl
import com.example.playlistmaker.media.domain.db.PlaylistRepo
import com.example.playlistmaker.media.domain.db.PlaylistRepoImpl
import com.example.playlistmaker.player.data.TrackPlayerImpl
import com.example.playlistmaker.player.domain.TrackPlayer
import com.example.playlistmaker.search.data.DataConst.SEARCH_PREFS
import com.example.playlistmaker.search.data.DataConst.THEME_PREFS
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.TrackRepositoryImpl
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.settings.data.ThemePreferencesRepositoryImpl
import com.example.playlistmaker.settings.domain.ThemePreferencesRepository
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val trackRepoModule = module {
    single<SharedPreferences>(named(SEARCH_PREFS)) {
        androidContext().getSharedPreferences(SEARCH_PREFS, Context.MODE_PRIVATE)
    }

    single<SharedPreferences>(named(THEME_PREFS)) {
        androidContext().getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE)
    }

    factory<Gson> { Gson() }

    factory<TrackRepository> {
        TrackRepositoryImpl(
            get<NetworkClient>(), get<SharedPreferences>(named(SEARCH_PREFS)), get<Gson>(), get<AppDatabase>()
        )
    }

    factory<MediaPlayer> {
        MediaPlayer()
    }

    factory<TrackPlayer> {
        TrackPlayerImpl(get<MediaPlayer>())
    }

    single<Resources> {
        androidContext().resources
    }

    factory<ThemePreferencesRepository> {
        ThemePreferencesRepositoryImpl(get<SharedPreferences>(named(THEME_PREFS)), get<Resources>())
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db").fallbackToDestructiveMigration()
            .build()
    }

    factory { TrackDbConverter() }

    single<FavoriteTrackRepo> {
        FavoriteTrackRepoImpl(get<AppDatabase>(), get<TrackDbConverter>())
    }

    factory { PlaylistDbConverter() }

    single<PlaylistRepo> {
        PlaylistRepoImpl(get<AppDatabase>(), get<PlaylistDbConverter>())
    }
}