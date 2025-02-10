package com.example.playlistmaker.presentation

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

class TrackViewHolder(itemView: View, private val listener: OnItemClickListener?) :
    RecyclerView.ViewHolder(itemView) {

    init {
        itemView.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener?.onItemClick(position)
            }
        }
    }

    private val tvTrackName: TextView = itemView.findViewById(R.id.tvTrackName)
    private val tvTrackArtistName: TextView = itemView.findViewById(R.id.tvTrackArtistName)
    private val tvTrackTime: TextView = itemView.findViewById(R.id.tvTrackTime)
    private val ivTrackImage: ImageView = itemView.findViewById(R.id.ivTrackImage)

    fun bind(item: Track) {
        var actualName = item.trackName
        var actualArtistName = item.artistName
        var actualTrackTime = item.trackTime

        if (actualTrackTime.isNullOrBlank()) {
            actualTrackTime = "0:00"
        }
        if (actualName.isNullOrBlank()) {
            actualName = itemView.context.getString(R.string.text_null)
        }
        if (actualArtistName.isNullOrBlank()) {
            actualArtistName = itemView.context.getString(R.string.text_null)
        }

        tvTrackName.text = actualName
        tvTrackArtistName.text = actualArtistName
        tvTrackTime.text = actualTrackTime

        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .into(ivTrackImage)
    }
}