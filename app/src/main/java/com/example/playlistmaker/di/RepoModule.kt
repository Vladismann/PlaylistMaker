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
        TrackRepositoryImpl(get<NetworkClient>(), get<SharedPreferences>(named(SEARCH_PREFS)), get<Gson>())
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
}