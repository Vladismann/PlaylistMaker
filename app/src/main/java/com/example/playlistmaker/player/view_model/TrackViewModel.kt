package com.example.playlistmaker.player.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.api.TrackInteractor

class TrackViewModel(
    tracksInteractor: TrackInteractor,
) : ViewModel() {

    private var loadingLiveData = MutableLiveData(true)

    init {
        if (tracksInteractor.readTrackForAudioPlayer() != null) {
            loadingLiveData.value = false
        }
    }

    fun getLoadingLiveData(): LiveData<Boolean> = loadingLiveData

    companion object {
        fun getViewModelFactory(context: Context): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val interactor = Creator.provideTrackInteractor(context)

                    TrackViewModel(
                        interactor
                    )
                }
            }
    }


}