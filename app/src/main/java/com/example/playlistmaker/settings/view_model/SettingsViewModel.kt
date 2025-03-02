package com.example.playlistmaker.settings.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.creator.Creator

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val themePreferencesRepository = Creator.provideThemePreferenceInteractor(application)

    private val isDarkThemeState = MutableLiveData<Boolean>()
    val isDarkTheme: LiveData<Boolean> get() = isDarkThemeState

    init {
        isDarkThemeState.value = themePreferencesRepository.getCurrentTheme()
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        if (themePreferencesRepository.getCurrentTheme() == darkThemeEnabled) {
            return
        }
        themePreferencesRepository.switchTheme(darkThemeEnabled)
        isDarkThemeState.value = darkThemeEnabled
    }
}