package us.johnchambers.podcast.screens.fragments.playlist_generic

import android.app.Activity
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.support.percent.PercentRelativeLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.row_latest_playlist.view.*
import org.greenrobot.eventbus.EventBus
import us.johnchambers.podcast.Events.latest.LatestRowActionButtonPressedEvent
import us.johnchambers.podcast.R
import us.johnchambers.podcast.database.EpisodeTable
import us.johnchambers.podcast.misc.MyFileManager
import us.johnchambers.podcast.screens.fragments.playlist_latest.LatestPlaylistRecyclerAdapter
import kotlin.math.roundToInt

class GenericPlaylistRecyclerAdapter(private val episodeList: List<EpisodeTable>) :
        RecyclerView.Adapter<GenericPlaylistRecyclerAdapter.ViewHolder>() {


    class ViewHolder(val layout : PercentRelativeLayout) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) :
            GenericPlaylistRecyclerAdapter.ViewHolder {
        val layout = LayoutInflater.from(parent.context)
                //todo can I use this row layout?
                .inflate(R.layout.row_latest_playlist, parent, false) as PercentRelativeLayout
        return ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.layout.context
        holder.layout.row_latest_episode_name.text =  (episodeList[position].title).trim() + " " + (episodeList[position].pubDate)
        var bitmap = MyFileManager.getInstance().getPodcastImage(episodeList[position].pid)
        holder.layout.row_latest_image.setImageBitmap(bitmap)

        var buttonListener = object : View.OnClickListener {
            override public fun onClick(v : View?)  {
                var pos = holder.getLayoutPosition(); //getting position
                EventBus.getDefault().post(LatestRowActionButtonPressedEvent(pos)) //todo should I put menu here instead of event call?
            }
        }
        holder.layout.row_latest_button.setOnClickListener(buttonListener)

        setProgress(episodeList[position], holder)
    }

    override fun getItemCount(): Int {
        return episodeList.size
    }

    private fun setProgress(episodeInfo: EpisodeTable, holder: ViewHolder) {
        var context = holder.layout.context
        val left = GradientDrawable()
        left.shape = GradientDrawable.RECTANGLE
        left.setColor(ContextCompat.getColor(context, R.color.semiLightBackground))

        val right = GradientDrawable()
        right.shape = GradientDrawable.RECTANGLE
        right.setColor(ContextCompat.getColor(context, R.color.lightBackground))


        val ar = arrayOf(left, right)
        val layer = LayerDrawable(ar)

        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        val fullWidth = displayMetrics.widthPixels
        var imageWidth = (fullWidth * 0.20).roundToInt()
        var workingWidth = fullWidth - imageWidth

        val playPoint = episodeInfo.playPointAsLong
        val length = episodeInfo.lengthAsLong


        var ratio = 0f
        if (playPoint != 0L) {
            if (playPoint >= length) {
                ratio = 1f
            } else {
                ratio = playPoint.toFloat() / length
            }
        }

        var size = Math.round(workingWidth * ratio)
        if (size > 0) {
            size+= imageWidth
        }

        layer.setLayerInset(1, size, 0, 0, 0)

        holder.layout.background = layer
    }



}