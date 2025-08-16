package com.example.playlistmaker.search.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.view_model.SearchScreenState
import com.example.playlistmaker.search.view_model.SearchViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragmentCompose : Fragment() {

    private var isClickAllowed = true
    private val clickDebounceDelay = 1000L
    private val viewModel by viewModel<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity()
            }
        })

        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    SearchScreen(
                        viewModel = viewModel,
                        onTrackClick = ::handleTrackClick,
                        onHistoryTrackClick = ::handleTrackClick,
                        onClearHistory = viewModel::clearHistory,
                        onRetry = { viewModel.searchDebounce(viewModel.lastQuery) },
                        onClearSearch = { viewModel.searchDebounce("") }
                    )
                }
            }
        }
    }

    private fun handleTrackClick(track: Track) {
        if (clickDebounce()) {
            viewModel.saveTrackToHistory(track)
            viewModel.saveForAudioPlayer(track)
            viewLifecycleOwner.lifecycleScope.launch {
                delay(1000)
                findNavController().navigate(R.id.action_global_to_trackFragment)
            }
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
    fun SearchScreen(
        viewModel: SearchViewModel,
        onTrackClick: (Track) -> Unit,
        onHistoryTrackClick: (Track) -> Unit,
        onClearHistory: () -> Unit,
        onRetry: () -> Unit,
        onClearSearch: () -> Unit
    ) {
        val state by viewModel.searchScreenState.observeAsState(SearchScreenState.Content(emptyList(), emptyList(), ""))
        var query by viewModel.query

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.defaultBackground))
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.search),
                        fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                        fontSize = dimensionResource(R.dimen.default_text_size).value.sp,
                        color = colorResource(R.color.defaultTextColor)
                    )
                },
                backgroundColor = Color.Transparent,
                elevation = 0.dp
            )

            SearchInput(
                query = query,
                onQueryChange = { newQuery ->
                    query = newQuery
                    viewModel.searchDebounce(newQuery)
                },
                onClearSearch = {
                    query = ""
                    onClearSearch()
                    viewModel.searchDebounce(query)
                },
                onDone = {}
            )

            when (state) {
                is SearchScreenState.Loading -> LoadingIndicator()
                is SearchScreenState.Content -> SearchContent(
                    state = state as SearchScreenState.Content,
                    query = query,
                    onTrackClick = onTrackClick,
                    onHistoryTrackClick = onHistoryTrackClick,
                    onClearHistory = onClearHistory
                )

                is SearchScreenState.Error -> {
                    val error = state as SearchScreenState.Error
                    ErrorView(
                        icon = painterResource(R.drawable.track_search_error),
                        text = stringResource(R.string.connection_error),
                        showRetry = error.showRefresh,
                        onRetry = onRetry
                    )
                }
            }
        }
    }

    @Composable
    fun LoadingIndicator() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            CircularProgressIndicator(
                modifier = Modifier.padding(top = 150.dp),
                color = colorResource(R.color.YP_Blue)
            )
        }
    }

    @Composable
    fun SearchContent(
        state: SearchScreenState.Content,
        query: String,
        onTrackClick: (Track) -> Unit,
        onHistoryTrackClick: (Track) -> Unit,
        onClearHistory: () -> Unit
    ) {
        if (state.tracks.isEmpty() && state.query.isNotBlank()) {
            ErrorView(
                icon = painterResource(R.drawable.track_not_found),
                text = stringResource(R.string.nothing_found),
                showRetry = false,
                onRetry = {}
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ) {
                items(state.tracks) { track ->
                    TrackItem(track = track, onClick = { onTrackClick(track) })
                }
            }
        }

        if (query.isEmpty() && state.historyTracks.isNotEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.search_history_header),
                    color = colorResource(R.color.defaultTextColor),
                    fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                    fontSize = 19.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                ) {
                    items(state.historyTracks) { track ->
                        TrackItem(track = track, onClick = { onHistoryTrackClick(track) })
                    }
                }
                Button(
                    onClick = onClearHistory,
                    colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(R.color.defaultTextColor)),
                    shape = RoundedCornerShape(54.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 24.dp, bottom = 16.dp)
                        .height(40.dp)
                ) {
                    Text(
                        text = stringResource(R.string.clear_search),
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                        color = colorResource(R.color.defaultBackground),
                        letterSpacing = 0.sp
                    )
                }
            }
        }
    }

    @Composable
    fun SearchInput(
        query: String,
        onQueryChange: (String) -> Unit,
        onClearSearch: () -> Unit,
        onDone: () -> Unit
    ) {
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .background(
                        color = colorResource(R.color.searchPageInput),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.search_small_svg),
                        contentDescription = "Search",
                        tint = colorResource(R.color.searchPagePlaceholder),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (query.isEmpty()) {
                            Text(
                                text = stringResource(R.string.search),
                                color = colorResource(R.color.searchPagePlaceholder),
                                fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                                fontSize = 14.sp,
                                modifier = Modifier.align(Alignment.CenterStart)
                            )
                        }
                        BasicTextField(
                            value = query,
                            onValueChange = onQueryChange,
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = colorResource(R.color.YP_Black),
                                fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                                fontSize = 14.sp
                            ),
                            cursorBrush = SolidColor(colorResource(R.color.YP_Blue)),
                            keyboardActions = KeyboardActions(onDone = { onDone() }),
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterStart)
                        )
                    }
                    if (query.isNotEmpty()) {
                        IconButton(onClick = onClearSearch) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_close),
                                contentDescription = "Clear",
                                tint = colorResource(R.color.searchPagePlaceholder)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ErrorView(icon: Painter, text: String, showRetry: Boolean, onRetry: () -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(painter = icon, contentDescription = null, modifier = Modifier.size(120.dp), tint = Color.Unspecified)
            Text(
                text = text,
                modifier = Modifier.padding(top = 16.dp),
                color = colorResource(R.color.defaultTextColor),
                fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                fontSize = 19.sp,
                textAlign = TextAlign.Center
            )
            if (showRetry) {
                Button(
                    onClick = onRetry,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 24.dp, bottom = 16.dp)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = colorResource(R.color.defaultTextColor)
                    ),
                    shape = RoundedCornerShape(54.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.refresh),
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                        color = colorResource(R.color.defaultBackground),
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.sp
                    )
                }
            }
        }
    }

    @Composable
    fun TrackItem(track: Track, onClick: () -> Unit) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth(),
            factory = { context ->
                LayoutInflater.from(context).inflate(R.layout.track_item, null, false).apply {
                    setOnClickListener { onClick() }
                }
            },
            update = { view ->
                val trackName = view.findViewById<TextView>(R.id.tvTrackName)
                val trackArtist = view.findViewById<TextView>(R.id.tvTrackArtistName)
                val trackTime = view.findViewById<TextView>(R.id.tvTrackTime)
                val trackImage = view.findViewById<ImageView>(R.id.ivTrackImage)

                trackName.text = track.trackName
                trackArtist.text = track.artistName
                trackTime.text = track.trackTime
                Glide.with(view.context)
                    .load(track.artworkUrl100)
                    .placeholder(R.drawable.placeholder)
                    .into(trackImage)
            }
        )
    }
}