package us.johnchambers.podcast.screens.fragments.playlist_generic

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
import us.johnchambers.podcast.Events.fragment.OpenSubscribedDetailEvent
import us.johnchambers.podcast.Events.fragment.SubscribedDetailClosedEvent
import us.johnchambers.podcast.Events.latest.LatestRowActionButtonPressedEvent
import us.johnchambers.podcast.Events.player.PlayerClosedEvent
import us.johnchambers.podcast.Events.player.ResumePlaylistEvent
import us.johnchambers.podcast.R
import us.johnchambers.podcast.database.PodcastDatabaseHelper
import us.johnchambers.podcast.fragments.MyFragment
import us.johnchambers.podcast.misc.BottomNavigationViewHelper
import us.johnchambers.podcast.objects.DocketEmbededPlaylist
import us.johnchambers.podcast.objects.DocketLatest
import us.johnchambers.podcast.objects.DocketTagAllPlaylist
import us.johnchambers.podcast.objects.FragmentBackstackType
import us.johnchambers.podcast.playlists.Playlist
import us.johnchambers.podcast.playlists.PlaylistFactory
import us.johnchambers.podcast.playlists.TagAllPlaylist
import us.johnchambers.podcast.screens.fragments.playlist_latest.LatestPlaylistFragment
import us.johnchambers.podcast.screens.fragments.playlist_latest.LatestPlaylistRecyclerAdapter

class GenericPlaylistFragment : MyFragment() {


    lateinit var _playlist : Playlist
    lateinit var _view : View

    private lateinit var _recyclerView : RecyclerView
    private lateinit var _viewAdapter: GenericPlaylistRecyclerAdapter //RecyclerView.Adapter<*>
    private lateinit var _viewManager: RecyclerView.LayoutManager
    private lateinit var _tag: String
    private lateinit var _navigation: BottomNavigationView

    private var _bottomNavigationListener: BottomNavigationView.OnNavigationItemSelectedListener? = null

    companion object {
        @JvmStatic
        fun newInstance(tag: String): GenericPlaylistFragment { //todo setup so can pass value
            val args: Bundle = Bundle()
            args.putSerializable("TAG", tag)
            val fragment = GenericPlaylistFragment()
            fragment.arguments = args
            return fragment
        }
    }


    fun setTag(tag: String) {
        _tag = tag
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        //todo hardcode for testing till have set passing of tag
        var tag = arguments?.get("TAG")
        _playlist = PlaylistFactory.getPlaylist(DocketTagAllPlaylist(tag.toString())) //todo create docket for tag
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        _view = inflater.inflate(R.layout.fragment_generic_playlist, container, false) //todo I need to change this or can I use this fragment

        if (_playlist.getCurrentEpisodes().size == 0) {
            flipNoDataMessage()
        }
        else {
            _viewManager = LinearLayoutManager(context)
            _viewAdapter = GenericPlaylistRecyclerAdapter(_playlist.getCurrentEpisodes()) //todo add to recycler

            _recyclerView = _view.findViewById(R.id.latest_recycler_view) as RecyclerView //todo can I use this view?

            _recyclerView.apply {
                setHasFixedSize(false)
                layoutManager = _viewManager
                adapter = _viewAdapter //todo wrong recycler view
            }

            setItemTouchHelper()
        }
        addNavigationListener()
        _navigation = _view.findViewById(R.id.navigation) as BottomNavigationView
        _navigation.setOnNavigationItemSelectedListener(_bottomNavigationListener)
        _navigation.itemIconTintList = null
        BottomNavigationViewHelper.removeShiftMode(_navigation);

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


    private fun fillRecyclerView() {
        flipNoDataMessage()
        if (_playlist.isEmpty()) {
            return
        }
        _viewManager = LinearLayoutManager(context)
        _viewAdapter = GenericPlaylistRecyclerAdapter(_playlist.getCurrentEpisodes()) //todo add to recycler

        _recyclerView = _view.findViewById(R.id.latest_recycler_view) as RecyclerView //todo can I use this view?

        _recyclerView.apply {
            setHasFixedSize(false)
            layoutManager = _viewManager
            adapter = _viewAdapter //todo wrong recycler view

            setItemTouchHelper()
        }


    }


    override fun getBackstackType() : FragmentBackstackType {
        return FragmentBackstackType.BRANCH //todo need to put placehoder in config
    }

    //*********************************
    //* local methods
    //*********************************
    private fun refreshEpisodeView() {
        _playlist.getCurrentEpisodes() //will refresh list without wiping db
        _recyclerView.adapter.notifyDataSetChanged()
    }

    //*********************************
    //* bottom menu listener
    //*********************************

    private fun processNavigation(item: MenuItem) {
        if (item.itemId == R.id.bm_refresh) {
            _playlist.getEpisodes() //will cause an episode refresh in playlist
            fillRecyclerView()
        }

        if (item.itemId == R.id.bm_show_all) {
            _playlist.setNewOnly(false)
            _playlist.getEpisodes() //will cause an episode refresh in playlist
            fillRecyclerView()
        }

        if (item.itemId == R.id.bm_show_new) {
            _playlist.setNewOnly(true)
            _playlist.getEpisodes() //will cause an episode refresh in playlist
            fillRecyclerView()
        }

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
                    var pid = _playlist.getCurrentEpisodes().get(row).pid
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

        val simpleCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
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
                    _viewAdapter.notifyDataSetChanged() //todo should clear with adapter fix
                }
                dragTo = -1
                dragFrom = dragTo
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                var position = viewHolder.getAdapterPosition();
                _playlist.removeItem(position)
                _viewAdapter.notifyDataSetChanged() //todo should clear with adapter fix
                flipNoDataMessage()
            }



        }

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(_recyclerView)
    }



}