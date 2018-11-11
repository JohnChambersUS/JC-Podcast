package us.johnchambers.podcast.screens.fragments.tag

import android.content.Context
import android.content.DialogInterface
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
import android.widget.Toast
import org.greenrobot.eventbus.EventBus
import us.johnchambers.podcast.Events.fragment.OpenSubscribedFragmentEvent
import us.johnchambers.podcast.Events.fragment.RefreshManualPlaylistFragment
import us.johnchambers.podcast.Events.player.ResumePlaylistEvent
import us.johnchambers.podcast.R
import us.johnchambers.podcast.database.EpisodeTable
import us.johnchambers.podcast.database.PodcastDatabaseHelper
import us.johnchambers.podcast.database.TagTable
import us.johnchambers.podcast.fragments.MyFragment
import us.johnchambers.podcast.misc.C
import us.johnchambers.podcast.objects.DocketEmbededPlaylist
import us.johnchambers.podcast.objects.DocketEpisode
import us.johnchambers.podcast.objects.FragmentBackstackType
import android.widget.EditText



class TagFragment : MyFragment() {

    lateinit var _view : View
    lateinit var _tagList : MutableList<TagTable>

    private lateinit var _recyclerView : RecyclerView
    private lateinit var _viewAdapter: RecyclerView.Adapter<*>
    private lateinit var _viewManager: RecyclerView.LayoutManager

    private var _bottomNavigationListener: BottomNavigationView.OnNavigationItemSelectedListener? = null

    companion object {
        @JvmStatic
        fun newInstance() =
                TagFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }

    override fun getBackstackType(): FragmentBackstackType {
        return FragmentBackstackType.BRANCH
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        //fill tag list
        //_tagList = PodcastDatabaseHelper.getInstance().tagTableEntries

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _view = inflater.inflate(R.layout.fragment_tag, container, false)

        fillTagListRecyclerView()

        /*
        if (_tagList.isEmpty()) {
            flipNoDataMessage()
        }
        else {
            _viewManager = LinearLayoutManager(context)
            _viewAdapter = TagRecyclerAdapter(_tagList)

            _recyclerView = _view.findViewById(R.id.manual_recycler_view) as RecyclerView

            _recyclerView.apply {
                setHasFixedSize(false)
                layoutManager = _viewManager
                adapter = _viewAdapter
            }
            setItemTouchHelper()
        }
        */
        addNavigationListener()
        val navigation = _view.findViewById(R.id.manual_navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(_bottomNavigationListener)
        navigation.itemIconTintList = null

        return _view
    }

    //**************************************
    //* private methods
    //**************************************

    fun flipNoDataMessage() {
        var noEpisodesMessage = _view.findViewById(R.id.manual_no_episodes_message) as TextView
        var recyclerView = _view.findViewById(R.id.manual_recycler_view) as RecyclerView
        if (_tagList.isEmpty()) {
            noEpisodesMessage.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
        else {
            noEpisodesMessage.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    fun fillTagListRecyclerView() {
        _tagList = PodcastDatabaseHelper.getInstance().tagTableEntries

        flipNoDataMessage()

        if (!_tagList.isEmpty()){
            _viewManager = LinearLayoutManager(context)
            _viewAdapter = TagRecyclerAdapter(_tagList)

            _recyclerView = _view.findViewById(R.id.manual_recycler_view) as RecyclerView

            _recyclerView.apply {
                setHasFixedSize(false)
                layoutManager = _viewManager
                adapter = _viewAdapter
            }
            setItemTouchHelper()
        }




    }



    //*********************************
    //* bottom menu listener
    //*********************************

    private fun processNavigation(item: MenuItem) {

        if (item.itemId == R.id.mp_add) {
            displayAddDialog()
        }

    }

    private fun addNavigationListener() {

        _bottomNavigationListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            processNavigation(item)
            true
        }
    }




    //**********************************************
    //* setup item touch helper for recyclerview
    //**********************************************

    fun setItemTouchHelper() {

        val simpleCallback = object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(recyclerView: RecyclerView, source: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                var position = viewHolder.getAdapterPosition();
                var itemToRemove = _tagList.get(position)
                PodcastDatabaseHelper.getInstance().deleteTag(itemToRemove);
                _tagList.removeAt(position)
                //_viewAdapter.notifyDataSetChanged()
                //flipNoDataMessage()
                fillTagListRecyclerView()
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(_recyclerView)
    }

    //************************
    //* add tag functionality
    //************************
    private fun displayAddDialog() {

        val layoutInflaterAndroid = LayoutInflater.from(requireContext())
        val addView = layoutInflaterAndroid.inflate(R.layout.fragment_tag_add_dialog, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(addView)
        val userInputDialogEditText = addView.findViewById<View>(R.id.userInputDialog) as EditText
        builder.setNegativeButton("Cancel") { dialog, id -> dialog.cancel() }
        builder.setPositiveButton("Add") { dialog, id -> kotlin.run {
                updateTagTableList(userInputDialogEditText.text.toString())
                dialog.cancel()
            }
        }

        builder.show()
    }

    private fun updateTagTableList(tag: String) {
        if (tag.isBlank()) {return}
        var newTag = TagTable()
        newTag.tag = tag
        PodcastDatabaseHelper.getInstance().upsertTag(newTag)
        //update recycler view
        fillTagListRecyclerView()

    }


}