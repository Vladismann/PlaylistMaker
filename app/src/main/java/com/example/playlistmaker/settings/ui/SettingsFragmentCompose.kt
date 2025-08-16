package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragmentCompose : Fragment() {

    private val settingsViewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setContent {
                val isDarkTheme by settingsViewModel.isDarkTheme.observeAsState(false)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = colorResource(id = R.color.defaultBackground))
                ) {
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(R.string.settings),
                                fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                                fontSize = dimensionResource(R.dimen.default_text_size).value.sp,
                                color = colorResource(R.color.defaultTextColor)
                            )
                        },
                        backgroundColor = Color.Transparent,
                        elevation = 0.dp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Dark Theme Switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.dark_theme),
                            fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                            fontSize = 16.sp,
                            color = colorResource(id = R.color.defaultTextColor),
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { settingsViewModel.switchTheme(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = colorResource(id = R.color.switchThumbActive),  // соответствует state_checked="true"
                                uncheckedThumbColor = colorResource(id = R.color.switchTumbler),
                                checkedTrackColor = colorResource(id = R.color.switchBackgroundActive),  // трек включен
                                uncheckedTrackColor = colorResource(id = R.color.switchBackground)// default
                            ),
                            modifier = Modifier.offset(x = 12.dp)
                        )
                    }
                    SettingsMenuItem(
                        text = stringResource(R.string.share_app),
                        iconRes = R.drawable.share_svg,
                        onClick = { shareApp() }
                    )
                    SettingsMenuItem(
                        text = stringResource(R.string.write_to_support),
                        iconRes = R.drawable.support_svg,
                        onClick = { sendSupportEmail() }
                    )
                    SettingsMenuItem(
                        text = stringResource(R.string.user_agreement),
                        iconRes = R.drawable.arrow_forward,
                        onClick = { openUserAgreement() }
                    )
                }
            }
        }
    }

    @Composable
    private fun SettingsMenuItem(
        text: String,
        @DrawableRes iconRes: Int,
        onClick: () -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                fontSize = 16.sp,
                color = colorResource(id = R.color.defaultTextColor),
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.android_course_link))
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_from)))
    }

    private fun sendSupportEmail() {
        val emailAddress = getString(R.string.student_email)
        val emailSubject = getString(R.string.email_subject)
        val emailBody = getString(R.string.email_body)
        val uri = Uri.parse("mailto:$emailAddress?subject=${Uri.encode(emailSubject)}&body=${Uri.encode(emailBody)}")
        val emailIntent = Intent(Intent.ACTION_SENDTO, uri)
        startActivity(emailIntent)
    }

    private fun openUserAgreement() {
        val url = getString(R.string.user_agreements)
        val termsIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(termsIntent)
    }
}
