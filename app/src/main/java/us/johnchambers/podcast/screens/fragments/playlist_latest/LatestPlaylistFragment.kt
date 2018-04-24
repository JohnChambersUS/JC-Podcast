package us.johnchambers.podcast.screens.fragments.playlist_latest


import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.recyclerview.R.attr.layoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import us.johnchambers.podcast.fragments.MyFragment

import us.johnchambers.podcast.R
import us.johnchambers.podcast.playlists.EmptyPlaylist
import us.johnchambers.podcast.playlists.Playlist
import us.johnchambers.podcast.playlists.PlaylistFactory

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import us.johnchambers.podcast.Events.latest.LatestRowActionButtonPressedEvent
import us.johnchambers.podcast.Events.player.ResumePlaylistEvent
import us.johnchambers.podcast.database.EpisodeTable
import us.johnchambers.podcast.database.PodcastDatabaseHelper
import us.johnchambers.podcast.objects.*
import android.support.v7.widget.DefaultItemAnimator
import us.johnchambers.podcast.Events.player.PlayerClosedEvent


/**
 * A simple [Fragment] subclass.
 * Use the [LatestPlaylistFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class LatestPlaylistFragment : MyFragment() {

    lateinit var _playlist : Playlist

    private lateinit var _recyclerView : RecyclerView
    private lateinit var _viewAdapter: RecyclerView.Adapter<*>
    private lateinit var _viewManager: RecyclerView.LayoutManager

    private var _bottomNavigationListener: BottomNavigationView.OnNavigationItemSelectedListener? = null

    companion object {
        @JvmStatic
        fun newInstance(): LatestPlaylistFragment {
            return LatestPlaylistFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        _playlist = PlaylistFactory.getPlaylist(DocketLatest())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_latest_playlist, container, false)

        if (_playlist.getEpisodes().size == 0) {
            var noEpisodesMessage = view.findViewById(R.id.no_episodes_message)
            var recyclerView = view.findViewById(R.id.latest_recycler_view)
            noEpisodesMessage.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
        else {
            _viewManager = LinearLayoutManager(context)
            _viewAdapter = LatestPlaylistRecyclerAdapter(_playlist.getEpisodes())

            _recyclerView = view.findViewById(R.id.latest_recycler_view) as RecyclerView

            _recyclerView.apply {
                setHasFixedSize(false)
                layoutManager = _viewManager
                adapter = _viewAdapter
            }

            _recyclerView.setItemAnimator(null)

            addNavigationListener()
            val navigation = view.findViewById(R.id.navigation) as BottomNavigationView
            navigation.setOnNavigationItemSelectedListener(_bottomNavigationListener)
            navigation.itemIconTintList = null
        }

        return view;
    }

    override fun getBackstackType() : FragmentBackstackType {
        return FragmentBackstackType.ROOT
    }

    //*********************************
    //* local methods
    //*********************************
    private fun refreshEpisodeView() {
        _playlist.getEpisodes() //will refresh list
        _recyclerView.adapter.notifyDataSetChanged()
    }


    //*********************************
    //* bottom menu listener
    //*********************************

    private fun processNavigation(item: MenuItem) {
        if (item.itemId == R.id.bm_play) {
            var docket = DocketEmbededPlaylist(_playlist)
            var event = ResumePlaylistEvent(docket)
            EventBus.getDefault().post(event)
        }
    }

    private fun addNavigationListener() {

        _bottomNavigationListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            processNavigation(item)
            true
        }

    }

    //**************************
    //* event listener
    //**************************
    @Subscribe
    fun onEvent(event: PlayerClosedEvent) {
        refreshEpisodeView()
    }

    @Subscribe
    fun onEvent(event : LatestRowActionButtonPressedEvent) {
        var row = event.row

        // add popup menu
        val colors = arrayOf<CharSequence>("Play this episode", "Reset to beginning", "Mark as played")

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Pick an option:")
        builder.setItems(colors) { dialog, which ->
            when (which) {
                0 -> {
                    _playlist.setCurrentEpisodeIndex(row)
                    var docket = DocketEmbededPlaylist(_playlist)
                    var event = ResumePlaylistEvent(docket)
                    EventBus.getDefault().post(event)
                }
                1 -> {
                    var eid = _playlist.getEpisodes().get(row).eid
                    PodcastDatabaseHelper.getInstance().updateEpisodeDuration(eid, 1)
                    PodcastDatabaseHelper.getInstance().updateEpisodePlayPoint(eid, 0)
                    var updatedEpisode = PodcastDatabaseHelper.getInstance().getEpisodeTableRowByEpisodeId(eid)
                    _playlist.getEpisodes() //will refresh episode list
                    _recyclerView.adapter.notifyItemChanged(row)
                }
                2 -> { var z = 3
                    var updateRow = _playlist.getEpisodes().get(row)
                    PodcastDatabaseHelper.getInstance().updateEpisodePlayPoint(updateRow.eid,
                            updateRow.lengthAsLong)
                    _playlist.getEpisodes() //will refresh episode list
                    _recyclerView.adapter.notifyItemChanged(row)
                }
            }
        }

        builder.setNegativeButton("Cancel") { dialog, id -> dialog.cancel() }

        builder.show()
    }



}
