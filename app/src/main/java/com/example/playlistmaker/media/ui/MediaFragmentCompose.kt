package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.example.playlistmaker.R
import com.example.playlistmaker.universalUiComponents.CustomTopBar
import kotlinx.coroutines.launch

class MediaFragmentCompose : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MediaScreen()
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun MediaScreen() {
        val scope = rememberCoroutineScope()
        val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.defaultBackground))
        ) {
            CustomTopBar(titleText = stringResource(R.string.media))

            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = colorResource(id = R.color.defaultBackground),
                contentColor = colorResource(id = R.color.defaultTextColor),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = colorResource(id = R.color.defaultTextColor)
                    )
                }
            ) {
                CustomTab(
                    text = stringResource(id = R.string.favorites),
                    selected = pagerState.currentPage == 0,
                    onClick = { scope.launch { pagerState.animateScrollToPage(0) } }
                )
                CustomTab(
                    text = stringResource(id = R.string.playlists),
                    selected = pagerState.currentPage == 1,
                    onClick = { scope.launch { pagerState.animateScrollToPage(1) } }
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> FragmentContainer(fragment = { FavoritesFragment() })
                    1 -> FragmentContainer(fragment = { PlaylistsFragment() })
                }
            }
        }
    }

    @Composable
    fun FragmentContainer(fragment: () -> Fragment) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                FragmentContainerView(context).apply {
                    id = View.generateViewId()
                    (context as AppCompatActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(id, fragment())
                        .commit()
                }
            }
        )
    }

    @Composable
    fun CustomTab(
        text: String,
        selected: Boolean,
        onClick: () -> Unit
    ) {
        Tab(
            selected = selected,
            onClick = onClick,
            text = {
                Text(
                    text = text,
                    color = colorResource(id = R.color.defaultTextColor),
                    fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                    fontSize = 14.sp
                )
            }
        )
    }

}
