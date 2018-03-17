package us.johnchambers.podcast.playlists

import us.johnchambers.podcast.database.EpisodeTable
import us.johnchambers.podcast.database.PodcastDatabaseHelper
import us.johnchambers.podcast.database.PodcastTable
import us.johnchambers.podcast.misc.Constants
import us.johnchambers.podcast.objects.Docket
import kotlin.math.E

/**
 * Created by johnchambers on 3/11/18.
 */
open class PodcastPlaylist(docket : Docket) : Playlist(docket) {

    var _podcastInfo : PodcastTable

    init {
        _podcastInfo = PodcastDatabaseHelper.getInstance().getPodcastRow(docket.getId())
        _episodes = PodcastDatabaseHelper.getInstance().getEpisodesSortedNewest(docket.getId())
        alignWithNowPlayingInfo()
        setCurrentEpisodeIndex()
    }

    override fun isEmpty(): Boolean {
        return _episodes.isEmpty()
    }

    override fun setCurrentEpisodeIndex() {
        if (_episodes.size < 1) return

        if (_podcastInfo.currentEpisode == Constants.NO_CURRENT_EPISODE) {
            _podcastInfo.currentEpisode = _episodes.first().eid
            if (_podcastInfo.mode == Constants.PLAYBACK_MODE_BOOK) {
                _podcastInfo.currentEpisode = _episodes.last().eid
            }
            updatePlaylistInfo()
        }

        if (_episodes.isEmpty()) {
            _episodeIndex = -1
            return
        }
        var foundIt = false
        _episodeIndex = 0
        while ((!foundIt) && (_episodeIndex < _episodes.size)) {
            if (_podcastInfo.currentEpisode.equals(_episodes.get(_episodeIndex).eid)) {
                //if current is completed set to next up, will set to -1 if first is done
                if (_episodes.get(_episodeIndex).playPointAsLong >= _episodes.get(_episodeIndex).lengthAsLong ) {
                    _episodeIndex--
                }
                foundIt = true
            } else {
                _episodeIndex++
            }
        }
        if (!foundIt) {
            _episodeIndex = -1
        }
        _episodeIndex++ //point to one after you want to play for get next
    }

    //private, protected
    protected override fun updatePlaylistInfo() {
        PodcastDatabaseHelper.getInstance().updatePodcastTableRow(_podcastInfo)
    }

    override fun getNextEpisode() : EpisodeTable {
        if (_episodes.isEmpty()) return EpisodeTable()

        var returnET = EpisodeTable()

        var foundIt = false
        while ((!foundIt) && (--_episodeIndex > -1)) {
            var epi = _episodes.get(_episodeIndex)
            var pp = epi.playPointAsLong
            var l = epi.lengthAsLong
            var comp = pp.compareTo(l)

            if (comp.equals(-1)) {
                returnET = epi
                foundIt = true
            }
        }
        return returnET //no more unplayed episodes so returning blank
    }

    override fun setCurrentEpisode(eid: String) {
        _podcastInfo.currentEpisode = eid;
        updatePlaylistInfo()
    }

    override fun alignWithNowPlayingInfo() {
        if (PodcastDatabaseHelper.getInstance().nowPlayingPlaylist.equals(_podcastInfo.pid)) {
            _podcastInfo.currentEpisode = PodcastDatabaseHelper.getInstance().nowPlayingEpisodeId
            updatePlaylistInfo()
        }
    }




}