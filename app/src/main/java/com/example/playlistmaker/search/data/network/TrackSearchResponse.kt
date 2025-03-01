package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackDto

class TrackSearchResponse (
    val results: List<TrackDto>
) : Response()