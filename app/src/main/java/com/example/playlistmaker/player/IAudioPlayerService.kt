package com.example.playlistmaker.player

import com.example.playlistmaker.player.view_model.PlayerState

interface IAudioPlayerService {
    fun prepareAndPlay(url: String?)
    fun pause()
    fun resume()
    fun setObserver(listener: (PlayerState) -> Unit)
    fun showNotification(shouldShow : Boolean)
}