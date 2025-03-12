package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.media.MediaPlayer
import com.example.playlistmaker.player.data.TrackPlayerImpl
import com.example.playlistmaker.player.domain.TrackPlayer
import com.example.playlistmaker.search.data.DataConst.SEARCH_PREFS
import com.example.playlistmaker.search.data.DataConst.THEME_PREFS
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.TrackRepositoryImpl
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.settings.data.ThemePreferencesRepositoryImpl
import com.example.playlistmaker.settings.domain.ThemePreferencesRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val trackRepoModule = module {
    single<SharedPreferences>(named("SearchPrefs")) {
        androidContext().getSharedPreferences(SEARCH_PREFS, Context.MODE_PRIVATE)
    }

    single<SharedPreferences>(named("ThemePrefs")) {
        androidContext().getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE)
    }

    single<TrackRepository> {
        TrackRepositoryImpl(
            get<NetworkClient>(),
            get<SharedPreferences>(named("SearchPrefs"))
        )
    }

    single<MediaPlayer> {
        MediaPlayer()
    }

    single<TrackPlayer> {
        TrackPlayerImpl(get<MediaPlayer>())
    }

    single<Resources> {
        androidContext().resources
    }

    single<ThemePreferencesRepository> {
        ThemePreferencesRepositoryImpl(get<SharedPreferences>(named("ThemePrefs")), get<Resources>())
    }
}