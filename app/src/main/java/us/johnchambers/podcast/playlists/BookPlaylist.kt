package us.johnchambers.podcast.playlists

import us.johnchambers.podcast.database.EpisodeTable
import us.johnchambers.podcast.database.PodcastDatabaseHelper

class BookPlaylist(playlist: String) : PodcastPlaylist(playlist) {

    override fun getNextEpisode() : EpisodeTable {
        //if no episodes return empty
        if (_episodes.isEmpty()) return EpisodeTable()

        if (_episodeIndex == -1)
            _episodeIndex = _episodes.size - 1
        do {
            var currEpisode = PodcastDatabaseHelper.getInstance().getEpisodeTableRowByEpisodeId(_episodes.get(_episodeIndex).eid)
            if (currEpisode == null) continue
            _episodes.set(_episodeIndex, currEpisode)
            if (currEpisode.playPointAsLong < currEpisode.lengthAsLong) {
                return currEpisode
            }
        } while (--_episodeIndex > -1)

        return EpisodeTable()
    }

}