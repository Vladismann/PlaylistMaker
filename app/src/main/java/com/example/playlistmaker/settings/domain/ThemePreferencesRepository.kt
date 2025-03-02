package com.example.playlistmaker.settings.domain

interface ThemePreferencesRepository {

    fun getCurrentTheme(): Boolean

    fun switchTheme(darkThemeEnabled: Boolean)
}