package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest.Builder
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.view_model.PlaylistScreenState
import com.example.playlistmaker.media.view_model.PlaylistsViewModel
import com.example.playlistmaker.universalUiComponents.ActionButton
import com.example.playlistmaker.universalUiComponents.ErrorView
import com.example.playlistmaker.universalUiComponents.LoadingIndicator
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragmentCompose : Fragment() {

    private val viewModel: PlaylistsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                PlaylistsScreen(
                    viewModel = viewModel,
                    onPlaylistClick = { playlistId ->
                        findNavController().navigate(
                            R.id.action_media_to_playlistDetails,
                            Bundle().apply { putLong("playlistId", playlistId) }
                        )
                    },
                    onCreatePlaylistClick = {
                        findNavController().navigate(R.id.action_media_to_newPlaylist)
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPlaylists()
    }

    @Composable
    fun PlaylistsScreen(
        viewModel: PlaylistsViewModel,
        onPlaylistClick: (Long) -> Unit,
        onCreatePlaylistClick: () -> Unit
    ) {
        val state by viewModel.screenState.collectAsState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.defaultBackground))
        ) {
            // Верхний блок: кнопка и сообщение об ошибке
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                ActionButton(stringResource(R.string.create_playlist), onCreatePlaylistClick)

                Spacer(modifier = Modifier.height(16.dp))

                if (state is PlaylistScreenState.Content && (state as PlaylistScreenState.Content).playlists.isEmpty()) {
                    ErrorView(
                        icon = painterResource(R.drawable.track_not_found),
                        text = stringResource(R.string.playlists_empty),
                        showRetry = false,
                        onRetry = {},
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }

            // Список занимает оставшееся место
            if (state is PlaylistScreenState.Content && (state as PlaylistScreenState.Content).playlists.isNotEmpty()) {
                val playlists = (state as PlaylistScreenState.Content).playlists

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(8.dp),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(playlists) { playlist ->
                        playlist.playlistId?.let { id ->
                            PlaylistItem(
                                playlist = playlist,
                                onClick = { onPlaylistClick(id) }
                            )
                        }
                    }
                }
            }

            // Загрузка в любом случае сверху
            if (state is PlaylistScreenState.Loading) {
                LoadingIndicator()
            }
        }
    }


    @Composable
    fun PlaylistItem(
        playlist: Playlist,
        onClick: () -> Unit
    ) {
        val painter = rememberAsyncImagePainter(Builder(LocalContext.current).data(data = playlist.playlistImageUrl ?: "").apply(block = { ->
            placeholder(R.drawable.placeholder)
            error(R.drawable.placeholder)
            crossfade(true)
        }).build())

        Card(
            modifier = Modifier
                .padding(4.dp)
                .width(160.dp)
                .clickable { onClick() },
            elevation = 0.dp,
            backgroundColor = colorResource(id = R.color.defaultBackground)
        ) {
            Column {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(4.dp))

                Column(
                    modifier = Modifier
                        .height(32.dp)
                        .padding(horizontal = 4.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = playlist.playlistName ?: "",
                        color = colorResource(R.color.defaultTextColor),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = FontFamily(Font(R.font.ys_display_regular))
                    )

                    val trackCountText = if (!playlist.trackIds.isNullOrEmpty()) {
                        val count = playlist.trackIds.count()
                        pluralStringResource(id = R.plurals.track_count, count = count, count)
                    } else {
                        "0 треков"
                    }

                    Text(
                        text = trackCountText,
                        color = colorResource(R.color.defaultTextColor),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = FontFamily(Font(R.font.ys_display_regular))
                    )
                }
            }
        }
    }


}





