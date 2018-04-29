package us.johnchambers.podcast.objects

import us.johnchambers.podcast.misc.C
import us.johnchambers.podcast.playlists.Playlist

class DocketEmbededPlaylist(playlist: Playlist) : Docket(playlist.getPlaylistId()) {

    private var _playlist = playlist

    init {
        _docketType = C.dockett.TYPE_IS_EMBEDED_PLAYLIST
    }

    fun getPlaylist() : Playlist {
        return _playlist
    }
}