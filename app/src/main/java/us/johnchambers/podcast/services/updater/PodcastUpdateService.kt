package us.johnchambers.podcast.services.updater

import android.app.*
import android.content.Intent
import android.content.Context
import android.os.Build
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import us.johnchambers.podcast.R
import us.johnchambers.podcast.database.PodcastDatabaseHelper
import us.johnchambers.podcast.database.PodcastTable
import us.johnchambers.podcast.misc.C
import us.johnchambers.podcast.misc.L
import us.johnchambers.podcast.misc.VolleyQueue
import us.johnchambers.podcast.objects.FeedResponseWrapper
import java.util.*
import kotlin.collections.HashMap


class PodcastUpdateService : IntentService("PodcastUpdateService") {

    var podcastStack = Stack<PodcastTable>()
    var _intent : Intent? = null
    var _notificationId = 23457
    var _notificationChannelId = "us.johnchambers.player.updater"

    override fun onHandleIntent(intent: Intent?) {
        _intent = intent
        L.i(this as Object, "Updater started")
        if (intent != null) {
            displayNotification()
            updatePodcasts()
        }
    }

    override fun onDestroy() {
        L.i(this as Object, "Updater ended")
        super.onDestroy()
    }

    fun updatePodcasts() {
        try {
            podcastStack.add(C.podcasts.UPDATE_STACK.pop())
        } catch (e: Exception) {}

        if (podcastStack.isEmpty()) {
            var podcastList = PodcastDatabaseHelper.getInstance(applicationContext).allPodcastRows
            podcastStack.addAll(podcastList)
        }

        updateNextPodcast()
    }

    fun updateNextPodcast() {
        if (podcastStack.isEmpty()) {
            clearNotification()
            stopSelf()
        }
        else {
            updateThisPodcast(podcastStack.pop())
        }
    }

    fun updateThisPodcast(podcast : PodcastTable) {
        L.i(this as Object, "Updating " + podcast.name)
        val sr = StringRequest(Request.Method.GET,
                podcast.getFeedUrl(),
                Response.Listener { response -> updateTheDB(response, podcast) },

                Response.ErrorListener { e ->
                    L.i(this as Object, e.message  + podcast.name);
                })
        val vq = VolleyQueue.getInstance(applicationContext)
        val rq = vq.requestQueue
        rq!!.add(sr)
    }

    fun updateTheDB(response : String, currPodcastTableRow : PodcastTable) {
        var newEpisodes = HashMap<String, Boolean>()
        val feedResponseWrapper = FeedResponseWrapper(response,
                currPodcastTableRow.feedUrl)
        feedResponseWrapper.processEpisodesFromBottom()
        while (feedResponseWrapper.prevEpisode()) {
            //get curr episode id
            var newEpisodeId = feedResponseWrapper.episodeId
            //check to see if episode in DB
            var dbRow = PodcastDatabaseHelper.getInstance()
                    .getEpisodeTableRowByEpisodeId(newEpisodeId)
            //if not in DB, then add
            if (dbRow == null) {
                val currEpisode = feedResponseWrapper.getFilledEpisodeTable()
                PodcastDatabaseHelper.getInstance().insertEpisodeTableRow(currEpisode)
                //PodcastDatabaseHelper.getInstance().addNewEpisodeRow(feedResponseWrapper)
                L.i(this as Object,
                        "Adding episode " + feedResponseWrapper.currEpisodeTitle)
            }
            newEpisodes.set(newEpisodeId, true)
        }
        removeDeadEpisodesFromDB(newEpisodes, currPodcastTableRow.pid)
        updateNextPodcast()
    }

    fun removeDeadEpisodesFromDB(newEpisodes : HashMap<String, Boolean>, pid : String) {
        var dbEpisodeList = PodcastDatabaseHelper.getInstance().getEpisodesSortedNewest(pid)
        for (episode in dbEpisodeList) {
            if (!newEpisodes.contains(episode.eid)) {
                PodcastDatabaseHelper.getInstance().deleteEpisodeRow(episode.eid)
                L.i(this as Object, "removing episode " + episode.title)
            }
        }
    }

    fun displayNotification() {

        // create channel
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(_notificationChannelId,
                    "Diffcast",
                    NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "A Different Podcast App"
            channel.enableLights(false)
            channel.enableVibration(false)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }

        val notif: Notification.Builder
        @Suppress("DEPRECATION")
        notif = Notification.Builder(applicationContext)
        notif.setSmallIcon(R.mipmap.ic_launcher)
        notif.setContentTitle("Updating Podcasts")
        notif.setContentText("")
        notif.setAutoCancel(true)
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notif.setChannelId(_notificationChannelId)
        }

        var pendingIntent : PendingIntent = PendingIntent.getActivity(applicationContext,
                0,
                Intent("new intent"),
                0)

        notif.setContentIntent(pendingIntent)

        notif.build()
    }

    fun clearNotification() {
        var notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(_notificationId)
        notificationManager.cancelAll()

    }


}
