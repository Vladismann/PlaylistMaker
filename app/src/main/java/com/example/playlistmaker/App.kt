package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.data.ThemePreferencesRepositoryImpl
import com.example.playlistmaker.settings.domain.ThemePreferencesRepository


class App : Application() {

    private lateinit var themePreferencesRepository: ThemePreferencesRepository

    override fun onCreate() {
        super.onCreate()
        themePreferencesRepository = ThemePreferencesRepositoryImpl(this)

        val isDarkTheme = themePreferencesRepository.getCurrentTheme()

        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
