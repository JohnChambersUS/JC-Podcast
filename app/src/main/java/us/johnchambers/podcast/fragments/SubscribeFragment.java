package us.johnchambers.podcast.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;

import java.net.URL;

import us.johnchambers.podcast.Adapters.SearchDisplayAdapter;
import us.johnchambers.podcast.Adapters.SubscribeEpisodeListAdapter;
import us.johnchambers.podcast.R;
import us.johnchambers.podcast.misc.VolleyQueue;
import us.johnchambers.podcast.objects.FeedResponseWrapper;
import us.johnchambers.podcast.objects.SearchRow;
import us.johnchambers.podcast.objects.SubscribeEpisodeRow;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SubscribeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SubscribeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubscribeFragment extends Fragment {
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
        //todo call volley to fill fragment
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

    private void init(SearchRow sr) {
        _searchRow = sr;
    }

    private void loadFeedInfo(String response) {
        _adapter = new SubscribeEpisodeListAdapter(_view.getContext());
        ListView listView = (ListView) _view.findViewById(R.id.subscribeEpisodeListView);
        listView.setAdapter(_adapter);

        _feedResponseWrapper = new FeedResponseWrapper(response);

        while (_feedResponseWrapper.nextEpisode()) {
            SubscribeEpisodeRow ser = new SubscribeEpisodeRow();
            ser.setDate(_feedResponseWrapper.getCurrEpisodeDate());
            ser.setTitle(_feedResponseWrapper.getCurrEpisodeTitle());
            ser.setDownloadLink(_feedResponseWrapper.getDownloadLink());
            _adapter.add(ser);
        }
        //todo make image volley call for top image
        getPodcastImage();
    }

    public void addImageToSubscribeScreen(Bitmap bitmap) {
        ImageView iv = (ImageView)_view.findViewById((R.id.subscribe_ResultImage));
        iv.setImageBitmap(bitmap);
    }


    //***************************
    //* Volley section
    //***************************

    private void getPodcastFeedInfo() {
        String feedUrl = null;
        try {
            feedUrl = new URL(_searchRow.getFeedUrl()).toString();
        }
        catch(Exception e) {}
        StringRequest sr = new StringRequest(Request.Method.GET,
                feedUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadFeedInfo(response);
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
        void onSubscribeFragmentBackButtonPressed();

    }
}
