package us.johnchambers.podcast.playlists

import us.johnchambers.podcast.database.EpisodeTable
import us.johnchambers.podcast.database.PodcastDatabaseHelper
import us.johnchambers.podcast.objects.Docket

/**
 * Created by johnchambers on 3/11/18.
 */
abstract class Playlist(docket : Docket) {

    lateinit var _docket : Docket
    lateinit var _episodes : List<EpisodeTable>
    var _episodeIndex = -1

    init {
        _docket = docket
    }

    init {}

    abstract fun isEmpty() : Boolean

    abstract fun setCurrentEpisodeIndex()

    abstract protected fun updatePlaylistInfo()

    abstract fun getNextEpisode(): EpisodeTable

    abstract fun setCurrentEpisode(eid : String)

    open fun getPlaylistId() : String {
        return _docket.getId()
    }

    abstract protected fun alignWithNowPlayingInfo()

}