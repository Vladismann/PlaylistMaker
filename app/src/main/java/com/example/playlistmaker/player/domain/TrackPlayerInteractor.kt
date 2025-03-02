package com.example.playlistmaker.player.domain

import com.example.playlistmaker.player.data.TrackPlayer.StatusObserver

interface TrackPlayerInteractor {
    fun preparePlayer(url: String?)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun resumePlayer()
    fun getCurrentPosition(): Int
    fun setStatusObserver(observer: StatusObserver)
}