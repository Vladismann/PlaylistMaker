package com.example.playlistmaker.universalUiComponents

import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track

@Composable
fun CustomTopBar(
    titleText: String,
) {
    TopAppBar(
        title = {
            Text(
                text = titleText,
                fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                fontSize = dimensionResource(R.dimen.default_text_size).value.sp,
                color = colorResource(R.color.defaultTextColor)
            )
        },
        backgroundColor = Color.Transparent,
        elevation = 0.dp,
    )
}

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(R.color.defaultTextColor)),
            shape = RoundedCornerShape(54.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            modifier = Modifier.height(40.dp)
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                color = colorResource(R.color.defaultBackground),
                letterSpacing = 0.sp
            )
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

@Composable
fun ErrorView(icon: Painter, text: String, showRetry: Boolean, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),   // занимаем всё пространство
        contentAlignment = Alignment.Center  // и центрируем содержимое
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = Color.Unspecified
            )
            Text(
                text = text,
                modifier = Modifier.padding(top = 16.dp),
                color = colorResource(R.color.defaultTextColor),
                fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                fontSize = 19.sp,
                textAlign = TextAlign.Center
            )
            if (showRetry) {
                ActionButton(onClick = onRetry, text = stringResource(R.string.refresh))
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