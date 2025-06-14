package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.models.Track

fun interface OnItemLongClickListener {

    fun onItemLongClick(track: Track): Boolean
}