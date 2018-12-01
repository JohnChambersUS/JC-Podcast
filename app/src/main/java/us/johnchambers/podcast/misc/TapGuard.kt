package us.johnchambers.podcast.misc

import android.util.Log
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date

class TapGuard(val timeLimit: Int) {

    var lastTapTime = Date()

    public fun tooSoon() : Boolean {
        var diff = Date().time - lastTapTime.time
        L.i("dateDiff", diff.toString())
        if (diff < timeLimit) {
            return true
        }
        lastTapTime = Date()
        return false;
    }
}