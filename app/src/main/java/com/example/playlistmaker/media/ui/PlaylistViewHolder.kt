package com.example.playlistmaker.media.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.models.Playlist

class PlaylistViewHolder (view: View): RecyclerView.ViewHolder(view) {

    private val image: ImageView = itemView.findViewById(R.id.rvPlaylistImage)
    private val name: TextView = itemView.findViewById(R.id.rvPlaylistName)
    private val trackCount: TextView = itemView.findViewById(R.id.rvPlaylistTrackCount)

    fun bind(playlist: Playlist) {
        val uriString = playlist.playlistImageUrl
        if (!uriString.isNullOrEmpty()) {
            Glide.with(itemView.context)
                .load(uriString.toUri())
                .placeholder(R.drawable.placeholder)
                .into(image)
        } else {
            image.setImageResource(R.drawable.placeholder)
        }
        name.text = playlist.playlistName
        if (!playlist.trackIds.isNullOrEmpty()) {
            val count = playlist.trackIds.count()
            trackCount.text = itemView.context.resources.getQuantityString(
                R.plurals.track_count,
                count,
                count
            )
        } else {
            trackCount.text = "0 треков"
        }
    }
}