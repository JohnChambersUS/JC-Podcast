package us.johnchambers.podcast.screens.fragments.playlist_manual


import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import us.johnchambers.podcast.Events.fragment.*
import us.johnchambers.podcast.Events.fragment.SubscribedDetailClosedEvent
import us.johnchambers.podcast.Events.manual.ManualRowActionButtonPressedEvent
import us.johnchambers.podcast.Events.player.PlayerClosedEvent
import us.johnchambers.podcast.Events.player.ResumePlaylistEvent

import us.johnchambers.podcast.R
import us.johnchambers.podcast.database.PodcastDatabaseHelper
import us.johnchambers.podcast.fragments.MyFragment
import us.johnchambers.podcast.misc.C
import us.johnchambers.podcast.objects.DocketEmbededPlaylist
import us.johnchambers.podcast.objects.DocketManualPlaylist
import us.johnchambers.podcast.objects.FragmentBackstackType
import us.johnchambers.podcast.playlists.Playlist
import us.johnchambers.podcast.playlists.PlaylistFactory

class ManualPlaylistFragment : MyFragment() {

    lateinit var _view : View;
    lateinit var _playlist : Playlist

    private lateinit var _recyclerView : RecyclerView
    private lateinit var _viewAdapter: RecyclerView.Adapter<*>
    private lateinit var _viewManager: RecyclerView.LayoutManager

    private var _bottomNavigationListener: BottomNavigationView.OnNavigationItemSelectedListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        EventBus.getDefault().register(this)
        _playlist = PlaylistFactory.getPlaylist(DocketManualPlaylist())

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _view =  inflater.inflate(R.layout.fragment_manual_playlist, container, false)

        if (_playlist.getEpisodes().size == 0) {
            flipNoDataMessage()
        }
        else {
            _viewManager = LinearLayoutManager(context)
            _viewAdapter = ManualPlaylistRecyclerAdapter(_playlist.getEpisodes())

            _recyclerView = _view.findViewById(R.id.manual_recycler_view) as RecyclerView

            _recyclerView.apply {
                setHasFixedSize(false)
                layoutManager = _viewManager
                adapter = _viewAdapter
            }
            setItemTouchHelper()
        }
        addNavigationListener()
        val navigation = _view.findViewById(R.id.manual_navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(_bottomNavigationListener)
        navigation.itemIconTintList = null

        return _view;
    }


    companion object {
        @JvmStatic
        fun newInstance() =
                ManualPlaylistFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }

    override fun getBackstackType() : FragmentBackstackType {
        return FragmentBackstackType.ROOT
    }

    private fun flipNoDataMessage() {
        var noEpisodesMessage = _view.findViewById(R.id.manual_no_episodes_message) as TextView
        var recyclerView = _view.findViewById(R.id.manual_recycler_view) as RecyclerView
        if (_playlist.getEpisodes().size == 0) {
            noEpisodesMessage.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
        else {
            noEpisodesMessage.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }


    }

    //*********************************
    //* bottom menu listener
    //*********************************

    private fun processNavigation(item: MenuItem) {

        if (item.itemId == R.id.mp_clear) {
            PodcastDatabaseHelper.getInstance().removePlaylistFromPlaylistTable(C.playlist.MANUAL_PLAYLIST);
            PodcastDatabaseHelper.getInstance().conditionalyClearNowPlaying(C.playlist.MANUAL_PLAYLIST);
            EventBus.getDefault().post(RefreshManualPlaylistFragment())
        }

        if (item.itemId == R.id.mp_add) {
            EventBus.getDefault().post(OpenSubscribedFragmentEvent())
        }

        if (item.itemId == R.id.mp_play) {
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

    //*********************************
    //* event listeners
    //*********************************
    @Subscribe
    fun onEvent(event: SubscribedDetailClosedEvent) {
        EventBus.getDefault().post(RefreshManualPlaylistFragment())
    }

    @Subscribe
    fun onEvent(event: PlayerClosedEvent) {
        EventBus.getDefault().post(RefreshManualPlaylistFragment())
    }

    @Subscribe
    fun onEvent(event : ManualRowActionButtonPressedEvent) {
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
                    var eid = _playlist.getEpisode(row).eid
                    PodcastDatabaseHelper.getInstance().updateEpisodeDuration(eid, 1)
                    PodcastDatabaseHelper.getInstance().updateEpisodePlayPoint(eid, 0)
                    _playlist.removeItem(-1) //will refresh episode list
                    _recyclerView.adapter.notifyItemChanged(row)
                }
                2 -> {
                    var updateRow = _playlist.getEpisode(row)
                    PodcastDatabaseHelper.getInstance().updateEpisodePlayPoint(updateRow.eid,
                            updateRow.lengthAsLong)
                    _playlist.removeItem(-1) //will refresh episode list
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

        val simpleCallback = object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            var dragFrom = -1
            var dragTo = -1

            override fun onMove(recyclerView: RecyclerView, source: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean {

                if(dragFrom == -1) {
                    dragFrom =  source.adapterPosition;
                }
                dragTo = target.adapterPosition;

                return false
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)

                if (dragFrom !== -1 && dragTo !== -1 && dragFrom !== dragTo) {
                    _playlist.moveItem(dragFrom, dragTo)
                    _viewAdapter.notifyDataSetChanged()
                }
                dragTo = -1
                dragFrom = dragTo
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
