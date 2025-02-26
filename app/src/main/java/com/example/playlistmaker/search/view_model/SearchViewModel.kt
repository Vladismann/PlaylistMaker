package com.example.playlistmaker.search.view_model

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.TrackSearchResult

class SearchViewModel(private val trackInteractor: TrackInteractor) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val TRACK_HISTORY_SIZE = 10

        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val trackInteractor = Creator.provideTrackInteractor(context)
                SearchViewModel(trackInteractor)
            }
        }
    }

    private val screenSearchResults = MutableLiveData<List<Track>>()
    val searchResults: LiveData<List<Track>> = screenSearchResults

    private val screenErrorState = MutableLiveData<Boolean>()
    val errorState: LiveData<Boolean> = screenErrorState

    private val screenLoadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> = screenLoadingState

    private val screenHistoryTracks = MutableLiveData<List<Track>>()
    val historyTracks: LiveData<List<Track>> = screenHistoryTracks

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    init {
        loadSearchHistory()
    }

    private fun searchTracks(query: String) {
        screenLoadingState.value = true
        screenErrorState.value = false

        trackInteractor.searchTracks(query, object : TrackInteractor.TrackConsumer {
            override fun consume(actualResult: TrackSearchResult) {
                if (query.isBlank()) {
                    handler.post {
                        loadSearchHistory()
                        screenLoadingState.value = false
                    }
                    return
                }
                handler.post {
                    screenLoadingState.value = false
                    if (actualResult.isError) {
                        screenErrorState.value = true
                        screenSearchResults.value = emptyList()
                    } else {
                        screenSearchResults.value = actualResult.tracks
                    }
                }
            }
        })
    }

    fun searchDebounce(query: String) {
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({ searchTracks(query) }, SEARCH_DEBOUNCE_DELAY)
    }

    fun saveTrackToHistory(track: Track) {
        val currentHistory = screenHistoryTracks.value?.toMutableList() ?: mutableListOf()
        currentHistory.remove(track)
        if (currentHistory.size == TRACK_HISTORY_SIZE) {
            currentHistory.removeAt(TRACK_HISTORY_SIZE - 1)
        }
        currentHistory.add(0, track)
        trackInteractor.writeTracksHistory(currentHistory)
        screenHistoryTracks.value = currentHistory
    }

    fun saveForAudioPlayer(track: Track) {
        trackInteractor.writeTrackForAudioPlayer(track)
    }

    fun clearHistory() {
        trackInteractor.clearTracksHistory()
        screenHistoryTracks.value = emptyList()
    }

    fun loadSearchHistory() {
        screenHistoryTracks.value = trackInteractor.getTracksHistory()
    }

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
}
