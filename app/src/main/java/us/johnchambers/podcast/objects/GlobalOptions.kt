package us.johnchambers.podcast.objects

import us.johnchambers.podcast.database.OptionsTable
import us.johnchambers.podcast.database.PodcastDatabaseHelper
import us.johnchambers.podcast.misc.C

class GlobalOptions {

    var optionsList : List<OptionsTable>

    init {
        optionsList = PodcastDatabaseHelper.getInstance().getOptionsByPodcastId(C.options.GLOBAL)
    }

    //********************************
    //* speed section
    //********************************
    fun getSpeeds() : Array<String> {
        return C.options.SPEEDS
    }

    fun getMaxSpeed() : Int {
        return C.options.SPEEDS.size - 1
    }

    fun getCurrentSpeedIndex() : Int {
        var speedIndex = getSpeedOptionPosition()
        if (speedIndex < 0) {
            for (i in 0..(C.options.SPEEDS.size - 1)) {
                if (C.options.SPEEDS.get(i).equals("normal"))
                    speedIndex = i
            }
        }
        return speedIndex
    }

    fun setCurrentSpeed(pos: Int) {
        var row = OptionsTable()
        row.pid = C.options.GLOBAL
        row.option = C.options.KEY_SPEED
        row.setting = C.options.SPEEDS.get(pos)
        PodcastDatabaseHelper.getInstance().upsertOption(row)
    }

    private fun getSpeedOptionPosition() : Int {
        var returnPos = -1
        var value = PodcastDatabaseHelper.getInstance().getOptionValue(C.options.GLOBAL, C.options.KEY_SPEED)
        for (i in 0..(C.options.SPEEDS.size - 1)) {
            var s = C.options.SPEEDS.get(i)
            if (s.equals(value)) {
                returnPos = i
                break
            }
        }
        return returnPos
    }
    //**********************************
    //* end of speed section
    //**********************************

}