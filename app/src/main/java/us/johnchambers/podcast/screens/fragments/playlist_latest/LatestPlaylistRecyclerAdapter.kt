package us.johnchambers.podcast.screens.fragments.playlist_latest


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater
import android.view.ViewGroup
import us.johnchambers.podcast.R
import us.johnchambers.podcast.database.EpisodeTable
import 	android.support.percent.PercentRelativeLayout
import android.view.View
import android.widget.Button
import android.view.View.OnClickListener;
import kotlinx.android.synthetic.main.row_latest_playlist.view.*
import org.greenrobot.eventbus.EventBus
import us.johnchambers.podcast.Events.latest.LatestRowActionButtonPressedEvent
import us.johnchambers.podcast.misc.MyFileManager

class LatestPlaylistRecyclerAdapter(private val episodeList: List<EpisodeTable>) :
        RecyclerView.Adapter<LatestPlaylistRecyclerAdapter.ViewHolder>() {

    class ViewHolder(val layout : PercentRelativeLayout) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) :
            LatestPlaylistRecyclerAdapter.ViewHolder {
        val layout = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_latest_playlist, parent, false) as PercentRelativeLayout
        return ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var text = episodeList[position].title
        holder.layout.row_latest_episode_name.text =  (episodeList[position].title).trim()
        var bitmap = MyFileManager.getInstance().getPodcastImage(episodeList[position].pid)
        holder.layout.row_latest_image.setImageBitmap(bitmap)

        var buttonListener = object : View.OnClickListener {
            override public fun onClick(v : View?)  {
                var pos = holder.getLayoutPosition(); //getting position
                EventBus.getDefault().post(LatestRowActionButtonPressedEvent(pos))
            }
        }

        holder.layout.row_latest_button.setOnClickListener(buttonListener)
    }

    override fun getItemCount(): Int {
        return episodeList.size
    }

}