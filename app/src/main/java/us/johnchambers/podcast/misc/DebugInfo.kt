package us.johnchambers.podcast.misc

import android.content.Context
import java.io.File
import java.util.*

/**
 * Created by johnchambers on 2/18/18.
 */
class DebugInfo constructor(_context: Context) {

    var _context : Context  = _context

    fun writeTimeFile(suffix : String) {

        var julian = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        var hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        var min = Calendar.getInstance().get(Calendar.MINUTE)

        var fn = Integer.toString(julian) + "." + Integer.toString(hour) + "." + Integer.toString(min)

        var dir2 : String = _context.applicationContext.getExternalFilesDir(null).toString()

        val file = File(dir2 + "/" + fn + "." + suffix)

        if (!file.exists()) {
            try {

                file.createNewFile()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}