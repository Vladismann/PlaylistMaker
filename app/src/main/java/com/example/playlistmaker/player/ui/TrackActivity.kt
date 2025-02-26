package com.example.playlistmaker.player.ui;

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.databinding.ActivityAudioplayerBinding
import com.example.playlistmaker.player.view_model.TrackViewModel

class TrackActivity : ComponentActivity() {

    private lateinit var viewModel: TrackViewModel
    private lateinit var binding: ActivityAudioplayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            TrackViewModel.getViewModelFactory( this)
        )[TrackViewModel::class.java]

        viewModel.getLoadingLiveData().observe(this) { isLoading ->
            changeProgressBarVisibility(isLoading)
        }
    }

    private fun changeProgressBarVisibility(visible: Boolean) {
        // Обновляем видимость прогресс-бара
    }
}

