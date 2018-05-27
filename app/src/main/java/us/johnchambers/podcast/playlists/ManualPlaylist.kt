package us.johnchambers.podcast.playlists

import us.johnchambers.podcast.database.EpisodeTable
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

    //todo is dummied with emtpy return
    override fun getNextEpisode(): EpisodeTable {
        return EpisodeTable()
    }

    override fun alignWithNowPlayingInfo() {

    }

    private fun refreshEpisodeList() {
        _episodes.clear()
        var episodeIds = PodcastDatabaseHelper.getInstance().updatedLatestPlaylist
        for (episode in episodeIds) {
            _episodes.add(PodcastDatabaseHelper.getInstance().getEpisodeTableRowByEpisodeId(episode.eid))
        }
    }

    override fun getPlaylistId() : String {
        return C.playlist.MANUAL_PLAYLIST
    }





}