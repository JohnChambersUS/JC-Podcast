package us.johnchambers.podcast.objects

import us.johnchambers.podcast.database.PodcastDatabaseHelper
import us.johnchambers.podcast.misc.C

/**
 * Created by johnchambers on 3/16/18.
 */
class DocketEpisode(episode : String) : Docket(episode) {

    init {
        _docketType = C.dockett.TYPE_IS_EPISODE
    }

}