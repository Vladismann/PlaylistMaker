package com.example.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.playlistmaker.DataConst.SEARCH_PREFS
import com.example.playlistmaker.DataConst.TRACK_TO_AUDIO_PLAYER_KEY
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        val toolbar = findViewById<Toolbar>(R.id.apToolbar)
        val trackImage = findViewById<ImageView>(R.id.apTrackImage)
        val trackName = findViewById<TextView>(R.id.apTrackName)
        val trackArtistName = findViewById<TextView>(R.id.apTrackArtistName)
        val trackDuration = findViewById<TextView>(R.id.apActualDuration)
        val trackAlbum = findViewById<TextView>(R.id.apActualAlbum)
        val trackYear = findViewById<TextView>(R.id.apActualYear)
        val trackGenre = findViewById<TextView>(R.id.apActualGenre)
        val trackCountry = findViewById<TextView>(R.id.apActualCountry)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val actualTrack = readTrackFromPrefs()

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
            if (actualTrack.trackTimeMillis != null) {
                trackDuration.text =
                    SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(actualTrack.trackTimeMillis)
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
        }
    }

    private fun readTrackFromPrefs(): Track? {
        val sharedPreferences = getSharedPreferences(SEARCH_PREFS, MODE_PRIVATE)
        val json = sharedPreferences.getString(TRACK_TO_AUDIO_PLAYER_KEY, null)
        return if (!json.isNullOrEmpty()) {
            GsonProvider.gson.fromJson(json, Track::class.java)
        } else {
            null
        }
    }
}