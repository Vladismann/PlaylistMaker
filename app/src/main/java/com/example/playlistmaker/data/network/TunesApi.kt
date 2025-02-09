package com.example.playlistmaker.data.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TunesApi {

    @GET("search")
    fun searchTracks(@Query("term") term: String): Call<TrackSearchResponse>
}
