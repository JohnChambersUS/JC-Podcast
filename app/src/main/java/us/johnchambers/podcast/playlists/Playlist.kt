package us.johnchambers.podcast.playlists

import us.johnchambers.podcast.database.EpisodeTable
import us.johnchambers.podcast.database.PodcastDatabaseHelper
import us.johnchambers.podcast.misc.C
import us.johnchambers.podcast.objects.Docket

/**
 * Created by johnchambers on 3/11/18.
 */
abstract class Playlist(playlistId : String) {

    lateinit var _docket : Docket
    lateinit var _episodes : MutableList<EpisodeTable>
    var _episodeIndex = -1
    var _playistId = playlistId
    var _newOnly = false;

    init {

    }

    open fun setNewOnly(newValue: Boolean) {
        _newOnly = newValue
    }

    open fun isEmpty() : Boolean {
        return _episodes.isEmpty()
    }

    open fun setCurrentEpisodeIndex(index : Int) {
        if ((index > -1) && (index < _episodes.size))
            _episodeIndex = index;
        else
            _episodeIndex = -1
    }


    abstract protected fun updatePlaylistInfo()

    abstract fun getNextEpisode(): EpisodeTable

    open fun setCurrentEpisode(eid : String) {
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

    open fun getPlaylistId() : String {
        return _playistId
    }

    abstract protected fun alignWithNowPlayingInfo()

    open fun getEpisodes() : MutableList<EpisodeTable> {
        return _episodes
    }

    open fun getCurrentEpisodes() : MutableList<EpisodeTable>  {
        return _episodes
    }

    open fun getEpisode(position: Int) : EpisodeTable {
        try {
            return _episodes.get(position)
        }
        catch(e: Exception) {
            return EpisodeTable()
        }
    }

    open fun removeItem(index: Int) {

    }

    open fun moveItem(source: Int, target: Int) {

    }


}