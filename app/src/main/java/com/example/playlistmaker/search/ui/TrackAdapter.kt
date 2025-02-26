package com.example.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track

class TrackAdapter (
private var data: List<Track>
) : RecyclerView.Adapter<TrackViewHolder> () {

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun updateData(newData: List<Track>) {
        data = newData
        notifyDataSetChanged()
    }

    fun clear() {
        data = listOf()
        notifyDataSetChanged()
    }

    fun getItems() : List<Track> {
        return data
    }

    fun getItem(position: Int): Track {
        return data[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}