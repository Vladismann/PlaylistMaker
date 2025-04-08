package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment: Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val settingsViewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity()
            }
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.share.setOnClickListener { shareApp() }
        binding.support.setOnClickListener { sendSupportEmail() }
        binding.agreement.setOnClickListener { openUserAgreement() }

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.switchTheme(isChecked)
        }

        settingsViewModel.isDarkTheme.observe(viewLifecycleOwner) { isDarkTheme ->
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