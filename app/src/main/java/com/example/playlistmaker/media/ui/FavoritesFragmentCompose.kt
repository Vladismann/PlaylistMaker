package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.media.view_model.FavoritesScreenState
import com.example.playlistmaker.media.view_model.FavoritesViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.universalUiComponents.ErrorView
import com.example.playlistmaker.universalUiComponents.LoadingIndicator
import com.example.playlistmaker.universalUiComponents.TrackItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragmentCompose : Fragment() {

    private val viewModel: FavoritesViewModel by viewModel()
    private var isClickAllowed = true
    private val clickDebounceDelay = 1000L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                FavoritesScreen(
                    viewModel = viewModel,
                    onTrackClick = { track ->
                        if (clickDebounce()) {
                            startTrackFragment(track)
                        }
                    }
                )
            }
        }
    }

    private fun startTrackFragment(track: Track) {
        viewModel.saveForAudioPlayer(track)
        viewLifecycleOwner.lifecycleScope.launch {
            delay(1000)
            findNavController().navigate(R.id.action_global_to_trackFragment)
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(clickDebounceDelay)
                isClickAllowed = true
            }
        }
        return current
    }

    @Composable
    fun FavoritesScreen(
        viewModel: FavoritesViewModel,
        onTrackClick: (Track) -> Unit
    ) {
        val state by viewModel.screenState.collectAsState()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.defaultBackground)),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                is FavoritesScreenState.Loading -> {
                    LoadingIndicator()
                }

                is FavoritesScreenState.Content -> {
                    val tracks = (state as FavoritesScreenState.Content).tracks

                    if (tracks.isEmpty()) {
                        ErrorView(
                            icon = painterResource(id = R.drawable.track_not_found),
                            text = stringResource(R.string.favorites_empty),
                            showRetry = false,
                            onRetry = {}
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 12.dp)
                        ) {
                            items(tracks) { track ->
                                TrackItem(
                                    track = track,
                                    onClick = { onTrackClick(track) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}
