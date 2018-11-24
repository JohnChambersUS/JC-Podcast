package us.johnchambers.podcast.objects

import us.johnchambers.podcast.misc.C

class DocketTagAllPlaylist(tag : String) : Docket(tag)  {

    init {
        _docketType = C.dockett.TYPE_IS_TAG_ALL_PLAYLIST
    }
}