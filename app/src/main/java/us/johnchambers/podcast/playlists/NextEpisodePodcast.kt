package us.johnchambers.podcast.playlists

import us.johnchambers.podcast.database.EpisodeTable
import us.johnchambers.podcast.database.PodcastDatabaseHelper

class NextEpisodePodcast {

    fun getNextEpisode(episodes: MutableList<EpisodeTable>, index: Int ) : Int {
        //if no episodes return empty
        if (episodes.isEmpty()) return -1
        var episodeIndex = index
        //if have an episode index, find it or first unplayed one before it
        if (episodeIndex > -1) {
            do {
                var currEpisode = PodcastDatabaseHelper.getInstance().getEpisodeTableRowByEpisodeId(episodes.get(episodeIndex).eid)
                if (currEpisode == null) continue
                episodes.set(episodeIndex, currEpisode)
                if (currEpisode.playPointAsLong < currEpisode.lengthAsLong) {
                    return episodeIndex
                }
            } while (--episodeIndex > -1)
        }

        //if -1 start from top down and look for played or partially played
        episodeIndex = 0
        do {
            var currEpisode = PodcastDatabaseHelper.getInstance().getEpisodeTableRowByEpisodeId(episodes.get(episodeIndex).eid)
            if (currEpisode == null) continue
            episodes.set(episodeIndex, currEpisode)
            if (currEpisode.playPointAsLong > 0) { //found a played one
                if (currEpisode.playPointAsLong < currEpisode.lengthAsLong) {
                    return episodeIndex
                }
                else {
                    if (episodeIndex == 0) {
                        return -1
                    }
                    else {
                        episodeIndex--
                        return episodeIndex
                    }
                }
            }
        } while (++episodeIndex < episodes.size)

        //if there are no unplayed episodes, then play 0 episode.
        episodeIndex = 0
        var currEpisode = PodcastDatabaseHelper.getInstance().getEpisodeTableRowByEpisodeId(episodes.get(episodeIndex).eid)
        if ((currEpisode != null) && (currEpisode.playPointAsLong < currEpisode.lengthAsLong)) {
            return episodeIndex
        }

        return -1
    }



}