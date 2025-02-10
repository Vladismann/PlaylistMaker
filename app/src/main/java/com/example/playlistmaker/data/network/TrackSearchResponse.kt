package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TrackDto

class TrackSearchResponse (
    val results: List<TrackDto>
) : Response()