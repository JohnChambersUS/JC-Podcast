package us.johnchambers.podcast.Events.player

import us.johnchambers.podcast.objects.Docket

/**
 * Created by johnchambers on 3/13/18.
 */


class ResumePlaylistEvent(docket : Docket) {

    var _docket = docket

    fun getDocketPackage() : Docket {
        return _docket
    }


}