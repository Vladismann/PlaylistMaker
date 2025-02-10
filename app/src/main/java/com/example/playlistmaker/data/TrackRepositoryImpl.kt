package com.example.playlistmaker.data

import android.content.Context.MODE_PRIVATE
import com.example.playlistmaker.App
import com.example.playlistmaker.data.DataConst.SEARCH_PREFS
import com.example.playlistmaker.data.DataConst.TRACK_HISTORY_KEY
import com.example.playlistmaker.data.DataConst.TRACK_TO_AUDIO_PLAYER_KEY
import com.example.playlistmaker.data.network.TrackSearchRequest
import com.example.playlistmaker.data.network.TrackSearchResponse
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {

    private var sharedPreferences = App.context.getSharedPreferences(SEARCH_PREFS, MODE_PRIVATE)

    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))

        if (response.resultCode == 400 || response.resultCode == 0 || response.resultCode == 503) {
            throw Exception()
        }
        //у аудиокниг нет trackId, фильтруем только треки без аудиокниг
        if (response.resultCode == 200) {
            return (response as TrackSearchResponse).results.filter { track -> track.trackId != null }
                .map { track ->
                    Track(
                        track.trackId,
                        track.trackName,
                        track.artistName,
                        SimpleDateFormat("m:ss", Locale.getDefault()).format(track.trackTimeMillis),
                        track.artworkUrl100,
                        track.collectionName,
                        track.releaseDate,
                        track.primaryGenreName,
                        track.country,
                        track.previewUrl,
                    )
                }
        } else {
            return emptyList()
        }
    }

    override fun saveTrackHistory(tracks: Array<Track>) {
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