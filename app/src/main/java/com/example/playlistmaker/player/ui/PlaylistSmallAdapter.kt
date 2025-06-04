package com.example.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.models.Playlist

class PlaylistSmallAdapter(private var playlist: List<Playlist>): RecyclerView.Adapter<PlaylistSmallViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistSmallViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_small_item, parent, false)
        return PlaylistSmallViewHolder(view)
    }

    private var listener: OnSmallPlaylistClickListener? = null

    fun setOnItemClickListener(listener: OnSmallPlaylistClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return playlist.size
    }

    override fun onBindViewHolder(holder: PlaylistSmallViewHolder, position: Int) {
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