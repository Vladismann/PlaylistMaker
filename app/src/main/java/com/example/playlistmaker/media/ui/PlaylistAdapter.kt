package com.example.playlistmaker.media.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.models.Playlist

class PlaylistAdapter(private var playlist: List<Playlist>): RecyclerView.Adapter<PlaylistViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_item, parent, false)
        return PlaylistViewHolder(view)
    }

    private var listener: PlaylistClickListener? = null

    fun setOnItemClickListener(listener: PlaylistClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return playlist.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlist[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener {
            listener?.onItemClick(playlist)
        }
    }

    fun updateData(newData: List<Playlist>) {
        playlist = newData
        notifyDataSetChanged()
    }
}