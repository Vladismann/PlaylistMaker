package com.example.playlistmaker.player.view_model

data class PlayerState(
    val isPlaying: Boolean = false,
    val progress: Int = 0,
    val currentTime: String = "00:00"
)