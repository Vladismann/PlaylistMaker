package com.example.playlistmaker

import android.content.Context
import com.example.playlistmaker.data.TrackRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.impl.TrackInteractorImpl

object Creator {

    private fun getTrackRepository(context: Context): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient(), context)
    }

    fun provideTrackInteractor(context: Context): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository(context))
    }
}