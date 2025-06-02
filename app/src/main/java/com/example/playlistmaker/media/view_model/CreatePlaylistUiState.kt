package com.example.playlistmaker.media.view_model

data class CreatePlaylistUiState(
    val name: String = "",
    val description: String = "",
    val imagePath: String = "",
    val isCreateEnabled: Boolean = false
)