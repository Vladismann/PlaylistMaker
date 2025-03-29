package com.example.playlistmaker.settings.data

import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.search.data.DataConst.DARK_THEME_KEY
import com.example.playlistmaker.settings.domain.ThemePreferencesRepository

class ThemePreferencesRepositoryImpl(private val sharedPreferences: SharedPreferences,  private val resources: Resources) : ThemePreferencesRepository {

    override fun getCurrentTheme(): Boolean {
        if (!sharedPreferences.contains(DARK_THEME_KEY)) { // Для первого запуска, ориентируемся на тему пользователя, далее по настройкам
            val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            setDarkTheme(currentNightMode == Configuration.UI_MODE_NIGHT_YES)
        }
        return sharedPreferences.getBoolean(DARK_THEME_KEY, false)
    }

    private fun setDarkTheme(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(DARK_THEME_KEY, enabled).apply()
        if (enabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun switchTheme(darkThemeEnabled: Boolean) {
        setDarkTheme(darkThemeEnabled)
    }
}