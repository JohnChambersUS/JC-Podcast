package us.johnchambers.podcast.playlists

import us.johnchambers.podcast.database.PodcastDatabaseHelper
import us.johnchambers.podcast.misc.C
import us.johnchambers.podcast.misc.Constants
import us.johnchambers.podcast.objects.*

/**
 * Created by johnchambers on 3/11/18.
 */
object PlaylistFactory {

    fun getPlaylist(docket: Docket): Playlist {
        if (docket._docketType.equals(C.dockett.TYPE_IS_PODCAST)) {
            var podcastInfo = PodcastDatabaseHelper.getInstance().getPodcastRow(docket.getId())
            if (podcastInfo.mode.equals(Constants.PLAYBACK_MODE_PODCAST))
                return PodcastPlaylist(docket.getId())
            else
                return BookPlaylist(docket.getId())
        }

        if (docket._docketType.equals(C.dockett.TYPE_IS_EPISODE)) {
            var eid = docket.getId()
            var episodeRow = PodcastDatabaseHelper.getInstance().getEpisodeTableRowByEpisodeId(eid)
            var podcastInfo = PodcastDatabaseHelper.getInstance().getPodcastRow(episodeRow.pid)
            var pl : Playlist
            if (podcastInfo.mode.equals(Constants.PLAYBACK_MODE_PODCAST)) {
                pl = PodcastPlaylist(episodeRow.pid)
            }
            else {
                pl = BookPlaylist(episodeRow.pid)
            }
            pl.setCurrentEpisode(eid)
            return pl
        }

        if (docket._docketType.equals(C.dockett.TYPE_IS_EMPTY)) {
            return processEmptyDockett()
        }

        if (docket._docketType.equals(C.dockett.TYPE_IS_LATEST)) {
            return LatestPlaylist(useExisting = false)
        }

        if (docket._docketType.equals(C.dockett.TYPE_IS_MANUAL)) {
            return ManualPlaylist()
        }

        if (docket._docketType.equals(C.dockett.TYPE_IS_EMBEDED_PLAYLIST)) {
            return (docket as DocketEmbededPlaylist).getPlaylist()
        }

        if (docket._docketType.equals(C.dockett.TYPE_IS_TAG_ALL_PLAYLIST)) {
            return TagAllPlaylist(false, docket.getId()) //tag id is stored as id
        }

        return EmptyPlaylist()
    }

    private fun processEmptyDockett() : Playlist {
        var npPlaylistId = NowPlaying.getPlaylistId()
        var npEpisode = NowPlaying.getEpisodeId()

        //if no-playlist-flag or ""
        //  return empty playlist
        if ((npPlaylistId.equals("")) || (npPlaylistId.equals(NowPlaying.NO_PLAYLIST_FLAG)))
            return EmptyPlaylist()

        //if latest-playlist-flag
        //  make new latest-playlist
        //  set current episode to one in playlist
        //  return latest-playlist
        if (npPlaylistId.equals(C.playlist.LATEST_PLAYLIST)) {
            var pl = LatestPlaylist(useExisting = true)
            pl.setCurrentEpisode(npEpisode)
            return pl
        }

        //if manual-playlist-flag
        //  make new manual-playlist
        //  set current episode to one in playlist
        //  return latest-playlist
        if (npPlaylistId.equals(C.playlist.MANUAL_PLAYLIST)) {
            var pl = ManualPlaylist();
            pl.setCurrentEpisode(npEpisode)
            return pl
        }

        //if playlist id starts with pid
        //  build podcast-playlist
        //  set to episode in now playing
        //  return podcast-playlist
        var parts = npPlaylistId.split("_")
        if (parts.size > 1) {
            if (parts.get(0).equals("pid")) {
                var pl = PodcastPlaylist(npPlaylistId)
                pl.setCurrentEpisode(npEpisode)
                return pl
            }
        }

        //if playlist exists - count number of episodes in playlist
        //if count > 0
        //  build generic playlist
        //  return generic playlist
        var playlistCount = PodcastDatabaseHelper.getInstance().getPlaylistCount(npPlaylistId)
        if (playlistCount > 0) {
            var pl = TagAllPlaylist(true, npPlaylistId)
            pl.setCurrentEpisode(npEpisode)
            return pl
        }

        //final
        //  return empty playlist
        return EmptyPlaylist()

    }
}