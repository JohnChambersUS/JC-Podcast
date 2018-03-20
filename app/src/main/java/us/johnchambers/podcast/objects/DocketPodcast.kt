package us.johnchambers.podcast.objects

import us.johnchambers.podcast.misc.C

/**
 * Created by johnchambers on 3/13/18.
 */
open class DocketPodcast(id : String) : Docket(id) {

    init {
        _docketType = C.dockett.TYPE_IS_PODCAST
    }



}