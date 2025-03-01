package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.player.domain.TrackPlayer
import com.example.playlistmaker.player.domain.TrackPlayerImpl
import com.example.playlistmaker.search.data.TrackRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.impl.TrackInteractorImpl

object Creator {

    private fun getTrackRepository(context: Context): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient(), context)
    }

    fun provideTrackInteractor(context: Context): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository(context))
    }

    fun provideTrackPlayer(): TrackPlayer {
        return TrackPlayerImpl()
    }
}