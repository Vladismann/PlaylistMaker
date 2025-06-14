package com.example.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track

class TrackAdapter(
    private var data: List<Track>
) : RecyclerView.Adapter<TrackViewHolder>() {

    private var listener: OnItemClickListener? = null
    private var longClickListener: OnItemLongClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        this.longClickListener = listener
    }

    fun updateData(newData: List<Track>) {
        data = newData
        notifyDataSetChanged()
    }

    fun clear() {
        data = listOf()
        notifyDataSetChanged()
    }

    fun removeItem(trackId: Long?) {
        val newList = data.toMutableList().apply {
            removeAll { it.trackId == trackId }
        }
        updateData(newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = data[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            listener?.onItemClick(track)
        }
        holder.itemView.setOnLongClickListener {
            longClickListener?.onItemLongClick(track) ?: false
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}