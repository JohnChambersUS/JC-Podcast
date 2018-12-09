package us.johnchambers.podcast.misc

import us.johnchambers.podcast.database.PodcastTable
import java.util.*

/**
 * Created by johnchambers on 3/14/18.
 *
 * New constants file
 */
object C {

    object dockett {
        var TYPE_IS_PLAYLIST = "playlist"
        var TYPE_IS_EPISODE = "episode"
        var TYPE_IS_PODCAST = "podcast"
        var TYPE_IS_NOTHING = "nothing"
        var TYPE_IS_EMPTY = "empty"
        var TYPE_IS_LATEST = "latest"
        var TYPE_IS_MANUAL = "manual_playlist"
        var TYPE_IS_EMBEDED_PLAYLIST = "embeded_playlist"
        var TYPE_IS_GENERIC_PLAYLIST = "generic_playlist"
        var TYPE_IS_TAG_ALL_PLAYLIST = "tag_all_playlist"
    }

    object playlist {
        var TAG_PLAYLIST_SUFFIX = "_tag_playlist"
        var GENERIC_PLAYLIST = "generic_playlist"
        var LATEST_PLAYLIST = "latest_playlist"
        var MANUAL_PLAYLIST = "manual_playlist"
        var DUMMY = "dummy"
    }

    object options {
        var REWIND_MINUTES = "rewind_minutes"
        var FORWARD_MINUTES = "forward_minutes"
        var GLOBAL = "GLOBAL"
        var KEY_SPEED = "speed"
        var NORMAL_SPEED = "normal"
        var GLOBAL_SPEED = "global speed"
        var GLOBAL_SPEEDS = arrayOf("0.50", "0.75", "0.90", NORMAL_SPEED, "1.10",  "1.25", "1.40", "1.50", "1.75", "2.0")
        var PODCAST_SPEEDS = arrayOf("0.50", "0.75", "0.90", GLOBAL_SPEED, NORMAL_SPEED, "1.10",  "1.25", "1.40", "1.50", "1.75", "2.0")

    }

    object podcasts {
        var UPDATE_STACK = Stack<PodcastTable>()
    }
}