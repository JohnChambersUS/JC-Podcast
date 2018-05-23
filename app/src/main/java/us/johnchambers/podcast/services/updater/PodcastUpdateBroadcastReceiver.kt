package us.johnchambers.podcast.services.updater

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import us.johnchambers.podcast.misc.L

class PodcastUpdateBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

            val theIntent = Intent(context.applicationContext,
                    us.johnchambers.podcast.services.updater.PodcastUpdateService::class.java)
            context.startService(theIntent)

            L.i(this as Object, "Broadcast Reciever ran, started updater")
    }
}
