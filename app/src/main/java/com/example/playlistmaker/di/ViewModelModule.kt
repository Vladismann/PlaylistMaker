package com.example.playlistmaker.di

import com.example.playlistmaker.player.domain.TrackPlayerInteractor
import com.example.playlistmaker.player.view_model.TrackViewModel
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.view_model.SearchViewModel
import com.example.playlistmaker.settings.domain.ThemePreferenceInteractor
import com.example.playlistmaker.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get<TrackInteractor>())
    }

    viewModel {
        TrackViewModel(get<TrackInteractor>(), get<TrackPlayerInteractor>())
    }

    viewModel {
        SettingsViewModel(get<ThemePreferenceInteractor>())
    }
}