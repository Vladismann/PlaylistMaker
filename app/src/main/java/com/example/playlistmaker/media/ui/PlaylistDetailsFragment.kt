package com.example.playlistmaker.media.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.example.playlistmaker.databinding.PlaylistSmallItemBinding
import com.example.playlistmaker.media.view_model.PlaylistDetailsScreenState
import com.example.playlistmaker.media.view_model.PlaylistDetailsViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistDetailsFragment : Fragment() {

    private var isClickAllowed = true
    private val clickDebounceDelay = 1000L
    private lateinit var binding: FragmentPlaylistDetailsBinding
    private lateinit var itemBinding: PlaylistSmallItemBinding
    private val viewModel by viewModel<PlaylistDetailsViewModel>()
    private var wasEmptySnackbarShown = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        itemBinding = PlaylistSmallItemBinding.bind(binding.root.findViewById(R.id.playlistMenu))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val playlistId = arguments?.getLong("playlistId") ?: -1L
        viewModel.updateDetails(playlistId)
        binding.rvPlaylist.adapter = TrackAdapter(emptyList())

        viewModel.getScreenStateLiveData().observe(viewLifecycleOwner) { screenState ->
            when (screenState) {
                is PlaylistDetailsScreenState.Content -> {
                    loadTrackInfo(screenState)
                }
                else -> {
                }
            }
        }

        binding.apToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })

        (binding.rvPlaylist.adapter as TrackAdapter).setOnItemClickListener { track ->
            if (clickDebounce()) {
                startTrackFragment(track)
            }
        }

        (binding.rvPlaylist.adapter as TrackAdapter).setOnItemLongClickListener { track ->
            showDeleteTrackConfirmationDialog(track)
            true
        }
        binding.plPlaylistMore.setOnClickListener {
            val behavior = BottomSheetBehavior.from(binding.playlistMenu).apply {
                state = BottomSheetBehavior.STATE_EXPANDED
                skipCollapsed = true
                isHideable = true
            }
        }
        binding.plPlaylistShare.setOnClickListener { sharePlaylist() }
        binding.menuShare.setOnClickListener { sharePlaylist() }
        binding.menuDelete.setOnClickListener { showDeletePlaylistConfirmationDialog() }

        binding.menuEdit.setOnClickListener {
            val bundle = Bundle().apply {
                putLong("playlistId", viewModel.actualPlaylistId)
            }
            val navController = findNavController()
            navController.navigate(R.id.action_media_to_editPlaylist, bundle)
        }
    }

    private fun loadTrackInfo(screenState: PlaylistDetailsScreenState.Content) {
        if (!screenState.playlist?.playlistImageUrl.isNullOrEmpty()) {
            Glide.with(this@PlaylistDetailsFragment)
                .load(screenState.playlist?.playlistImageUrl?.toUri())
                .placeholder(R.drawable.placeholder_full_size).into(binding.plPlaylistImage)
            Glide.with(this@PlaylistDetailsFragment)
                .load(screenState.playlist?.playlistImageUrl?.toUri())
                .placeholder(R.drawable.placeholder_full_size).into(itemBinding.rvSmPlaylistImage)
        } else {
            binding.plPlaylistImage.setImageResource(R.drawable.placeholder_full_size)
            itemBinding.rvSmPlaylistImage.setImageResource(R.drawable.placeholder)
        }

        binding.plPlaylistName.text = screenState.playlist?.playlistName
        binding.plPlaylistDescr.text = screenState.playlist?.playlistDescr
        (binding.rvPlaylist.adapter as TrackAdapter).updateData(screenState.tracks)
        binding.plPlaylistTime.text = this@PlaylistDetailsFragment.resources.getQuantityString(
            R.plurals.minute_count, screenState.totalTime.toInt(), screenState.totalTime,
        )
        binding.plPlaylistCount.text = this@PlaylistDetailsFragment.resources.getQuantityString(
            R.plurals.track_count, screenState.trackCount, screenState.trackCount
        )
        itemBinding.rvSmPlaylistName.text = screenState.playlist?.playlistName
        itemBinding.rvSmPlaylistTrackCount.text = this@PlaylistDetailsFragment.resources.getQuantityString(
            R.plurals.track_count, screenState.trackCount, screenState.trackCount
        )
        if (screenState.trackCount == 0 && !wasEmptySnackbarShown) {
            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                "В этом плейлисте нет ни одного трека",
                800
            ).show()
            wasEmptySnackbarShown = true
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(clickDebounceDelay)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun startTrackFragment(track: Track) {
        viewModel.saveForAudioPlayer(track)
        viewLifecycleOwner.lifecycleScope.launch {
            delay(1000)
            val navController = findNavController()
            navController.navigate(R.id.action_global_to_trackFragment)
        }
    }

    private fun showDeleteTrackConfirmationDialog(track: Track) {
        AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
            .setMessage("Хотите удалить трек?")
            .setNegativeButton("НЕТ") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("ДА") { dialog, _ ->
                dialog.dismiss()
                viewModel.deleteTrackFromPlaylist(track.trackId)
            }
            .show()
    }

    private fun showDeletePlaylistConfirmationDialog() {
        AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
            .setMessage("Хотите удалить ${binding.plPlaylistName.text}?")
            .setNegativeButton("НЕТ") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("ДА") { dialog, _ ->
                dialog.dismiss()
                viewModel.deletePlaylist()
                findNavController().popBackStack()
            }
            .show()
    }

    private fun sharePlaylist() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            val shareMessage = viewModel.getShareMessage(binding.plPlaylistCount.text.toString())
            if (shareMessage.isBlank()) {
                AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
                    .setMessage("В этом плейлисте нет списка треков, которым можно поделиться")
                    .setPositiveButton("OK", null)
                    .show()
                return
            }
            putExtra(Intent.EXTRA_TEXT, shareMessage)
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_from)))
    }
}