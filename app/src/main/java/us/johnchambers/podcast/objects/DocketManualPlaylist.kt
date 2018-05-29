package us.johnchambers.podcast.objects

import us.johnchambers.podcast.misc.C

class DocketManualPlaylist  : Docket("dummy") {

    init {
        _docketType = C.dockett.TYPE_IS_MANUAL
    }
}