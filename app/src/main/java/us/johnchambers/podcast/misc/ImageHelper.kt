package us.johnchambers.podcast.misc

import android.graphics.Bitmap
import com.squareup.picasso.Picasso
import us.johnchambers.podcast.database.PodcastDatabaseHelper

/**
 * Created by johnchambers on 3/17/18.
 */
object ImageHelper {

    fun getEpisodeImage(podcastId : String) : Bitmap {
        var podcastInfo = PodcastDatabaseHelper.getInstance().getPodcastRow(podcastId)
        var logoUrl = podcastInfo.logoUrl
        var bitmap = Picasso.get().load(logoUrl).get()
        return bitmap
    }
}