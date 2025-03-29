package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.di.networkModule
import com.example.playlistmaker.di.trackInteractorModule
import com.example.playlistmaker.di.trackRepoModule
import com.example.playlistmaker.di.viewModelModule
import com.example.playlistmaker.settings.domain.ThemePreferenceInteractor
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class App : Application() {

    private val themePreferencesInteractor: ThemePreferenceInteractor by inject()

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(networkModule, trackRepoModule, trackInteractorModule, viewModelModule)
        }
        val isDarkTheme = themePreferencesInteractor.getCurrentTheme()

        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
