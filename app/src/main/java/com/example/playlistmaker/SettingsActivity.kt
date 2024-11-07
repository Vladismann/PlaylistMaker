package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val buttonShare = findViewById<ImageView>(R.id.share)
        val buttonSupport = findViewById<ImageView>(R.id.support)
        val buttonAgreement = findViewById<ImageView>(R.id.agreement)
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        buttonShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                val shareMessage = getString(R.string.android_course_link)
                putExtra(Intent.EXTRA_TEXT, shareMessage)
            }
            startActivity(Intent.createChooser(shareIntent, R.string.share_from.toString()))
        }

        buttonSupport.setOnClickListener {
            val emailAddress = getString(R.string.student_email)
            val emailSubject = getString(R.string.email_subject)
            val emailBody = getString(R.string.email_body)

            val uri = Uri.parse(
                "mailto:$emailAddress?subject=${Uri.encode(emailSubject)}&body=${
                    Uri.encode(emailBody)
                }"
            )
            val emailIntent = Intent(Intent.ACTION_SENDTO, uri)
            startActivity(emailIntent)
        }

        buttonAgreement.setOnClickListener {
            val url = getString(R.string.user_agreements)
            val termsIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            startActivity(termsIntent)
        }

    }
}