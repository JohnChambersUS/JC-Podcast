package us.johnchambers.podcast.playlists

import us.johnchambers.podcast.database.EpisodeTable
import us.johnchambers.podcast.objects.Docket

/**
 * Created by johnchambers on 3/11/18.
 */
class EmptyPlaylist(docket : Docket) : Playlist(docket) {

    override fun isEmpty(): Boolean {
        return true;
    }

    override fun alignWithNowPlayingInfo() {}

    override fun setCurrentEpisodeIndex() {}


    override fun updatePlaylistInfo() {}

    override fun getNextEpisode(): EpisodeTable {
        return EpisodeTable()
    }

    override fun setCurrentEpisode(eid: String) {}


}