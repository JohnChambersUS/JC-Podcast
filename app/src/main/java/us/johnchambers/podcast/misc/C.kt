package us.johnchambers.podcast.misc

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
        var TYPE_IS_EMBEDED_PLAYLIST = "embeded_playlist"
    }

    object playlist {
        var LATEST_PLAYLIST = "latest_playlist"
        var DUMMY = "dummy"
    }

    object options {
        var GLOBAL = "GLOBAL"
        var SPEEDS = arrayOf("0.50", "0.75", "0.90", "normal", "1.10",  "1.25", "1.40", "1.50", "1.75", "2.0")
        var KEY_SPEED = "speed"
        var NORMAL_SPEED = "normal"

    }
}