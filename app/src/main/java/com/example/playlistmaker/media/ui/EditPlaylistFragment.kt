package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : CreatePlaylistFragment() {

    override val viewModel: EditPlaylistViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val playlistId = arguments?.getLong("playlistId") ?: -1L
        viewModel.init(playlistId)
        binding.createPlaylist.text = "Сохранить"
        binding.apToolbar.title = "Редактировать"

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })

        binding.createPlaylist.setOnClickListener {
            viewModel.uiState.value?.let { it ->
                if (it.isCreateEnabled) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.createPlaylist()
                        findNavController().popBackStack()
                    }
                } else {
                    Toast.makeText(requireContext(), "Заполните название плейлиста", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            binding.createPlaylist.isEnabled = state.isCreateEnabled
            if (binding.inputNameEdit.text.toString() != state.name) binding.inputNameEdit.setText(state.name)
            if (binding.inputDescrEdit.text.toString() != state.description) binding.inputDescrEdit.setText(state.description)
            if (!state.imagePath.isBlank()) {
                Glide.with(this)
                    .load(state.imagePath.toUri())
                    .placeholder(R.drawable.add_image)
                    .into(binding.image)
            }
        }
    }
}