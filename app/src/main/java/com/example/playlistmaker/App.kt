package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.ThemePreferenceInteractor


class App : Application() {

    private lateinit var themePreferencesInteractor: ThemePreferenceInteractor

    override fun onCreate() {
        super.onCreate()
        themePreferencesInteractor = Creator.provideThemePreferenceInteractor(applicationContext)
        val isDarkTheme = themePreferencesInteractor.getCurrentTheme()

        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
