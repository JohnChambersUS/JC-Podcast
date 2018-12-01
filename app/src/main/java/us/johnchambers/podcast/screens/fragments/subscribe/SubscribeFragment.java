package us.johnchambers.podcast.screens.fragments.subscribe;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.Date;
import java.util.List;

import us.johnchambers.podcast.Events.fragment.CloseSubscribeFragmentEvent;
import us.johnchambers.podcast.Events.player.ResumePlaylistEvent;
import us.johnchambers.podcast.Events.service.UpdatePodcastsEvent;
import us.johnchambers.podcast.R;
import us.johnchambers.podcast.database.EpisodeTable;
import us.johnchambers.podcast.database.PodcastDatabaseHelper;
import us.johnchambers.podcast.database.PodcastMode;
import us.johnchambers.podcast.database.PodcastTable;
import us.johnchambers.podcast.fragments.MyFragment;
import us.johnchambers.podcast.misc.C;
import us.johnchambers.podcast.misc.Constants;
import us.johnchambers.podcast.misc.TapGuard;
import us.johnchambers.podcast.objects.DocketPodcast;
import us.johnchambers.podcast.objects.FragmentBackstackType;
import us.johnchambers.podcast.misc.MyFileManager;
import us.johnchambers.podcast.misc.VolleyQueue;
import us.johnchambers.podcast.objects.FeedResponseWrapper;
import us.johnchambers.podcast.screens.fragments.search.SearchRow;

public class SubscribeFragment extends MyFragment {

    SearchRow _searchRow;
    FeedResponseWrapper _feedResponseWrapper;
    SubscribeEpisodeListAdapter _adapter;

    //private OnFragmentInteractionListener mListener;
    private View _view;
    private View _header;
    private Context _context;

    private BottomNavigationView.OnNavigationItemSelectedListener _bottomNavigationListener;

    private TapGuard _tapGuard = new TapGuard(Constants.MINIMUM_MILLISECONDS_BETWEEN_TAPS);

    public SubscribeFragment() {
        // Required empty public constructor
    }

    public static SubscribeFragment newInstance(SearchRow sr) {
        SubscribeFragment fragment = new SubscribeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.init(sr);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.fragment_subscribe, container, false);
        _header = inflater.inflate(R.layout.fragment_subscribe_header, null, false);
        //setSubscribeButtonListener();
        addNavigationListener();
        BottomNavigationView navigation = (BottomNavigationView) _view.findViewById(R.id.navigation2);
        navigation.setOnNavigationItemSelectedListener(_bottomNavigationListener);
        navigation.setItemIconTintList(null);

        getPodcastFeedInfo();

        return _view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //****************************
    //* common routines
    //***************************
    
    //*********************************
    //* bottom menu listener
    //*********************************

