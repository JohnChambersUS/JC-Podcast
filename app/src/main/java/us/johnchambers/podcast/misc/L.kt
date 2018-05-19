package us.johnchambers.podcast.misc

import android.util.Log

/**
 * Created by johnchambers on 2/18/18.
 */
object L {

    val flag = "~~~~"

    fun i(prefix: String, message: String) {
        if (Constants.DEBUG) Log.i(prefix, "$flag $message")
    }

    fun i(thisThing: Object, message: String) {
        if (Constants.DEBUG) Log.i(thisThing.`class`.simpleName, "$flag $message")
    }


}