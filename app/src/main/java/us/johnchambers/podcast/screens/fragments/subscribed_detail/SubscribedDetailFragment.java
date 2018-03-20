package us.johnchambers.podcast.screens.fragments.subscribed_detail;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import us.johnchambers.podcast.Events.player.ResumePlaylistEvent;
import us.johnchambers.podcast.R;
import us.johnchambers.podcast.database.EpisodeTable;
import us.johnchambers.podcast.database.PodcastDatabaseHelper;
import us.johnchambers.podcast.database.PodcastTable;
import us.johnchambers.podcast.fragments.MyFragment;
import us.johnchambers.podcast.misc.MyFileManager;
import us.johnchambers.podcast.objects.DocketEpisode;
import us.johnchambers.podcast.objects.DocketPodcast;
import us.johnchambers.podcast.objects.FragmentBackstackType;

public class SubscribedDetailFragment extends MyFragment {

    private OnFragmentInteractionListener mListener;

    private static PodcastTable _podcastTable = null;
    private static View _view;
    private static View _header;
    private static SubscribedDetailEpisodeListAdapter _adapter;
    private static Context _context;

    private BottomNavigationView.OnNavigationItemSelectedListener _bottomNavigationListener;

    public SubscribedDetailFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SubscribedDetailFragment newInstance(PodcastTable pt) {
        SubscribedDetailFragment fragment = new SubscribedDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        _podcastTable = pt;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.fragment_subscribed_detail, container, false);

        _header = inflater.inflate(R.layout.fragment_subscribed_detail_header,
                null, false);

        //todo populate image
        displayPodcastImage();

        //todo populate table
        populateEpisodeListView();
        addSubscribedDetailPlayListener();

        addNavigationListener();
        BottomNavigationView navigation = (BottomNavigationView) _view.findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(_bottomNavigationListener);
        navigation.setItemIconTintList(null);

        return _view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        _context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //*******************************************
    //* Common methods
    //*******************************************

    public FragmentBackstackType getBackstackType() {
        return FragmentBackstackType.BRANCH;
    }

    private void displayPodcastImage() {
        ImageView image = (ImageView) _header.findViewById(R.id.subscribed_detail_image);
        Bitmap pcImage = MyFileManager.getInstance().getPodcastImage(_podcastTable.getPid());
        if (pcImage == null) {
            pcImage =  BitmapFactory.decodeResource(_context.getResources(),
                    R.raw.missing_podcast_image);
        }
        image.setImageBitmap(pcImage);
    }

    private void populateEpisodeListView() {

        _adapter = new SubscribedDetailEpisodeListAdapter(_view.getContext());
        ListView listView = (ListView) _view.findViewById(R.id.subscribed_detail_episode_list_view);
        listView.addHeaderView(_header);
        listView.setAdapter(_adapter);

        List<EpisodeTable> episodeList = PodcastDatabaseHelper.getInstance()
                .getEpisodesSortedNewest(_podcastTable.getPid());

        for (EpisodeTable episode : episodeList) {
            _adapter.add(episode);
        }

    }

    private void addSubscribedDetailPlayListener() {
        ListView lv = (ListView)_view.findViewById(R.id.subscribed_detail_episode_list_view);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                processRowTap(adapter, position);
            }
        });
    }

    private void processRowTap(AdapterView listView, int position) {
        EpisodeTable panelRow = _adapter.headerListGetItem(position);
        EventBus.getDefault().post(new ResumePlaylistEvent(new DocketEpisode(panelRow.getEid())));
    }

    //******************************
    //* interfaces
    //******************************

    public interface OnFragmentInteractionListener {
        void onSubscribedDetailFragmentUnsubscribe();
    }

    //*********************************
    //* bottom menu listener
    //*********************************

    private void processNavigation(MenuItem item) {
        if (item.getItemId() == R.id.bm_unsubscribe) {
            unsubscribeDialog();
        }
        if (item.getItemId() == R.id.bm_play) {

            //picked up by main activity to flow
            EventBus.getDefault()
                    .post(new ResumePlaylistEvent(new DocketPodcast(_podcastTable.getPid())));
        }
    }

    private void addNavigationListener() {

        _bottomNavigationListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                processNavigation(item);
                return true;
            }

        };

    }

    //***************************
    //* Dialog
    //***************************
    private void unsubscribeDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(_context);
        builder.setMessage("")
                .setTitle("Do you really want to unsubscribe from this podcast?");

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                PodcastDatabaseHelper.getInstance().removeEntirePodcast( _podcastTable.getPid());
                mListener.onSubscribedDetailFragmentUnsubscribe();
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

}
