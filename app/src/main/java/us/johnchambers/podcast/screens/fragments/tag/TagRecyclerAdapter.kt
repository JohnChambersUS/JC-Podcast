package us.johnchambers.podcast.screens.fragments.tag

import android.support.percent.PercentRelativeLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.row_manual_playlist.view.*
import kotlinx.android.synthetic.main.row_tag.view.*
import org.greenrobot.eventbus.EventBus
import us.johnchambers.podcast.Events.manual.ManualRowActionButtonPressedEvent
import us.johnchambers.podcast.R
import us.johnchambers.podcast.database.EpisodeTable
import us.johnchambers.podcast.database.TagTable
import us.johnchambers.podcast.misc.MyFileManager
import us.johnchambers.podcast.screens.fragments.playlist_manual.ManualPlaylistRecyclerAdapter

class TagRecyclerAdapter (private val tagList: List<TagTable>) :
        RecyclerView.Adapter<TagRecyclerAdapter.ViewHolder>() {


    class ViewHolder(val layout : PercentRelativeLayout) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) :
            TagRecyclerAdapter.ViewHolder {
        val layout = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_tag, parent, false) as PercentRelativeLayout
        return ViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return tagList.size
    }

    override fun onBindViewHolder(holder: TagRecyclerAdapter.ViewHolder, position: Int) {

        holder.layout.context
        holder.layout.row_tag_name.text =  (tagList[position].tag.trim())

        //setProgress(episodeList[position], holder)

        //var buttonListener = object : View.OnClickListener {
        //    override public fun onClick(v : View?)  {
        //        var pos = holder.getLayoutPosition(); //getting position
        //        EventBus.getDefault().post(ManualRowActionButtonPressedEvent(pos))
        //    }
       // }

       // holder.layout.row_manual_button.setOnClickListener(buttonListener)

    }

}