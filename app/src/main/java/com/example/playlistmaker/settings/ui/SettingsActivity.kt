package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        setSupportActionBar(binding.stToolbar)
        binding.stToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.share.setOnClickListener { shareApp() }
        binding.support.setOnClickListener { sendSupportEmail() }
        binding.agreement.setOnClickListener { openUserAgreement() }

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.switchTheme(isChecked)
        }

        settingsViewModel.isDarkTheme.observe(this) { isDarkTheme ->
            binding.themeSwitcher.isChecked = isDarkTheme
        }
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            val shareMessage = getString(R.string.android_course_link)
            putExtra(Intent.EXTRA_TEXT, shareMessage)
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
