package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val tvTrackName: TextView = itemView.findViewById(R.id.tvTrackName)
    private val tvTrackArtistName: TextView = itemView.findViewById(R.id.tvTrackArtistName)
    private val tvTrackTime: TextView = itemView.findViewById(R.id.tvTrackTime)
    private val ivTrackImage: ImageView = itemView.findViewById(R.id.ivTrackImage)

    fun bind(item: Track) {
        var actualName = item.trackName
        var actualArtistName = item.artistName
        var actualTrackTime = ""

        if (actualName.isBlank()) {
            actualName = itemView.context.getString(R.string.text_waiting)
        }
        if (actualArtistName.isBlank()) {
            actualArtistName = itemView.context.getString(R.string.text_waiting)
        }
        if (item.trackTimeMillis == 0L) {
            actualTrackTime = itemView.context.getString(R.string.text_waiting)
        }

        tvTrackName.text = actualName
        tvTrackArtistName.text = actualArtistName
        tvTrackTime.text = actualTrackTime.toString()

        Glide.with(itemView)
            .load(item.artworkUrl100)
            .transform(RoundedCorners(10))
            .placeholder(R.drawable.placeholder)
            .into(ivTrackImage)
    }
}