    private void processNavigation(MenuItem item) {
        if (_tapGuard.tooSoon()) return;
        if (item.getItemId() == R.id.bm_subscribe) {
            openSubscribeDialog();
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

    private void openSubscribeDialog() {
        final String[] mode = new String[1];
        mode[0] = Constants.PLAYBACK_MODE_PODCAST;
        final String[] choices = new String[]{"Podcast", "Book"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose a listening mode:");

        builder.setSingleChoiceItems(choices, 0,
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (choices[whichButton] == "Podcast") {
                    mode[0] = Constants.PLAYBACK_MODE_PODCAST;
                }
                else {
                    mode[0] = Constants.PLAYBACK_MODE_BOOK;
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                subscribePodcast(mode[0]);
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }


    private void openPopupMenu(String value) {
        subscribePodcast("podcast");
    }
    
    public void subscribePodcast(String mode) {
        //check to see if already subscribed
        boolean subscribed = false;
        try {
            subscribed = PodcastDatabaseHelper
                    .getInstance()
                    .alreadySubscribedToPodcast(_feedResponseWrapper.getPodcastId());
        }
        catch (Exception e) {
            subscribed = true;
        }
        if (!subscribed) {
            addNewPodcastToDB(mode);
            //start subscription in background instead of doing it here
            //addAllEpisodesToDatabase();
            //put event bus to start service
            EventBus.getDefault().post(new UpdatePodcastsEvent());

            Toast.makeText(getContext(),
                    "Adding episodes now for "
                            + _feedResponseWrapper.getPodcastTitle()
                            + ", it may take a few moments",
                    Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getContext(),
                    "You are already subscribed to this podcast.",
                    Toast.LENGTH_SHORT).show();
        }
        EventBus.getDefault().post(new CloseSubscribeFragmentEvent());
    }

    private void addNewPodcastToDB(String mode) {
        MyFileManager.getInstance().addPodcastImage(_feedResponseWrapper.getPodcastImage(),
                _feedResponseWrapper.getPodcastId());

        PodcastTable newRow = _feedResponseWrapper.getFilledPodcastTable(mode);
        PodcastDatabaseHelper.getInstance().insertPodcastTableRow(newRow);
        C.podcasts.INSTANCE.getUPDATE_STACK().push(newRow);
    }
    /*
    private void addAllEpisodesToDatabase() {
        _feedResponseWrapper.processEpisodesFromBottom();
        while (_feedResponseWrapper.prevEpisode()) {
            EpisodeTable currEpisode = _feedResponseWrapper.getFilledEpisodeTable();
            PodcastDatabaseHelper.getInstance().insertEpisodeTableRow(currEpisode);
        }
    }
    */
    private void init(SearchRow sr) {
        _searchRow = sr;
    }

    public FragmentBackstackType getBackstackType() {
        return FragmentBackstackType.BRANCH;
    }

    private void loadFeedInfo(String response, String feedUrl) {
        _adapter = new SubscribeEpisodeListAdapter(_view.getContext());
        ListView listView = (ListView) _view.findViewById(R.id.subscribeEpisodeListView);
        listView.addHeaderView(_header);
        listView.setAdapter(_adapter);

        _feedResponseWrapper = new FeedResponseWrapper(response, feedUrl);
        boolean valid = _feedResponseWrapper.dataIsValid();
        if (!_feedResponseWrapper.dataIsValid()) {
            Toast.makeText(getContext(), "BAD DATA! UNABLE TO LOAD PODCAST INFO. " + _feedResponseWrapper.getErrorMessage(),
                    Toast.LENGTH_LONG).show();
            return;
        }

        while (_feedResponseWrapper.nextEpisode()) {
            SubscribeEpisodeRow ser = new SubscribeEpisodeRow();
            ser.setDate(_feedResponseWrapper.getCurrEpisodeDate());
            ser.setTitle(_feedResponseWrapper.getCurrEpisodeTitle());
            ser.setDownloadLink(_feedResponseWrapper.getEpisodeDownloadLink());
            _adapter.add(ser);
        }
        setDefaultImage(); //in case no image ever gets pulled
        addImageToSubscribeScreen(_searchRow.getImage()); //use from podcast index image
        getPodcastImage(); //pull image from feedurl if available

    }

    public void setDefaultImage() {
        ImageView iv = (ImageView)_view.findViewById((R.id.subscribe_detail_image));
        Bitmap bitmap =  BitmapFactory.decodeResource(getContext().getResources(),
                R.raw.missing_podcast_image);
        iv.setImageBitmap(bitmap);
    }

    public void addImageToSubscribeScreen(Bitmap bitmap) {
        _feedResponseWrapper.setPodcastImage(bitmap);
        ImageView iv = (ImageView)_view.findViewById((R.id.subscribe_detail_image));
        iv.setImageBitmap(bitmap);
    }

    //***************************
    //* Volley section
    //***************************

    private void getPodcastFeedInfo() {
        Toast.makeText(getContext(),
                "Getting podcast info, this may take a few moments if there are lots of episodes.",
                Toast.LENGTH_LONG).show();
        String feedUrl = null;
        try {
            feedUrl = new URL(_searchRow.getFeedUrl()).toString();
        }
        catch(Exception e) {}
        final String feedUrlFinal = feedUrl;
        StringRequest sr = new StringRequest(Request.Method.GET,
                feedUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadFeedInfo(response, feedUrlFinal);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        int z = 1;
                    }
                });
        VolleyQueue vq = VolleyQueue.getInstance();
        RequestQueue rq = vq.getRequestQueue();
        rq.add(sr);
    }

    private void getPodcastImage() {
        String imageUrl = _feedResponseWrapper.getLogoUrl();
        if (imageUrl == null) {
            setDefaultImage();
        } else {
            try {
                imageUrl = new URL(imageUrl).toString();
            } catch (Exception e) {}

            ImageRequest ir = new ImageRequest(imageUrl,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            addImageToSubscribeScreen(response);
                        }
                    },
                    500,
                    500,
                    Bitmap.Config.ARGB_8888,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError e) {
                            int z = 1;
                        }
                    }
            );

            VolleyQueue.getInstance().getRequestQueue().add(ir);
        }

    }
}
