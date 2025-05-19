package com.example.playlistmaker.search.data

import android.content.SharedPreferences
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.search.data.DataConst.TRACK_HISTORY_KEY
import com.example.playlistmaker.search.data.DataConst.TRACK_TO_AUDIO_PLAYER_KEY
import com.example.playlistmaker.search.data.network.TrackSearchRequest
import com.example.playlistmaker.search.data.network.TrackSearchResponse
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Locale

class TrackRepositoryImpl(private val networkClient: NetworkClient,
                          private val sharedPreferences: SharedPreferences,
                          private val gson: Gson,
                          private val appDatabase: AppDatabase) : TrackRepository {


    override fun searchTracks(expression: String): Flow<List<Track>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))

        if (response.resultCode == 400 || response.resultCode == 0 || response.resultCode == 503) {
            throw Exception()
        } //у аудиокниг нет trackId, фильтруем только треки без аудиокниг
        val tracks = if (response.resultCode == 200) {
            (response as TrackSearchResponse).results.filter { track -> track.trackId != null }.map { track ->

                val trackYear = if (!track.releaseDate.isNullOrEmpty()) {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                    val date = dateFormat.parse(track.releaseDate)
                    date?.let {
                        SimpleDateFormat("YYYY", Locale.getDefault()).format(it)
                    } ?: ""
                } else {
                    "Unknown"
                }

                Track(track.trackId,
                    track.trackName,
                    track.artistName,
                    SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis ?: 0L),
                    track.artworkUrl100,
                    track.collectionName,
                    trackYear,
                    track.primaryGenreName,
                    track.country,
                    track.previewUrl,
                    )
            }
        } else {
            emptyList()
        }
        emit(tracks)
    }

    override fun saveTrackHistory(tracks: List<Track>) {
        val json = gson.toJson(tracks)
        sharedPreferences.edit().putString(TRACK_HISTORY_KEY, json).apply()
    }

    override fun getTrackHistory(): List<Track> {
        val json = sharedPreferences.getString(TRACK_HISTORY_KEY, null)
        return if (!json.isNullOrEmpty()) {
            gson.fromJson(json, Array<Track>::class.java).toList()
        } else {
            emptyList()
        }
    }

    override fun clearTrackHistory() {
        sharedPreferences.edit().clear().apply()
    }

    override fun saveTrackForAudioPlayer(track: Track) {
        val json = gson.toJson(track)
        sharedPreferences.edit().putString(TRACK_TO_AUDIO_PLAYER_KEY, json).apply()
    }

    override fun getTrackForAudioPlayer(): Track? {
        val json = sharedPreferences.getString(TRACK_TO_AUDIO_PLAYER_KEY, null)
        return if (!json.isNullOrEmpty()) {
            gson.fromJson(json, Track::class.java)
        } else {
            null
        }
    }

    override suspend fun checkIsFavorite(trackId: Long?): Boolean {
        return appDatabase.favoriteTrackDao().getTracksIds().contains(trackId)
    }

}