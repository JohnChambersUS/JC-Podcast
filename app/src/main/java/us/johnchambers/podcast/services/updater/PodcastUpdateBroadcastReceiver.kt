package us.johnchambers.podcast.services.updater

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import us.johnchambers.podcast.misc.Constants
import us.johnchambers.podcast.misc.DebugInfo

class PodcastUpdateBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent != null) {
            val intent = Intent(context.applicationContext,
                    us.johnchambers.podcast.services.updater.PodcastUpdateService::class.java)
            context.startService(intent)

            if (Constants.DEBUG) {
                val bug = DebugInfo(context)
                bug.writeTimeFile("ReceiverCalled")
            }
        }
    }
}
