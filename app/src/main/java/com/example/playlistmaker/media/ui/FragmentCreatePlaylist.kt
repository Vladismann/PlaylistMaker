package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding

class FragmentCreatePlaylist : Fragment() {

    companion object {
        fun newInstance(): FragmentCreatePlaylist {
            return FragmentCreatePlaylist()
        }
    }

    private lateinit var binding: FragmentCreatePlaylistBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        binding.apToolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}