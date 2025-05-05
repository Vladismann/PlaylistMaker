package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import java.io.IOException

class RetrofitNetworkClient(private val itunesService: ItunesApi): NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        return try {
            if (dto is TrackSearchRequest) {
                val resp = itunesService.searchTracks(dto.expression)

                val body = resp.body() ?: Response()
                return body.apply { resultCode = resp.code() }
            } else {
                Response().apply { resultCode = 400 }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Response().apply { resultCode = 503 }
        } catch (e: Exception) {
            e.printStackTrace()
            Response().apply { resultCode = 500 }
        }
    }
}