package com.example.playlistmaker.presentation.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.TrackInteractor
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val TRACK_UPDATE_TIME_DELAY = 500L
    }

    private lateinit var playButton: ImageButton
    private lateinit var trackPlayTime: TextView
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private var handler: Handler? = null
    private lateinit var tackInteractor: TrackInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        tackInteractor = Creator.provideTrackInteractor(this)
        val toolbar = findViewById<Toolbar>(R.id.apToolbar)
        val trackImage = findViewById<ImageView>(R.id.apTrackImage)
        val trackName = findViewById<TextView>(R.id.apTrackName)
        val trackArtistName = findViewById<TextView>(R.id.apTrackArtistName)
        val trackDuration = findViewById<TextView>(R.id.apActualDuration)
        val trackAlbum = findViewById<TextView>(R.id.apActualAlbum)
        val trackYear = findViewById<TextView>(R.id.apActualYear)
        val trackGenre = findViewById<TextView>(R.id.apActualGenre)
        val trackCountry = findViewById<TextView>(R.id.apActualCountry)
        playButton = findViewById(R.id.apPlayAudioButton)
        trackPlayTime = findViewById(R.id.apPlayingTime)
        handler = Handler(Looper.getMainLooper())

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val actualTrack = tackInteractor.readTrackForAudioPlayer()

        if (actualTrack != null) {
            Glide.with(this)
                .load(actualTrack.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg"))
                .placeholder(R.drawable.placeholder_full_size)
                .into(trackImage)

            if (!actualTrack.trackName.isNullOrEmpty()) {
                trackName.text = actualTrack.trackName
            }
            if (!actualTrack.artistName.isNullOrEmpty()) {
                trackArtistName.text =
                    actualTrack.artistName
            }
            if (!actualTrack.trackTime.isNullOrEmpty()) {
                trackDuration.text = actualTrack.trackTime
            }
            if (!actualTrack.collectionName.isNullOrEmpty()) {
                trackAlbum.text =
                    actualTrack.collectionName
            }
            if (!actualTrack.primaryGenreName.isNullOrEmpty()) {
                trackGenre.text =
                    actualTrack.primaryGenreName
            }
            if (!actualTrack.releaseDate.isNullOrEmpty()) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                val date = dateFormat.parse(actualTrack.releaseDate)
                trackYear.text =
                    date?.let {
                        SimpleDateFormat(
                            "YYYY",
                            Locale.getDefault()
                        ).format(it)
                    }
            }
            if (!actualTrack.country.isNullOrEmpty()) {
                trackCountry.text = actualTrack.country
            }
            preparePlayer(actualTrack.previewUrl)
        }

        playButton.setOnClickListener {
            playbackControl()
        }
    }

    private fun preparePlayer(url: String?) {
        if (url != null) {
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playButton.setBackgroundResource(R.drawable.play_audio_button)
                playerState = STATE_PREPARED
            }
            mediaPlayer.setOnCompletionListener {
                playerState = STATE_PREPARED
                handler?.removeCallbacks(playbackTimeRunnable)
                trackPlayTime.text = SimpleDateFormat("m:ss", Locale.getDefault()).format(0L)
                playButton.setBackgroundResource(R.drawable.play_audio_button)
            }
        } else {
            return
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setBackgroundResource(R.drawable.pause_audio_button)
        playerState = STATE_PLAYING
        playbackTimeForward()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setBackgroundResource(R.drawable.play_audio_button)
        playerState = STATE_PAUSED
        handler?.removeCallbacks(playbackTimeRunnable)
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler?.removeCallbacks(playbackTimeRunnable)
    }

    private val playbackTimeRunnable = Runnable {
        trackPlayTime.text =
            SimpleDateFormat("m:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        playbackTimeForward()
    }

    private fun playbackTimeForward() {
        handler?.postDelayed(playbackTimeRunnable, TRACK_UPDATE_TIME_DELAY)
    }
}