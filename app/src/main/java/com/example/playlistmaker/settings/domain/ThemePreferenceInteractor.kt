package com.example.playlistmaker.settings.domain

interface ThemePreferenceInteractor {

    fun getCurrentTheme(): Boolean

    fun switchTheme(darkThemeEnabled: Boolean)
}