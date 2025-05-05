package com.example.playlistmaker.search.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(private val trackInteractor: TrackInteractor) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val TRACK_HISTORY_SIZE = 10
    }

    private val screenState = MutableLiveData<SearchScreenState>(SearchScreenState.Loading)
    val searchScreenState: LiveData<SearchScreenState> = screenState

    init {
        loadSearchHistory()
    }

    private fun searchTracks(query: String) {
        viewModelScope.launch {
            if (query.isNotBlank()) {
                screenState.value = SearchScreenState.Loading
            }

            trackInteractor.searchTracks(query)
                .collect { actualResult ->
                    if (query.isBlank()) {
                        loadSearchHistory()
                    } else if (actualResult.isError) {
                        screenState.value = SearchScreenState.Error(showRefresh = true)
                    } else {
                        val currentHistory = when (val state = screenState.value) {
                            is SearchScreenState.Content -> state.historyTracks
                            else -> emptyList()
                        }

                        screenState.value = SearchScreenState.Content(
                            tracks = actualResult.tracks,
                            historyTracks = currentHistory,
                            query = query
                        )
                    }
                }
        }
    }

    private var searchJob: Job? = null

    fun searchDebounce(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchTracks(query)
        }
    }

    fun saveTrackToHistory(track: Track) {
        val currentHistory = trackInteractor.getTracksHistory().toMutableList()

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

    private fun loadSearchHistory() {
        val history = trackInteractor.getTracksHistory()
        screenState.value = SearchScreenState.Content(tracks = emptyList(), historyTracks = history, query = "")
    }
}
