package com.example.playlistmaker.player.domain

interface TrackPlayer {
    fun preparePlayer(url: String)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun resumePlayer()
    fun getCurrentPosition(): Int
    fun setStatusObserver(observer: StatusObserver)

    interface StatusObserver {
        fun onStop()
        fun onPlay()
        fun onResume()
    }
}