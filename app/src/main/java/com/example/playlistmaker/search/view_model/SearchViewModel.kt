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
        private const val TRACK_HISTORY_SIZE = 10

        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val trackInteractor = Creator.provideTrackInteractor(context)
                SearchViewModel(trackInteractor)
            }
        }
    }

    private val screenState = MutableLiveData<SearchScreenState>(SearchScreenState.Loading)
    val searchScreenState: LiveData<SearchScreenState> = screenState

    private val handler = Handler(Looper.getMainLooper())

    init {
        loadSearchHistory()
    }

    private fun searchTracks(query: String) {
        screenState.value = SearchScreenState.Loading

        trackInteractor.searchTracks(query, object : TrackInteractor.TrackConsumer {
            override fun consume(actualResult: TrackSearchResult) {
                handler.post {
                    if (query.isBlank()) {
                        loadSearchHistory()
                    } else if (actualResult.isError) {
                        screenState.value = SearchScreenState.Error(showRefresh = true)
                    } else {
                        val currentHistory = screenState.value?.let {
                            if (it is SearchScreenState.Content) it.historyTracks else emptyList()
                        } ?: emptyList()

                        screenState.value = SearchScreenState.Content(
                            tracks = actualResult.tracks,
                            historyTracks = currentHistory,
                            query = query)
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
        val currentHistory =
            (screenState.value as? SearchScreenState.Content)?.historyTracks?.toMutableList() ?: mutableListOf()

        currentHistory.remove(track)
        if (currentHistory.size == TRACK_HISTORY_SIZE) {
            currentHistory.removeAt(TRACK_HISTORY_SIZE - 1)
        }
        currentHistory.add(0, track)
        trackInteractor.writeTracksHistory(currentHistory)

        screenState.value = (screenState.value as? SearchScreenState.Content)?.copy(historyTracks = currentHistory)
    }

    fun saveForAudioPlayer(track: Track) {
        trackInteractor.writeTrackForAudioPlayer(track)
    }

    fun clearHistory() {
        trackInteractor.clearTracksHistory()
        screenState.value = (screenState.value as? SearchScreenState.Content)?.copy(historyTracks = emptyList())
    }

    fun loadSearchHistory() {
        val history = trackInteractor.getTracksHistory()
        screenState.value = SearchScreenState.Content(tracks = emptyList(), historyTracks = history, query = "")
    }
}
