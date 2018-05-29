package us.johnchambers.podcast.screens.fragments.playlist_manual


import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import us.johnchambers.podcast.Events.fragment.OpenSubscribeFragment
import us.johnchambers.podcast.Events.fragment.OpenSubscribedDetailEvent
import us.johnchambers.podcast.Events.fragment.OpenSubscribedFragmentEvent
import us.johnchambers.podcast.Events.fragment.RefreshManualPlaylistFragment
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
            var noEpisodesMessage = _view.findViewById(R.id.manual_no_episodes_message) as TextView
            var recyclerView = _view.findViewById(R.id.manual_recycler_view) as RecyclerView
            noEpisodesMessage.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
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

            _recyclerView.setItemAnimator(null)
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
    fun onEvent(event: PlayerClosedEvent) {
        EventBus.getDefault().post(RefreshManualPlaylistFragment())
    }


}
