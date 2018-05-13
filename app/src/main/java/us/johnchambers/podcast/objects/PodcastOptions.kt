package us.johnchambers.podcast.objects

import us.johnchambers.podcast.database.OptionsTable
import us.johnchambers.podcast.database.PodcastDatabaseHelper
import us.johnchambers.podcast.misc.C

class PodcastOptions(podcastId : String) {

    var _podcastId = podcastId

    //********************************
    //* speed section
    //********************************
    fun getSpeeds() : Array<String> {
        return C.options.PODCAST_SPEEDS
    }

    fun getMaxSpeed() : Int {
        return C.options.PODCAST_SPEEDS.size - 1
    }

    fun getCurrentSpeedAsString() : String {
        var speed = PodcastDatabaseHelper.getInstance().getOptionValue(_podcastId, C.options.KEY_SPEED) ?: C.options.GLOBAL_SPEED
        return speed
    }

    fun getCurrentSpeedAsFloat() : Float {
        var speedString = PodcastDatabaseHelper.getInstance().getOptionValue(_podcastId, C.options.KEY_SPEED) ?: C.options.GLOBAL_SPEED
        var returnSpeed = 1.0f
        if (speedString.equals(C.options.GLOBAL_SPEED)) {//if not set use global setting
            var globalOptions = GlobalOptions()
            speedString = globalOptions.getCurrentSpeedAsString()
        }
        if (!speedString.equals(C.options.NORMAL_SPEED)) {
            returnSpeed = speedString.toFloat()
        }
        return returnSpeed
    }

    fun getCurrentSpeedIndex() : Int {
        var speedIndex = getSpeedOptionPosition()
        //if speed index is not set in database then find use global index
        if (speedIndex < 0) {
            for (i in 0..(C.options.PODCAST_SPEEDS.size - 1)) {
                if (C.options.PODCAST_SPEEDS.get(i).equals(C.options.GLOBAL_SPEED))
                    speedIndex = i
            }
        }
        return speedIndex
    }

    fun setCurrentSpeed(pos: Int) {
        var row = OptionsTable()
        row.pid = _podcastId
        row.option = C.options.KEY_SPEED
        row.setting = C.options.PODCAST_SPEEDS.get(pos)
        PodcastDatabaseHelper.getInstance().upsertOption(row)
    }

    private fun getSpeedOptionPosition() : Int {
        var returnPos = -1
        var value = PodcastDatabaseHelper.getInstance().getOptionValue(_podcastId, C.options.KEY_SPEED)
        for (i in 0..(C.options.PODCAST_SPEEDS.size - 1)) {
            var s = C.options.PODCAST_SPEEDS.get(i)
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