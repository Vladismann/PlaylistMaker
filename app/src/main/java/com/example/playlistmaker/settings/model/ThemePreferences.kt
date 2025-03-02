package com.example.playlistmaker.settings.model

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources

class ThemePreferences(context: Context) {
    companion object {
        const val PREFS_NAME = "AppThemePrefs"
        const val DARK_THEME_KEY = "dark"
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val resources: Resources = context.resources

    fun getCurrentTheme(): Boolean {
        if (!sharedPreferences.contains(DARK_THEME_KEY)) { // для первого запуска, ориентируемся на тему пользователя, далее по настройкам
            val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            setDarkTheme(currentNightMode == Configuration.UI_MODE_NIGHT_YES)
        }
        return sharedPreferences.getBoolean(DARK_THEME_KEY, false)
    }

    private fun setDarkTheme(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(DARK_THEME_KEY, enabled).apply()
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        setDarkTheme(darkThemeEnabled)
    }
}