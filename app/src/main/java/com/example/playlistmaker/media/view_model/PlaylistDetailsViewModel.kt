package com.example.playlistmaker.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import kotlinx.coroutines.launch

class PlaylistDetailsViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val screenStateLiveData = MutableLiveData<PlaylistDetailsScreenState>(PlaylistDetailsScreenState.Loading)


    fun getScreenStateLiveData(): LiveData<PlaylistDetailsScreenState> = screenStateLiveData

    init {
        viewModelScope.launch {
            var playlist = playlistInteractor.getPlaylist(1)
            screenStateLiveData.postValue(PlaylistDetailsScreenState.Content(playlist))
        }
    }
}