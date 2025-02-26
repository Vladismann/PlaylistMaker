package com.example.playlistmaker.settings.view_model

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.settings.model.ThemePreferences

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val themePreferences = ThemePreferences(application)

    private val isDarkThemeState = MutableLiveData<Boolean>()
    val isDarkTheme: LiveData<Boolean> get() = isDarkThemeState

    init {
        isDarkThemeState.value = themePreferences.getCurrentTheme()
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        themePreferences.switchTheme(darkThemeEnabled)
        isDarkThemeState.value = darkThemeEnabled
        if (darkThemeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}