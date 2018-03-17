package us.johnchambers.podcast.objects

import us.johnchambers.podcast.misc.C

/**
 * Created by johnchambers on 3/17/18.
 */
class EmptyDocket : Docket("dummy") {

    init {
        _docketType = C.dockett.TYPE_IS_EMPTY
    }

}