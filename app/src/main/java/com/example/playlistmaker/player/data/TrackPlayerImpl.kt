package com.example.playlistmaker.player.data

import android.media.MediaPlayer

class TrackPlayerImpl : TrackPlayer {
    private var mediaPlayer = MediaPlayer()
    private var statusObserver: TrackPlayer.StatusObserver? = null

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT

    override fun preparePlayer(url: String?) {
        if (url != null) {
            if (url.isNotBlank()) {
                mediaPlayer.release()
                mediaPlayer = MediaPlayer()
                mediaPlayer.setDataSource(url)
                mediaPlayer.prepareAsync()
                mediaPlayer.setOnPreparedListener {
                    playerState = STATE_PREPARED
                    statusObserver?.onPlay()
                }
                mediaPlayer.setOnCompletionListener {
                    playerState = STATE_PREPARED
                    statusObserver?.onStop()
                }
            }
        }
    }

    override fun startPlayer() {
        if (playerState == STATE_PREPARED) {
            mediaPlayer.seekTo(0)
        }
        mediaPlayer.start()
        playerState = STATE_PLAYING
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
    }

    override fun resumePlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        statusObserver?.onResume()  // Сообщаем об успешном возобновлении
    }

    override fun releasePlayer() {
        mediaPlayer.release()
        statusObserver?.onStop()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun setStatusObserver(observer: TrackPlayer.StatusObserver) {
        this.statusObserver = observer
    }

}
