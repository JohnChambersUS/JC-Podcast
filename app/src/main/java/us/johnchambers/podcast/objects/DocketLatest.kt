package us.johnchambers.podcast.objects

import us.johnchambers.podcast.misc.C

class DocketLatest : Docket("dummy") {

    init {
        _docketType = C.dockett.TYPE_IS_LATEST
    }
}