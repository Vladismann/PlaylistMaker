package com.example.playlistmaker.di

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.network.ItunesApi
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<ItunesApi> {
        get<Retrofit>().create(ItunesApi::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get<ItunesApi>())
    }
}