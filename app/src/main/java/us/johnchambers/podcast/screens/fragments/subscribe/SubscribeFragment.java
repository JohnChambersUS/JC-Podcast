package us.johnchambers.podcast.screens.fragments.subscribe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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

import java.net.URL;
import java.util.Date;

import us.johnchambers.podcast.R;
import us.johnchambers.podcast.database.EpisodeTable;
import us.johnchambers.podcast.database.PodcastDatabaseHelper;
import us.johnchambers.podcast.database.PodcastMode;
import us.johnchambers.podcast.database.PodcastTable;
import us.johnchambers.podcast.fragments.MyFragment;
import us.johnchambers.podcast.objects.FragmentBackstackType;
import us.johnchambers.podcast.misc.MyFileManager;
import us.johnchambers.podcast.misc.VolleyQueue;
import us.johnchambers.podcast.objects.FeedResponseWrapper;
import us.johnchambers.podcast.screens.fragments.search.SearchRow;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SubscribeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SubscribeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubscribeFragment extends MyFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    SearchRow _searchRow;
    FeedResponseWrapper _feedResponseWrapper;
    SubscribeEpisodeListAdapter _adapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View _view;

    public SubscribeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SubscribeFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _view = inflater.inflate(R.layout.fragment_subscribe, container, false);
        setSubscribeButtonListener();
        getPodcastFeedInfo();

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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //****************************
    //* common routines
    //***************************

    private void setSubscribeButtonListener() {
        FloatingActionButton fab = (FloatingActionButton) _view.findViewById(R.id.fab_subscribe);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPopupMenu();
            }
        });
    }

    private void openPopupMenu() {

        View button = (View) _view.findViewById(R.id.fab_subscribe);
        PopupMenu popup = new PopupMenu(button.getContext(), button, Gravity.CENTER_VERTICAL);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Toast.makeText(getContext(),
                    _feedResponseWrapper.getPodcastId(),
                    Toast.LENGTH_SHORT).show();
            item.collapseActionView();
            subscribePodcast(item.toString());
            return true;
        }
        });

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.subscribe_options_menu, popup.getMenu());
        popup.show();
    }

    public void subscribePodcast(String item) {

        //check to see if already subscribed
        boolean subscribed = PodcastDatabaseHelper
                .getInstance()
                .alreadySubscribedToPodcast(_feedResponseWrapper.getPodcastId());
        if (!subscribed) {
            addNewPodcastToDB();
            addAllEpisodesToDatabase();
            mListener.onCloseSubscribeFragment();
            Toast.makeText(getContext(),
                    "You are now subscribed to: " + _feedResponseWrapper.getPodcastTitle(),
                    Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getContext(),
                    "You are already subscribed to this podcast.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void addNewPodcastToDB() {
        PodcastTable newRow = PodcastDatabaseHelper.getInstance().getNewPodcastTableRow();
        //Store image on disk
        MyFileManager.getInstance().addPodcastImage(_feedResponseWrapper.getPodcastImage(),
                _feedResponseWrapper.getPodcastId());
        //add items to podcast table row from wrapper
        newRow.setPid(_feedResponseWrapper.getPodcastId());
        newRow.setName(_feedResponseWrapper.getPodcastTitle());
        newRow.setFeedUrl(_feedResponseWrapper.getFeedUrl());
        newRow.setSubscriptionTypeViaPodcastMode(PodcastMode.Manual);
        newRow.setDownloadInterval(0);
        newRow.setLastDownloadDateViaDate(new Date());
        //insert podcast table row
        PodcastDatabaseHelper.getInstance().insertPodcastTableRow(newRow);
    }

    private void addAllEpisodesToDatabase() {
        _feedResponseWrapper.processEpisodesFromBottom();
        while (_feedResponseWrapper.prevEpisode()) {
            EpisodeTable currEpisode = PodcastDatabaseHelper.getInstance().getNewEpisodeTableRow();

            currEpisode.setPid(_feedResponseWrapper.getPodcastId());
            currEpisode.setEid(_feedResponseWrapper.getEpisodeId());
            currEpisode.setTitle(_feedResponseWrapper.getCurrEpisodeTitle());
            currEpisode.setSummary(_feedResponseWrapper.getCurrEpisodeSummary());
            currEpisode.setAudioUrl(_feedResponseWrapper.getEpisodeDownloadLink());
            currEpisode.setPubDate(_feedResponseWrapper.getCurrEpisodeDate());
            currEpisode.setLength("0:0");
            currEpisode.setPlayedViaBoolean(false);
            currEpisode.setInProgressViaBoolean(false);
            currEpisode.setPlayPoint("0:0");
            currEpisode.setLocalDownloadUrl(null);

            PodcastDatabaseHelper.getInstance().insertEpisodeTableRow(currEpisode);
        }
    }

    private void init(SearchRow sr) {
        _searchRow = sr;
    }

    public FragmentBackstackType getBackstackType() {
        return FragmentBackstackType.BRANCH;
    }

    private void loadFeedInfo(String response, String feedUrl) {
        _adapter = new SubscribeEpisodeListAdapter(_view.getContext());
        ListView listView = (ListView) _view.findViewById(R.id.subscribeEpisodeListView);
        listView.setAdapter(_adapter);

        _feedResponseWrapper = new FeedResponseWrapper(response, feedUrl);

        while (_feedResponseWrapper.nextEpisode()) {
            SubscribeEpisodeRow ser = new SubscribeEpisodeRow();
            ser.setDate(_feedResponseWrapper.getCurrEpisodeDate());
            ser.setTitle(_feedResponseWrapper.getCurrEpisodeTitle());
            ser.setDownloadLink(_feedResponseWrapper.getEpisodeDownloadLink());
            _adapter.add(ser);
        }
        setDefaultImage();
        getPodcastImage();
    }

    public void setDefaultImage() {
        ImageView iv = (ImageView)_view.findViewById((R.id.subscribe_ResultImage));
        Bitmap bitmap =  BitmapFactory.decodeResource(getContext().getResources(),
                R.mipmap.ic_missing_podcast_image);
        iv.setImageBitmap(bitmap);
    }

    public void addImageToSubscribeScreen(Bitmap bitmap) {
        _feedResponseWrapper.setPodcastImage(bitmap);
        ImageView iv = (ImageView)_view.findViewById((R.id.subscribe_ResultImage));
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
        try {
            imageUrl = new URL(imageUrl).toString();
        }
        catch(Exception e) {}

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

        //VolleyQueue vq = VolleyQueue.getInstance();
        //RequestQueue rq = vq.getRequestQueue();
        //rq.add(ir);
        VolleyQueue.getInstance().getRequestQueue().add(ir);
    }






    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onCloseSubscribeFragment();
    }
}
