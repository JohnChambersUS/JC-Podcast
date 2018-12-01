package us.johnchambers.podcast.screens.fragments.tag.tag_podcast_list

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import us.johnchambers.podcast.R
import us.johnchambers.podcast.database.PodcastDatabaseHelper
import us.johnchambers.podcast.database.PodcastTagJoinedObject
import us.johnchambers.podcast.fragments.MyFragment
import us.johnchambers.podcast.objects.FragmentBackstackType

class TagPodcastListFragment :MyFragment() {

    lateinit var _view : View
    private lateinit var _recyclerView : RecyclerView
    private lateinit var _viewAdapter: TagPodcastListRecyclerAdapter //RecyclerView.Adapter<*>
    private lateinit var _viewManager: RecyclerView.LayoutManager
    lateinit var _podcastTagList : MutableList<PodcastTagJoinedObject>
    lateinit var _workingTag : String
    private var _bottomNavigationListener: BottomNavigationView.OnNavigationItemSelectedListener? = null

    companion object {
        @JvmStatic
        fun newInstance() =
            TagPodcastListFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    public fun setTag(tag: String) {
        _workingTag = tag
    }

    override fun getBackstackType(): FragmentBackstackType {
        return FragmentBackstackType.BRANCH
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _view = inflater.inflate(R.layout.fragment_tag_podcast_list, container, false)
        _podcastTagList = PodcastDatabaseHelper.getInstance().getPodcastAndTagInfo(_workingTag)
        fillRecyclerView()
        addNavigationListener()
        val navigation = _view.findViewById(R.id.manual_navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(_bottomNavigationListener)
        return _view
    }

    //**************************************
    //* private methods
    //**************************************

    fun flipNoDataMessage() {
        var noEpisodesMessage = _view.findViewById(R.id.no_podcasts_message) as TextView
        var recyclerView = _view.findViewById(R.id.recycler_view) as RecyclerView
        if (_podcastTagList.isEmpty()) {
            noEpisodesMessage.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
        else {
            noEpisodesMessage.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }


    fun fillRecyclerView() {


        flipNoDataMessage()

        if (!_podcastTagList.isEmpty()){
            _viewManager = LinearLayoutManager(context)
            _viewAdapter = TagPodcastListRecyclerAdapter(_podcastTagList)
            _viewAdapter.setWorkingTag(_workingTag)

            _recyclerView = _view.findViewById(R.id.recycler_view) as RecyclerView

            _recyclerView.apply {
                setHasFixedSize(false)
                layoutManager = _viewManager
                adapter = _viewAdapter
            }
            // setItemTouchHelper()
        }
    }


    //*********************************
    //* bottom menu listener
    //*********************************

    private fun processNavigation(item: MenuItem) {

        if (item.itemId == R.id.mp_show_all) {
            _podcastTagList = PodcastDatabaseHelper.getInstance().getPodcastAndTagInfo(_workingTag)
            fillRecyclerView()
        }

        if (item.itemId == R.id.mp_show_tagged) {
            if (_podcastTagList.size == 0) return

            var done = false
            var currPos = _podcastTagList.size - 1
            while (!done) {
                if (_podcastTagList.get(currPos).tag != _workingTag) {
                    _podcastTagList.removeAt(currPos)
                }
                currPos--
                if (currPos < 0) done = true
            }
            fillRecyclerView()
        }
    }

    private fun addNavigationListener() {

        _bottomNavigationListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            processNavigation(item)
            true
        }
    }



}