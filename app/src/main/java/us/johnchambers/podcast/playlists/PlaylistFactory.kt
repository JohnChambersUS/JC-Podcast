package us.johnchambers.podcast.playlists

import us.johnchambers.podcast.misc.C
import us.johnchambers.podcast.objects.Docket
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




        return EmptyPlaylist(DocketPlaylist("dummy"))


    }
}