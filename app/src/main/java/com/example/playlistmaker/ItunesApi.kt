package com.example.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApi {

    @GET("search")
    fun searchTracks(@Query("term") term: String): Call<TrackResponse>
}