package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.player.domain.TrackPlayer
import com.example.playlistmaker.player.data.TrackPlayerImpl
import com.example.playlistmaker.player.domain.TrackPlayerInteractor
import com.example.playlistmaker.player.domain.TrackPlayerInteractorImpl
import com.example.playlistmaker.search.data.TrackRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.settings.data.ThemePreferencesRepositoryImpl
import com.example.playlistmaker.settings.domain.ThemePreferenceInteractor
import com.example.playlistmaker.settings.domain.ThemePreferenceInteractorImpl
import com.example.playlistmaker.settings.domain.ThemePreferencesRepository

object Creator {

    private fun getTrackRepository(context: Context): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient(), context)
    }

    fun provideTrackInteractor(context: Context): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository(context))
    }

    private fun getTrackPlayer(): TrackPlayer {
        return TrackPlayerImpl()
    }

    fun provideTrackPlayerInteractor(): TrackPlayerInteractor {
            return TrackPlayerInteractorImpl(getTrackPlayer())
    }

    private fun getThemePreferencesRepository(context: Context): ThemePreferencesRepository {
        return ThemePreferencesRepositoryImpl(context)
    }

    fun provideThemePreferenceInteractor(context: Context): ThemePreferenceInteractor {
        return ThemePreferenceInteractorImpl(getThemePreferencesRepository(context))
    }
}