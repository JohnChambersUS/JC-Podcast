package us.johnchambers.podcast.playlists

import us.johnchambers.podcast.database.EpisodeTable
import us.johnchambers.podcast.database.PlaylistTable
import us.johnchambers.podcast.database.PodcastDatabaseHelper
import us.johnchambers.podcast.misc.C

@Suppress("UNUSED_PARAMETER")
class ManualPlaylist() : Playlist(C.playlist.MANUAL_PLAYLIST){


    init {
        _episodes = mutableListOf()
        refreshEpisodeList()
    }

    override fun updatePlaylistInfo() {

    }

    override fun getNextEpisode(): EpisodeTable {
        if (_episodes.size == 0) {
            return EpisodeTable()
        }
        if (_episodeIndex == -1) {
            setCurrentEpisodeIndex(_episodes.size - 1)
        }
        var attempts = _episodes.size
        var foundIt = false
        do {
            var currEpisode = PodcastDatabaseHelper.getInstance().getEpisodeTableRowByEpisodeId(_episodes.get(_episodeIndex).eid)
            _episodes.set(_episodeIndex, currEpisode)
            if (currEpisode.playPointAsLong < currEpisode.lengthAsLong) {
                foundIt = true
            }
            else {
                _episodeIndex--
                if (_episodeIndex < 0) {
                    _episodeIndex = _episodes.size - 1
                }
            }
        } while(!foundIt && (--attempts > 0))
        if (foundIt)
            return _episodes.get(_episodeIndex)
        else
            return EpisodeTable()
    }

    override fun alignWithNowPlayingInfo() {

    }

    override fun getEpisodes(): MutableList<EpisodeTable> {
        refreshEpisodeList()
        return _episodes
    }

    private fun refreshEpisodeList() {
        _episodes.clear()
        var playlist = PodcastDatabaseHelper.getInstance().manualPlaylistEntries
        for (item in playlist) {
            try {
                _episodes.add(PodcastDatabaseHelper.getInstance().getEpisodeTableRowByEpisodeId(item.eid))
            }
            catch (e: Exception) {
                //todo gargage in playlist table, needs to be removed
            }
        }
    }

    override fun getPlaylistId() : String {
        return C.playlist.MANUAL_PLAYLIST
    }

    override fun removeItem(index: Int) {
        PodcastDatabaseHelper.getInstance().removeItemFromPlaylistTable(C.playlist.MANUAL_PLAYLIST, _episodes.get(index).eid)
        refreshEpisodeList()
    }





}