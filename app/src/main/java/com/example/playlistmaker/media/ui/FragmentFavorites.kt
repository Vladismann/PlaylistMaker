package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.media.view_model.FavoritesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentFavorites : Fragment() {
    companion object {
        fun newInstance(): FragmentFavorites {
            return FragmentFavorites()
        }
    }

    private val viewModel: FavoritesViewModel by viewModel()
    private lateinit var binding: FragmentFavoritesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }
}