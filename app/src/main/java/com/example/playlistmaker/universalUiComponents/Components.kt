package com.example.playlistmaker.universalUiComponents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R

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
