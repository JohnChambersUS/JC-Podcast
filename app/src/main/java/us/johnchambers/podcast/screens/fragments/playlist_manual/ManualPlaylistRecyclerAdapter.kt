package us.johnchambers.podcast.screens.fragments.playlist_manual

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
import kotlinx.android.synthetic.main.row_manual_playlist.view.*
import org.greenrobot.eventbus.EventBus
import us.johnchambers.podcast.Events.manual.ManualRowActionButtonPressedEvent
import us.johnchambers.podcast.R
import us.johnchambers.podcast.database.EpisodeTable
import us.johnchambers.podcast.misc.Constants
import us.johnchambers.podcast.misc.MyFileManager
import us.johnchambers.podcast.misc.TapGuard
import kotlin.math.roundToInt

class ManualPlaylistRecyclerAdapter (private val episodeList: List<EpisodeTable>) :
        RecyclerView.Adapter<ManualPlaylistRecyclerAdapter.ViewHolder>() {

    private val _tapGuard = TapGuard(Constants.MINIMUM_MILLISECONDS_BETWEEN_TAPS)

    class ViewHolder(val layout : PercentRelativeLayout) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) :
            ManualPlaylistRecyclerAdapter.ViewHolder {
        val layout = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_manual_playlist, parent, false) as PercentRelativeLayout
        return ViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return episodeList.size
    }

    override fun onBindViewHolder(holder: ManualPlaylistRecyclerAdapter.ViewHolder, position: Int) {

        holder.layout.context
        //todo delete var text = episodeList[position].title
        holder.layout.row_manual_episode_name.text =  (episodeList[position].title).trim()
        var bitmap = MyFileManager.getInstance().getPodcastImage(episodeList[position].pid)
        holder.layout.row_manual_image.setImageBitmap(bitmap)

        setProgress(episodeList[position], holder)

        var buttonListener = object : View.OnClickListener {
            override public fun onClick(v : View?)  {
                if (_tapGuard.tooSoon()) return
                var pos = holder.getLayoutPosition(); //getting position
                EventBus.getDefault().post(ManualRowActionButtonPressedEvent(pos))
            }
        }

        holder.layout.row_manual_button.setOnClickListener(buttonListener)

    }

    private fun setProgress(episodeInfo: EpisodeTable, holder: ManualPlaylistRecyclerAdapter.ViewHolder) {
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
        var imageWidth = (fullWidth * 0.20).roundToInt() //holder.layout.row_manual_image.width
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