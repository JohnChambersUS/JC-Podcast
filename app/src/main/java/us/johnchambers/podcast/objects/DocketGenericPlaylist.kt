package us.johnchambers.podcast.objects

import us.johnchambers.podcast.misc.C

class DocketGenericPlaylist(tag : String) : Docket(tag) {

    init {
        _docketType = C.dockett.TYPE_IS_GENERIC_PLAYLIST
    }


}