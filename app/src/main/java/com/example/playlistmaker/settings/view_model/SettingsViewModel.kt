package com.example.playlistmaker.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.ThemePreferenceInteractor

class SettingsViewModel(private val themePreferencesInteractor : ThemePreferenceInteractor) : ViewModel() {

    private val isDarkThemeState = MutableLiveData<Boolean>()
    val isDarkTheme: LiveData<Boolean> get() = isDarkThemeState

    init {
        isDarkThemeState.value = themePreferencesInteractor.getCurrentTheme()
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        if (themePreferencesInteractor.getCurrentTheme() == darkThemeEnabled) {
            return
        }
        themePreferencesInteractor.switchTheme(darkThemeEnabled)
        isDarkThemeState.value = darkThemeEnabled
    }
}