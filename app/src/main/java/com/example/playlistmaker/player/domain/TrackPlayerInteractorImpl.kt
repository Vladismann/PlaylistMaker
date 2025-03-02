package com.example.playlistmaker.player.domain

import com.example.playlistmaker.player.data.TrackPlayer

class TrackPlayerInteractorImpl(private val repository: TrackPlayer)  : TrackPlayerInteractor {

    override fun preparePlayer(url: String) {
        repository.preparePlayer(url)
    }

    override fun startPlayer() {
        repository.startPlayer()
    }

    override fun pausePlayer() {
        repository.pausePlayer()
    }

    override fun releasePlayer() {
        repository.releasePlayer()
    }

    override fun resumePlayer() {
        repository.resumePlayer()
    }

    override fun getCurrentPosition(): Int {
        return repository.getCurrentPosition()
    }

    override fun setStatusObserver(observer: TrackPlayer.StatusObserver) {
        repository.setStatusObserver(observer)
    }
}
