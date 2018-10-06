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
        return C.options.GLOBAL_SPEEDS
    }

    fun getMaxSpeed() : Int {
        return C.options.GLOBAL_SPEEDS.size - 1
    }

    fun getCurrentSpeedAsString() : String {
        try {
            return PodcastDatabaseHelper.getInstance().getOptionValue(C.options.GLOBAL, C.options.KEY_SPEED)
        }
        catch (e: Exception) { //return normal if speed if can't find
            return "1.0"
        }
    }

    fun getCurrentSpeedAsFloat() : Float {
        var speedString = PodcastDatabaseHelper.getInstance().getOptionValue(C.options.GLOBAL, C.options.KEY_SPEED)
        if (speedString == null) {
            speedString = C.options.NORMAL_SPEED
        }
        var returnSpeed = 1.0f
        if (!speedString.equals(C.options.NORMAL_SPEED)) {
            returnSpeed = speedString.toFloat()
        }
        return returnSpeed
    }

    fun getCurrentSpeedIndex() : Int {
        var speedIndex = getSpeedOptionPosition()
        if (speedIndex < 0) {
            for (i in 0..(C.options.GLOBAL_SPEEDS.size - 1)) {
                if (C.options.GLOBAL_SPEEDS.get(i).equals("normal"))
                    speedIndex = i
            }
        }
        return speedIndex
    }

    fun setCurrentSpeed(pos: Int) {
        var row = OptionsTable()
        row.pid = C.options.GLOBAL
        row.option = C.options.KEY_SPEED
        row.setting = C.options.GLOBAL_SPEEDS.get(pos)
        PodcastDatabaseHelper.getInstance().upsertOption(row)
    }

    private fun getSpeedOptionPosition() : Int {
        var returnPos = -1
        var value = PodcastDatabaseHelper.getInstance().getOptionValue(C.options.GLOBAL, C.options.KEY_SPEED)
        for (i in 0..(C.options.GLOBAL_SPEEDS.size - 1)) {
            var s = C.options.GLOBAL_SPEEDS.get(i)
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

    //**********************************
    //* rewind section
    //**********************************

    fun getRewindMinutesAsString() : String {
        try {
            return PodcastDatabaseHelper.getInstance().getOptionValue(C.options.GLOBAL, C.options.REWIND_MINUTES)
        }
        catch (e: Exception) { //return default value if can't find
            return "1"
        }
    }

    fun getRewindMinutesAsMilliseconds() : Int {
        var returnValue = 60000;
        try {
            var storedValue = PodcastDatabaseHelper.getInstance().getOptionValue(C.options.GLOBAL, C.options.REWIND_MINUTES)
            var value = storedValue.toInt()
            returnValue = value * 60000
        }
        catch (e: Exception) { //return default value if can't find

        }
        return returnValue
    }


    fun setRewindMinutes(mins:String) {
        if (mins.equals("")) return
        var row = OptionsTable()
        row.pid = C.options.GLOBAL
        row.option = C.options.REWIND_MINUTES
        row.setting = mins
        PodcastDatabaseHelper.getInstance().upsertOption(row)
    }

    //**********************************
    //* end of rewind section
    //**********************************

    //**********************************
    //* forward section
    //**********************************

    fun getForwardMinutesAsString() : String {
        try {
            return PodcastDatabaseHelper.getInstance().getOptionValue(C.options.GLOBAL, C.options.FORWARD_MINUTES)
        }
        catch (e: Exception) { //return default value if can't find
            return "10"
        }
    }

    fun getForwardMinutesAsMilliseconds() : Int {
        var returnValue = 60000;
        try {
            var storedValue = PodcastDatabaseHelper.getInstance().getOptionValue(C.options.GLOBAL, C.options.FORWARD_MINUTES)
            var value = storedValue.toInt()
            returnValue = value * 60000
        }
        catch (e: Exception) { //return default value if can't find

        }
        return returnValue
    }


    fun setForwardMinutes(mins:String) {
        if (mins.equals("")) return
        var row = OptionsTable()
        row.pid = C.options.GLOBAL
        row.option = C.options.FORWARD_MINUTES
        row.setting = mins
        PodcastDatabaseHelper.getInstance().upsertOption(row)
    }

    //**********************************
    //* end of forward section
    //**********************************


}