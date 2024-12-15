package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

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

        val actualTrackTime: String = if (item.trackTimeMillis == null) {
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(0L)
        } else {
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(item.trackTimeMillis)
        }

        if (actualName.isNullOrBlank()) {
            actualName = itemView.context.getString(R.string.text_empty)
        }
        if (actualArtistName.isNullOrBlank()) {
            actualArtistName = itemView.context.getString(R.string.text_empty)
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