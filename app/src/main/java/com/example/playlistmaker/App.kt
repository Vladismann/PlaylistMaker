package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate


class App : Application() {

    companion object {
        const val PREFS_NAME = "AppThemePrefs"
        const val DARK_THEME_KEY = "dark"
    }

    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val theme = getCurrentTheme()
        if (theme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    fun getCurrentTheme(): Boolean {
        if (!sharedPreferences.contains(DARK_THEME_KEY)) {
            //для первого запуска, ориентируемся на тему пользователя, далее по настройкам
            val currentNightMode =
                resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            setDarkTheme(currentNightMode == Configuration.UI_MODE_NIGHT_YES)
        }
        return sharedPreferences.getBoolean(DARK_THEME_KEY, false)
    }

    private fun setDarkTheme(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(DARK_THEME_KEY, enabled).apply()
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        setDarkTheme(darkThemeEnabled)
        if (darkThemeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}