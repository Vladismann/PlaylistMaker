package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media.view_model.PlaylistScreenState
import com.example.playlistmaker.media.view_model.PlaylistsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    companion object {
        fun newInstance(): PlaylistsFragment {
            return PlaylistsFragment()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvPlaylist.adapter = PlaylistAdapter(emptyList())

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.screenState.collect { state ->
                    when (state) {
                        is PlaylistScreenState.Loading -> {
                            binding.searchProgressBar.visibility = View.VISIBLE
                            binding.errorMessageElement.visibility = View.GONE
                            binding.rvPlaylist.visibility = View.GONE
                        }

                        is PlaylistScreenState.Content -> {
                            binding.searchProgressBar.visibility = View.GONE
                            if (state.playlists.isEmpty()) {
                                binding.errorMessageElement.visibility = View.VISIBLE
                                binding.rvPlaylist.visibility = View.GONE
                            } else {
                                (binding.rvPlaylist.adapter as PlaylistAdapter).updateData(state.playlists)
                                binding.rvPlaylist.visibility = View.VISIBLE
                                binding.errorMessageElement.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        // Можно добавить триггер для обновления данных
    }
}