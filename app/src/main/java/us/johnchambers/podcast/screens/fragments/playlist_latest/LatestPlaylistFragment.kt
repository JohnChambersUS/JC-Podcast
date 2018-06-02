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
import android.support.v7.widget.helper.ItemTouchHelper
import android.widget.TextView
import us.johnchambers.podcast.Events.fragment.OpenSubscribedDetailEvent
import us.johnchambers.podcast.Events.latest.SubscribedDetailClosedEvent
import us.johnchambers.podcast.Events.player.PlayerClosedEvent


/**
 * A simple [Fragment] subclass.
 * Use the [LatestPlaylistFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class LatestPlaylistFragment : MyFragment() {

    lateinit var _playlist : Playlist
    lateinit var _view : View

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

        _view = inflater.inflate(R.layout.fragment_latest_playlist, container, false)

        if (_playlist.getEpisodes().size == 0) {
            //var noEpisodesMessage = _view.findViewById(R.id.no_episodes_message) as TextView
            //var recyclerView = _view.findViewById(R.id.latest_recycler_view) as RecyclerView
            //noEpisodesMessage.visibility = View.VISIBLE
            //recyclerView.visibility = View.GONE
            flipNoDataMessage()
        }
        else {
            _viewManager = LinearLayoutManager(context)
            _viewAdapter = LatestPlaylistRecyclerAdapter(_playlist.getEpisodes())

            _recyclerView = _view.findViewById(R.id.latest_recycler_view) as RecyclerView

            _recyclerView.apply {
                setHasFixedSize(false)
                layoutManager = _viewManager
                adapter = _viewAdapter
            }

            //_recyclerView.setItemAnimator(null)
            setItemTouchHelper()
        }
        addNavigationListener()
        val navigation = _view.findViewById(R.id.navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(_bottomNavigationListener)
        navigation.itemIconTintList = null

        return _view;
    }

    private fun flipNoDataMessage() {
        var noEpisodesMessage = _view.findViewById(R.id.no_episodes_message) as TextView
        var recyclerView = _view.findViewById(R.id.latest_recycler_view) as RecyclerView
        if (_playlist.isEmpty()) {
            noEpisodesMessage.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
        else {
            noEpisodesMessage.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }


    override fun getBackstackType() : FragmentBackstackType {
        return FragmentBackstackType.ROOT
    }

    //*********************************
    //* local methods
    //*********************************
    private fun refreshEpisodeView() {
        _playlist.removeItem(-2) //will refresh list without wiping db
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

        if (item.itemId == R.id.bm_refresh) {
            _playlist.getEpisodes() //will cause an episode refresh in playlist
            flipNoDataMessage()
            _viewAdapter.notifyDataSetChanged()
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
    @Suppress("UNUSED_PARAMETER")
    fun onEvent(event: SubscribedDetailClosedEvent) {
        refreshEpisodeView()
    }

    @Subscribe
    @Suppress("UNUSED_PARAMETER")
    fun onEvent(event: PlayerClosedEvent) {
        refreshEpisodeView()
    }

    @Subscribe
    fun onEvent(event : LatestRowActionButtonPressedEvent) {
        var row = event.row

        // add popup menu
        val colors = arrayOf<CharSequence>("Play this episode", "Reset to beginning", "Mark as played", "Go to podcast")

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Pick an option:")
        builder.setItems(colors) { _, which ->
            when (which) {
                0 -> {
                    _playlist.setCurrentEpisodeIndex(row)
                    var docket = DocketEmbededPlaylist(_playlist)
                    var theEvent = ResumePlaylistEvent(docket)
                    EventBus.getDefault().post(theEvent)
                }
                1 -> {
                    var eid = _playlist.getEpisodes().get(row).eid
                    PodcastDatabaseHelper.getInstance().updateEpisodeDuration(eid, 1)
                    PodcastDatabaseHelper.getInstance().updateEpisodePlayPoint(eid, 0)
                    //todo delete var updatedEpisode = PodcastDatabaseHelper.getInstance().getEpisodeTableRowByEpisodeId(eid)
                    _playlist.getEpisodes() //will refresh episode list
                    _recyclerView.adapter.notifyItemChanged(row)
                }
                2 -> {
                    var updateRow = _playlist.getEpisodes().get(row)
                    PodcastDatabaseHelper.getInstance().updateEpisodePlayPoint(updateRow.eid,
                            updateRow.lengthAsLong)
                    _playlist.getEpisodes() //will refresh episode list
                    _recyclerView.adapter.notifyItemChanged(row)
                }
                3 -> {
                    var pid = _playlist.getEpisodes().get(row).pid
                    var pt = PodcastDatabaseHelper.getInstance().getPodcastRow(pid)
                    EventBus.getDefault().post(OpenSubscribedDetailEvent(pt))
                }
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    //**********************************************
    //* setup item touch helper for recyclerview
    //**********************************************

    fun setItemTouchHelper() {

        val simpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                var position = viewHolder.getAdapterPosition();
                _playlist.removeItem(position)
                _viewAdapter.notifyDataSetChanged()
                flipNoDataMessage()
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(_recyclerView)
    }


}
