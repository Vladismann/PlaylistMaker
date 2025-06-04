package com.example.playlistmaker.media.data.converters

import com.example.playlistmaker.media.data.db.PlaylistEntity
import com.example.playlistmaker.media.domain.models.Playlist

class PlaylistDbConverter {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescr,
            playlist.playlistImageUrl)
    }

    fun map(playlist: PlaylistEntity, trackIdkList: List<Long>): Playlist {
        return Playlist(playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescr,
            playlist.playlistImageUrl,
            trackIdkList)
    }
}