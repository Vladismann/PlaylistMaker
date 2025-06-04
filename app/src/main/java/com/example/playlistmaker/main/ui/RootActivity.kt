package com.example.playlistmaker.main.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityRootBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class RootActivity : AppCompatActivity() {

    companion object {
        const val LAST_SELECTED_FRAGMENT = "selected_item_id"
    }

    private lateinit var binding: ActivityRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container_view) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)
        val selectedItemId = savedInstanceState?.getInt(LAST_SELECTED_FRAGMENT) ?: R.id.mediaFragment
        bottomNavigationView.selectedItemId = selectedItemId

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.createPlaylistFragment -> {
                    binding.bottomNavigationViewLine.visibility = View.GONE
                    bottomNavigationView.visibility = View.GONE
                }

                R.id.trackFragment -> {
                    binding.bottomNavigationViewLine.visibility = View.GONE
                    bottomNavigationView.visibility = View.GONE
                }

                else -> {
                    binding.bottomNavigationViewLine.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val selectedItemId = binding.bottomNavigationView.selectedItemId
        outState.putInt(LAST_SELECTED_FRAGMENT, selectedItemId)
    }
}