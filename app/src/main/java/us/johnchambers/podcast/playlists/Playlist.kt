package us.johnchambers.podcast.playlists

import us.johnchambers.podcast.database.EpisodeTable
import us.johnchambers.podcast.database.PodcastDatabaseHelper
import us.johnchambers.podcast.objects.Docket

/**
 * Created by johnchambers on 3/11/18.
 */
abstract class Playlist(playlistId : String) {

    lateinit var _docket : Docket
    lateinit var _episodes : MutableList<EpisodeTable>
    var _episodeIndex = -1
    var _plalyistId = playlistId

    init {

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
        return _plalyistId
    }

    abstract protected fun alignWithNowPlayingInfo()

    open fun getEpisodes() : MutableList<EpisodeTable> {
        return _episodes
    }

}