package us.johnchambers.podcast.playlists

import us.johnchambers.podcast.database.EpisodeTable
import us.johnchambers.podcast.database.PodcastDatabaseHelper
import us.johnchambers.podcast.database.PodcastTable

/**
 * Created by johnchambers on 3/11/18.
 */
open class PodcastPlaylist(playlist : String) : Playlist(playlist) {

    var _podcastInfo : PodcastTable

    init {
        _podcastInfo = PodcastDatabaseHelper.getInstance().getPodcastRow(getPlaylistId())
        _episodes = PodcastDatabaseHelper.getInstance().getEpisodesSortedNewest(getPlaylistId())
    }

    //private, protected
    protected override fun updatePlaylistInfo() {
        PodcastDatabaseHelper.getInstance().updatePodcastTableRow(_podcastInfo)
    }

    override fun getNextEpisode() : EpisodeTable {
        //if no episodes return empty
        if (_episodes.isEmpty()) return EpisodeTable()

        //if have and episode index, find it or first unplayed one before it
        if (_episodeIndex > -1) {
            do {
                var currEpisode = PodcastDatabaseHelper.getInstance().getEpisodeTableRowByEpisodeId(_episodes.get(_episodeIndex).eid)
                if (currEpisode == null) continue
                _episodes.set(_episodeIndex, currEpisode)
                if (currEpisode.playPointAsLong < currEpisode.lengthAsLong) {
                    return currEpisode
                }
            } while (--_episodeIndex > -1)
        }

        //if -1 start from top down and look for played or partially played
        _episodeIndex = 0
        do {
            var currEpisode = PodcastDatabaseHelper.getInstance().getEpisodeTableRowByEpisodeId(_episodes.get(_episodeIndex).eid)
            if (currEpisode == null) continue
            _episodes.set(_episodeIndex, currEpisode)
            if (currEpisode.playPointAsLong > 0) { //found a played one
                if (currEpisode.playPointAsLong < currEpisode.lengthAsLong) {
                    return currEpisode
                }
                else {
                    if (_episodeIndex == 0) {
                        return EpisodeTable()
                    }
                    else {
                        _episodeIndex--
                        return _episodes.get(_episodeIndex)
                    }
                }
            }
        } while (++_episodeIndex < _episodes.size)

        //if there are no unplayed episodes, then play 0 episode.
        _episodeIndex = 0
        var currEpisode = PodcastDatabaseHelper.getInstance().getEpisodeTableRowByEpisodeId(_episodes.get(_episodeIndex).eid)
        if ((currEpisode != null) && (currEpisode.playPointAsLong < currEpisode.lengthAsLong)) {
            return currEpisode
        }

        return EpisodeTable()
    }

    override fun alignWithNowPlayingInfo() {
        if (PodcastDatabaseHelper.getInstance().nowPlayingPlaylist.equals(_podcastInfo.pid)) {
            _podcastInfo.currentEpisode = PodcastDatabaseHelper.getInstance().nowPlayingEpisodeId
            updatePlaylistInfo()
        }
    }

}