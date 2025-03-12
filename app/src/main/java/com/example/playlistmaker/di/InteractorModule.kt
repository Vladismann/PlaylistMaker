package com.example.playlistmaker.di

import com.example.playlistmaker.player.domain.TrackPlayer
import com.example.playlistmaker.player.domain.TrackPlayerInteractor
import com.example.playlistmaker.player.domain.TrackPlayerInteractorImpl
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.settings.domain.ThemePreferenceInteractor
import com.example.playlistmaker.settings.domain.ThemePreferenceInteractorImpl
import com.example.playlistmaker.settings.domain.ThemePreferencesRepository
import org.koin.dsl.module

val trackInteractorModule = module {

    single<TrackInteractor> {
        TrackInteractorImpl(get<TrackRepository>())
    }

    single<TrackPlayerInteractor> {
        TrackPlayerInteractorImpl(get<TrackPlayer>())
    }

    single<ThemePreferenceInteractor> {
        ThemePreferenceInteractorImpl(get<ThemePreferencesRepository>())
    }
}