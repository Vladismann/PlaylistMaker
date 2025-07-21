package com.example.playlistmaker.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.player.domain.TrackPlayer
import com.example.playlistmaker.player.domain.TrackPlayerInteractor
import com.example.playlistmaker.player.view_model.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AudioPlayerService() : Service(), IAudioPlayerService {

    private val binder = AudioPlayerBinder()
    private val trackPlayer: TrackPlayerInteractor by inject()
    private var updateTimeJob: Job? = null
    private var isPaused = false
    private var observer: ((PlayerState) -> Unit)? = null
    private var trackName: String? = null
    private var artistName: String? = null
    private var trackUrl: String? = null

    companion object {
        private const val NOTIFICATION_ID = 1
        const val NOTIFICATION_CHANNEL_ID = "audio_player_channel"
        private const val CHANNEL_NAME = "Audio Player"
    }

    inner class AudioPlayerBinder : Binder() {
        fun getService(): IAudioPlayerService = this@AudioPlayerService
    }

    override fun onBind(intent: Intent?): IBinder {
        trackName = intent?.getStringExtra("TRACK_NAME")
        artistName = intent?.getStringExtra("ARTIST_NAME")
        trackUrl = intent?.getStringExtra("TRACK_URL")
        return binder
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Service for playing music"
        channel.setSound(null, null)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }


    override fun showNotification(shouldShow : Boolean) {
        if (shouldShow && trackPlayer.isPlaying()) {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                createServiceNotification(),
                getForegroundServiceTypeConstant()
            )
        } else {
            stopForeground(true)
        }
    }

    fun initPlayer() {
        trackPlayer.setStatusObserver(object : TrackPlayer.StatusObserver {
            override fun onStop() {
                observer?.invoke(PlayerState(isPlaying = false, progress = 0, currentTime = "00:00"))
                updateTimeJob?.cancel()
                isPaused = false
                stopForeground(true)
                stopSelf()
            }

            override fun onPlay() {
                trackPlayer.startPlayer()
                observer?.invoke(PlayerState(isPlaying = true))
                startUpdateTimeJob()
                isPaused = false
            }

            override fun onResume() {
                observer?.invoke(PlayerState(isPlaying = true))
                startUpdateTimeJob()
                isPaused = false
            }
        })
    }

    override fun prepareAndPlay(url: String?) {
        trackPlayer.preparePlayer(url)
        trackPlayer.startPlayer()
        isPaused = false
    }

    override fun pause() {
        trackPlayer.pausePlayer()
        updateTimeJob?.cancel()
        observer?.invoke(
            PlayerState(
                isPlaying = false,
                progress = trackPlayer.getCurrentPosition(),
                currentTime =  getCurTimeStr(trackPlayer.getCurrentPosition())
            )
        )
        isPaused = true
    }

    override fun resume() {
        if (isPaused) {
            isPaused = false
            trackPlayer.resumePlayer()
            startUpdateTimeJob()
            observer?.invoke(
                PlayerState(
                    isPlaying = true,
                    progress = trackPlayer.getCurrentPosition(),
                    currentTime =  getCurTimeStr(trackPlayer.getCurrentPosition())
                )
            )
        }
    }

    override fun setObserver(listener: (PlayerState) -> Unit) {
        observer = listener
    }

    private fun startUpdateTimeJob() {
        updateTimeJob?.cancel()
        updateTimeJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                observer?.invoke(PlayerState(currentTime = getCurTimeStr(trackPlayer.getCurrentPosition()), isPlaying = true))
                delay(300)
            }
        }
    }

    private fun getCurTimeStr(time : Int) : String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(Date(time.toLong()))
    }

    override fun onDestroy() {
        updateTimeJob?.cancel()
        trackPlayer.releasePlayer()
        super.onDestroy()
    }

    private fun createServiceNotification(): Notification {
        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(trackName ?: "Трек")
            .setContentText(artistName ?: "Исполнитель")
            .setSmallIcon(R.drawable.media_svg)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
        return builder.build()
    }

    private fun getForegroundServiceTypeConstant(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            0
        }
    }
}
