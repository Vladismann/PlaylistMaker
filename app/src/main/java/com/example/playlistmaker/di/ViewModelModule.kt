package com.example.playlistmaker.di

import com.example.playlistmaker.media.domain.db.FavoriteTrackInteractor
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.view_model.CreatePlaylistViewModel
import com.example.playlistmaker.media.view_model.FavoritesViewModel
import com.example.playlistmaker.media.view_model.PlaylistDetailsViewModel
import com.example.playlistmaker.media.view_model.PlaylistsViewModel
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
        TrackViewModel(get<TrackInteractor>(), get<TrackPlayerInteractor>(), get<FavoriteTrackInteractor>(), get<PlaylistInteractor>())
    }

    viewModel {
        SettingsViewModel(get<ThemePreferenceInteractor>())
    }

    viewModel { FavoritesViewModel(get<FavoriteTrackInteractor>(), get<TrackInteractor>()) }

    viewModel { PlaylistsViewModel(get<PlaylistInteractor>()) }

    viewModel { CreatePlaylistViewModel(get<PlaylistInteractor>()) }

    viewModel { PlaylistDetailsViewModel(get<PlaylistInteractor>(), get<TrackInteractor>()) }
}