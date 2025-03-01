package com.example.playlistmaker.search.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.example.playlistmaker.search.data.DataConst.SEARCH_PREFS
import com.example.playlistmaker.search.data.DataConst.TRACK_HISTORY_KEY
import com.example.playlistmaker.search.data.DataConst.TRACK_TO_AUDIO_PLAYER_KEY
import com.example.playlistmaker.search.data.network.TrackSearchRequest
import com.example.playlistmaker.search.data.network.TrackSearchResponse
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackRepositoryImpl(private val networkClient: NetworkClient, context: Context) :
    TrackRepository {

    private val sharedPreferences by lazy {
        context.getSharedPreferences(SEARCH_PREFS, MODE_PRIVATE)
    }

    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))

        if (response.resultCode == 400 || response.resultCode == 0 || response.resultCode == 503) {
            throw Exception()
        }
        //у аудиокниг нет trackId, фильтруем только треки без аудиокниг
        if (response.resultCode == 200) {
            return (response as TrackSearchResponse).results.filter { track -> track.trackId != null }
                .map { track ->

                    val trackYear = if (!track.releaseDate.isNullOrEmpty()) {
                        val dateFormat =
                            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                        val date = dateFormat.parse(track.releaseDate)
                        date?.let {
                            SimpleDateFormat("YYYY", Locale.getDefault()).format(it)
                        } ?: ""
                    } else {
                        "Unknown"
                    }

                    Track(
                        track.trackId,
                        track.trackName,
                        track.artistName,
                        SimpleDateFormat("m:ss", Locale.getDefault()).format(
                            track.trackTimeMillis ?: 0L
                        ),
                        track.artworkUrl100,
                        track.collectionName,
                        trackYear,
                        track.primaryGenreName,
                        track.country,
                        track.previewUrl,
                    )
                }
        } else {
            return emptyList()
        }
    }

    override fun saveTrackHistory(tracks: List<Track>) {
        val json = GsonProvider.gson.toJson(tracks)
        sharedPreferences.edit()
            .putString(TRACK_HISTORY_KEY, json)
            .apply()
    }

    override fun getTrackHistory(): List<Track> {
        val json = sharedPreferences.getString(TRACK_HISTORY_KEY, null)
        return if (!json.isNullOrEmpty()) {
            GsonProvider.gson.fromJson(json, Array<Track>::class.java).toList()
        } else {
            emptyList()
        }
    }

    override fun clearTrackHistory() {
        sharedPreferences.edit().clear().apply()
    }

    override fun saveTrackForAudioPlayer(track: Track) {
        val json = GsonProvider.gson.toJson(track)
        sharedPreferences.edit()
            .putString(TRACK_TO_AUDIO_PLAYER_KEY, json)
            .apply()
    }

    override fun getTrackForAudioPlayer(): Track? {
        val json = sharedPreferences.getString(TRACK_TO_AUDIO_PLAYER_KEY, null)
        return if (!json.isNullOrEmpty()) {
            GsonProvider.gson.fromJson(json, Track::class.java)
        } else {
            null
        }
    }

}