package us.johnchambers.podcast.playlists

import us.johnchambers.podcast.misc.C
import us.johnchambers.podcast.objects.Docket
import us.johnchambers.podcast.objects.DocketEmpty
import us.johnchambers.podcast.objects.DocketPlaylist

/**
 * Created by johnchambers on 3/11/18.
 */
object PlaylistFactory {

    fun getPlaylist(docket: Docket): Playlist {
        if (docket._docketType.equals(C.dockett.TYPE_IS_PODCAST)) {
            return PodcastPlaylist(docket)
        }

        if (docket._docketType.equals(C.dockett.TYPE_IS_EPISODE)) {
            return EpisodePlaylist(docket)
        }

        if (docket._docketType.equals(C.dockett.TYPE_IS_EMPTY)) {
            var npPlaylistId = NowPlaying.getPlaylistId()
            if ((!npPlaylistId.equals("")) &&
                    (!npPlaylistId.equals(NowPlaying.NO_PLAYLIST_FLAG))) {
                var npDocket = DocketPlaylist(npPlaylistId)
                var playlist = PodcastPlaylist(npDocket)
                var npEpisode = NowPlaying.getEpisodeId()
                if (!npEpisode.equals(NowPlaying.NO_EPISODE_FLAG)) {
                    playlist.setCurrentEpisode(npEpisode)
                }
                return playlist
            }
            return EmptyPlaylist(docket)
        }

        return EmptyPlaylist(DocketEmpty())
    }
}