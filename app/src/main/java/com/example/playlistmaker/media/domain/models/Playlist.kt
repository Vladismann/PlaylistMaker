package com.example.playlistmaker.media.domain.models

data class Playlist (
    val playlistId: Long?,
    val playlistName: String,
    val playlistDescr: String?,
    val playlistImageUrl: String?,
    val trackIds: List<Long>)