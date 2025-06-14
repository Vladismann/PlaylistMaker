package com.example.playlistmaker.media.ui

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.media.view_model.CreatePlaylistViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

open class CreatePlaylistFragment : Fragment() {
    protected open val viewModel: CreatePlaylistViewModel by viewModel()
    protected lateinit var binding: FragmentCreatePlaylistBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        binding.apToolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
                binding.createPlaylist.isEnabled = state.isCreateEnabled
                if (binding.inputNameEdit.text.toString() != state.name) binding.inputNameEdit.setText(state.name)
                if (binding.inputDescrEdit.text.toString() != state.description) binding.inputDescrEdit.setText(state.description)
        }

        binding.inputNameEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onNameChanged(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.inputDescrEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onDescriptionChanged(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.image.setImageURI(uri)
                saveImageToPrivateStorage(uri)
            }
        }
        binding.addImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.createPlaylist.setOnClickListener {
            viewModel.uiState.value?.let { it1 ->
                if (it1.isCreateEnabled) {
                    val name = viewModel.uiState.value?.name
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.createPlaylist()
                        Toast.makeText(requireContext(), "Плейлист $name создан", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                } else {
                    Toast.makeText(requireContext(), "Заполните название плейлиста", Toast.LENGTH_SHORT).show()
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val state = viewModel.uiState.value
                if (state != null && (state.name.isNotEmpty() || state.description.isNotEmpty() || state.imagePath.isNotEmpty())) {
                    showExitConfirmationDialog()
                } else {
                    findNavController().popBackStack()
                }
            }
        })
    }

    private fun saveImageToPrivateStorage(uri: Uri) {
        val filePath = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlistMaker")
        if (!filePath.exists()) filePath.mkdirs()
        val randomFileName = "${System.currentTimeMillis()}.jpg"
        val file = File(filePath, randomFileName)
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory.decodeStream(inputStream).compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        inputStream?.close()
        outputStream.close()
        viewModel.onImagePathChanged(uri.toString())
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Завершить") { _, _ -> findNavController().popBackStack() }.create().show()
    }
}