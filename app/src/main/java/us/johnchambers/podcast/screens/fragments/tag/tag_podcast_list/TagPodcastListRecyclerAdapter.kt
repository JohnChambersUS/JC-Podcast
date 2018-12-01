package us.johnchambers.podcast.screens.fragments.tag.tag_podcast_list

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.percent.PercentRelativeLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.row_tag_podcast_list.view.*
import us.johnchambers.podcast.R
import us.johnchambers.podcast.database.PodcastDatabaseHelper
import us.johnchambers.podcast.database.PodcastTagJoinedObject
import us.johnchambers.podcast.database.PodcastTagTable
import us.johnchambers.podcast.misc.Constants
import us.johnchambers.podcast.misc.MyFileManager
import us.johnchambers.podcast.misc.TapGuard

class TagPodcastListRecyclerAdapter(private val podcastList: List<PodcastTagJoinedObject>) :
        RecyclerView.Adapter<TagPodcastListRecyclerAdapter.ViewHolder>() {

    lateinit var _workingTag : String
    private val _tapGuard = TapGuard(Constants.MINIMUM_MILLISECONDS_BETWEEN_TAPS_SHORT)

    class ViewHolder(val layout : PercentRelativeLayout) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) :
            TagPodcastListRecyclerAdapter.ViewHolder {
        val layout = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_tag_podcast_list, parent, false) as PercentRelativeLayout
        return TagPodcastListRecyclerAdapter.ViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return podcastList.size
    }

    override fun onBindViewHolder(holder: TagPodcastListRecyclerAdapter.ViewHolder, position: Int) {

        holder.layout.context
        var dbRow = PodcastDatabaseHelper.getInstance().getPodcastRow(podcastList[position].pid)
        var pn = dbRow.name
        holder.layout.row_podcast_name.text = pn.toString().trim()

        var pcImage: Bitmap? = MyFileManager.getInstance().getPodcastImage(podcastList[position].pid)
        if (pcImage == null) {
            pcImage = BitmapFactory.decodeResource(Resources.getSystem(),
                    R.raw.missing_podcast_image)
        }
        holder.layout.row_podcast_image.setImageBitmap(pcImage)

        holder.layout.row_tag_star_filled.visibility = View.VISIBLE
        holder.layout.row_tag_star_outline.visibility = View.GONE
        if ((podcastList[position].tag == null) || (podcastList[position].tag == "")) {
            holder.layout.row_tag_star_filled.visibility = View.GONE
            holder.layout.row_tag_star_outline.visibility = View.VISIBLE
        }

        //create hollow listener
        var outlinedStarListener = object : View.OnClickListener {
            override public fun onClick(v : View?)  {
                if (_tapGuard.tooSoon()) return
                var pos = holder.getLayoutPosition(); //getting position
                //flip view
                holder.layout.row_tag_star_filled.visibility = View.VISIBLE
                holder.layout.row_tag_star_outline.visibility = View.GONE

                //set array to tagged
                podcastList[position].tag = _workingTag
                //set database to tagged
                var ptTableRow = PodcastTagTable()
                ptTableRow.pid = podcastList[position].pid
                ptTableRow.tag = _workingTag
                PodcastDatabaseHelper.getInstance().upsertPodcastTag(ptTableRow)
            }
        }
        holder.layout.row_tag_star_outline.setOnClickListener(outlinedStarListener)

        //create filled listener
        var filledStarListener = object : View.OnClickListener {
            override public fun onClick(v : View?)  {
                if (_tapGuard.tooSoon()) return
                var pos = holder.getLayoutPosition(); //getting position
                holder.layout.row_tag_star_filled.visibility = View.GONE
                holder.layout.row_tag_star_outline.visibility = View.VISIBLE

                //set to untagged
                podcastList[position].tag = null
                //set database to tagged
                var ptTableRow = PodcastTagTable()
                ptTableRow.pid = podcastList[position].pid.toString().trim()
                ptTableRow.tag = _workingTag
                PodcastDatabaseHelper.getInstance().deletePodcastTagRow(ptTableRow)


            }
        }
        holder.layout.row_tag_star_filled.setOnClickListener(filledStarListener)

    }

    public fun tagListItem(pos: Int): PodcastTagJoinedObject? {
        return podcastList.get(pos)
    }

    public fun setWorkingTag(tag: String) {
        _workingTag = tag
    }


}