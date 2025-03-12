package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.models.Track

fun interface OnItemClickListener {
    fun onItemClick(track: Track)
}