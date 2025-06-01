package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentPlaylists : Fragment() {

    companion object {
        fun newInstance(): FragmentPlaylists {
            return FragmentPlaylists()
        }
    }

    private val viewModel: PlaylistsViewModel by viewModel()
    private lateinit var binding: FragmentPlaylistsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)

        binding.createPlaylist.setOnClickListener {
            parentFragment?.findNavController()?.navigate(R.id.action_media_to_newPlaylist)
        }
        return binding.root
    }
}