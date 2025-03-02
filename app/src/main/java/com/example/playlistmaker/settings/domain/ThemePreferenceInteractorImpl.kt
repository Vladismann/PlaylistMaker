package com.example.playlistmaker.settings.domain

class ThemePreferenceInteractorImpl(private val repository: ThemePreferencesRepository): ThemePreferenceInteractor {

    override fun getCurrentTheme(): Boolean {
        return repository.getCurrentTheme()
    }

    override fun switchTheme(darkThemeEnabled: Boolean) {
        repository.switchTheme(darkThemeEnabled)
    }
}
