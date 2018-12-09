package us.johnchambers.podcast.screens.fragments.tag.tag_add_to_podcast_fragment

import android.support.percent.PercentRelativeLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.row_tag_add_to_podcast.view.*
import us.johnchambers.podcast.R
import us.johnchambers.podcast.database.PodcastDatabaseHelper
import us.johnchambers.podcast.database.PodcastTagJoinedObject
import us.johnchambers.podcast.database.PodcastTagTable
import us.johnchambers.podcast.misc.Constants
import us.johnchambers.podcast.misc.TapGuard


class TagAddToPodcastRecyclerAdapter(private val podcastList: List<PodcastTagJoinedObject>) :
        RecyclerView.Adapter<TagAddToPodcastRecyclerAdapter.ViewHolder>() {

    lateinit var _workingPid : String
    private val _tapGuard = TapGuard(Constants.MINIMUM_MILLISECONDS_BETWEEN_TAPS_SHORT)

    class ViewHolder(val layout : PercentRelativeLayout) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) :
            TagAddToPodcastRecyclerAdapter.ViewHolder {
        val layout = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_tag_add_to_podcast, parent, false) as PercentRelativeLayout
        return TagAddToPodcastRecyclerAdapter.ViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return podcastList.size
    }

    override fun onBindViewHolder(holder: TagAddToPodcastRecyclerAdapter.ViewHolder, position: Int) {

        holder.layout.context
        holder.layout.row_tag_name2.text = podcastList[position].tag

        holder.layout.row_tag_star_filled2.visibility = View.VISIBLE
        holder.layout.row_tag_star_outline2.visibility = View.GONE
        if ((podcastList[position].pid == null) || (podcastList[position].pid == "")) {
            holder.layout.row_tag_star_filled2.visibility = View.GONE
            holder.layout.row_tag_star_outline2.visibility = View.VISIBLE
        }

        //create hollow listener
        var outlinedStarListener = object : View.OnClickListener {
            override public fun onClick(v : View?)  {
                if (_tapGuard.tooSoon()) return
                var pos = holder.getLayoutPosition(); //getting position
                //flip view
                holder.layout.row_tag_star_filled2.visibility = View.VISIBLE
                holder.layout.row_tag_star_outline2.visibility = View.GONE

                //set array to tagged
                podcastList[position].pid = _workingPid
                //set database to tagged
                var ptTableRow = PodcastTagTable()
                ptTableRow.tag = podcastList[position].tag
                ptTableRow.pid = _workingPid
                PodcastDatabaseHelper.getInstance().upsertPodcastTag(ptTableRow)
            }
        }
        holder.layout.row_tag_star_outline2.setOnClickListener(outlinedStarListener)

        //create filled listener
        var filledStarListener = object : View.OnClickListener {
            override public fun onClick(v : View?)  {
                if (_tapGuard.tooSoon()) return
                var pos = holder.getLayoutPosition(); //getting position
                holder.layout.row_tag_star_filled2.visibility = View.GONE
                holder.layout.row_tag_star_outline2.visibility = View.VISIBLE

                //set to untagged
                podcastList[position].pid = null
                //set database to tagged
                var ptTableRow = PodcastTagTable()
                ptTableRow.tag = podcastList[position].tag.toString().trim()
                ptTableRow.pid = _workingPid
                PodcastDatabaseHelper.getInstance().deletePodcastTagRow(ptTableRow)
            }
        }
        holder.layout.row_tag_star_filled2.setOnClickListener(filledStarListener)

    }

    public fun tagListItem(pos: Int): PodcastTagJoinedObject? {
        return podcastList.get(pos)
    }

    public fun setWorkingPid(pid: String) {
        _workingPid = pid
    }


}