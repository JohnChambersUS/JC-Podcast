package us.johnchambers.podcast.playlists

import us.johnchambers.podcast.database.EpisodeTable
import us.johnchambers.podcast.database.PodcastDatabaseHelper
import us.johnchambers.podcast.misc.C
import us.johnchambers.podcast.objects.Docket

@Suppress("UNUSED_PARAMETER")
class LatestPlaylist(useExisting : Boolean) : Playlist(C.playlist.LATEST_PLAYLIST) {


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

    override fun setCurrentEpisode(eid : String) {
        if (_episodes.size < 1) {
            _episodeIndex = -1
            return
        }

        _episodeIndex = _episodes.size - 1
        do {
            if (_episodes.get(_episodeIndex).eid.equals(eid))
                break
            else
                _episodeIndex--

        } while (_episodeIndex > -1)
    }

    override fun alignWithNowPlayingInfo() {

    }

    override fun getEpisodes() : MutableList<EpisodeTable> {
        refreshEpisodeList()
        return _episodes
    }

    private fun refreshEpisodeList() {
        _episodes.clear()
        var episodeIds = PodcastDatabaseHelper.getInstance().updatedLatestPlaylist
        for (episode in episodeIds) {
            _episodes.add(PodcastDatabaseHelper.getInstance().getEpisodeTableRowByEpisodeId(episode.eid))
        }
    }

    override fun getPlaylistId() : String {
        return C.playlist.LATEST_PLAYLIST
    }



}