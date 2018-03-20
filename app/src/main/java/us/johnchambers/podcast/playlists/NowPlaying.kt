package us.johnchambers.podcast.playlists

import us.johnchambers.podcast.database.PodcastDatabaseHelper

/**
 * Constants class for NowPlayingTable
 * Created by johnchambers on 2/21/18.
 */
object NowPlaying {

    const val NO_PLAYLIST_FLAG = "dummy"
    const val NO_EPISODE_FLAG = "dummy"
    const val EID = "eid"
    const val PLAYLIST = "playlist"

    fun episodeInProgress() : Boolean {
        return !(PodcastDatabaseHelper.getInstance().nowPlayingEpisodeId.equals(NO_EPISODE_FLAG))
    }

    fun playlistInProgress() : Boolean {
        return !(PodcastDatabaseHelper.getInstance().nowPlayingPlaylist.equals(NO_PLAYLIST_FLAG))
    }

    fun getEpisodeId() : String {
        return PodcastDatabaseHelper.getInstance().nowPlayingEpisodeId
    }

    fun setEpisodeId(eid:String) {
        PodcastDatabaseHelper.getInstance().updateNowPlayingEpisode(eid)
    }

    fun getPlaylistId() : String {
        return PodcastDatabaseHelper.getInstance().nowPlayingPlaylist
    }

    fun setPlaylistId(playlistId:String) {
        PodcastDatabaseHelper.getInstance().updateNowPlayingPlaylist(playlistId)
    }

    fun update(playlistId : String, episodeId : String) {
        setEpisodeId(episodeId)
        setPlaylistId(playlistId)
    }

    fun updateEpisodePlayPointAndLength(playPoint : Long, length : Long) {
        PodcastDatabaseHelper.getInstance().updateEpisodePlayPoint(getEpisodeId(), playPoint)
        PodcastDatabaseHelper.getInstance().updateEpisodeDuration(getEpisodeId(), length)
    }

}