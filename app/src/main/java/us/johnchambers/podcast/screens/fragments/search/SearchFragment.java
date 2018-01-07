package us.johnchambers.podcast.screens.fragments.search;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;

import java.net.URL;

import us.johnchambers.podcast.R;
import us.johnchambers.podcast.fragments.MyFragment;
import us.johnchambers.podcast.objects.FragmentBackstackType;
import us.johnchambers.podcast.misc.VolleyQueue;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends MyFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static Context _context = null;
    private static View _view;

    private SearchDisplayAdapter _adapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
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

        _view = inflater.inflate(R.layout.fragment_search, container, false);

        addListenerGoSearchButton();

        _adapter = new SearchDisplayAdapter(_context);
        ListView listView = (ListView) _view.findViewById(R.id.searchResultListView);
        listView.setAdapter(_adapter);

        addSearchResultsListViewListener();

        return _view;
    }
/*
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onSearchRowItemClicked(null);
        }
    }
*/
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
        return FragmentBackstackType.ROOT;
    }

    private void performSearch() {
        _adapter.clear();

        EditText et = (EditText) _view.findViewById(R.id.searchInputBox);
        String term = et.getText().toString();
        if (term.equals("")) {
            et.setHint("I said, enter a term");
        }
        else {
            String searchString = "https://itunes.apple.com/search?media=podcast&entity=podcast&limit=100&term="
                    + term;
            try {
                searchString = new URL(searchString).toString();
            }
            catch(Exception e) {}
            searchItunes(searchString);
        }
    }

    private void fillTable(String response) {
        ITunesCatalogResponse itr = new ITunesCatalogResponse(response);

        int resultCount = itr.getresultCount();
        Toast.makeText(_context,
                "I found " + Integer.toString(resultCount) + " podcasts",
                Toast.LENGTH_LONG).show();
        while (itr.moreRecords() == true) {
            itr.next();
            SearchRow sr = new SearchRow().setTitle(itr.getTitle()).setFeedUrl(itr.getFeedUrl());
            getPodcastImage(itr.getArtworkUrl(), sr);
        }
    }

    public void processImageResonse(Bitmap response, SearchRow sr) {
        sr.setImage(response);
        _adapter.add(sr);
        _adapter.notifyDataSetChanged();
    }



    //******************************************
    //* Listeners
    //******************************************




    private void addListenerGoSearchButton() {

        Button button = (Button) _view.findViewById(R.id.goSearchButton);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Toast.makeText(_context, "Press Listener tapped", Toast.LENGTH_LONG).show();
                hideKeyboard();
                performSearch();
            }
        });
    }

    public void hideKeyboard() {
            InputMethodManager imm = (InputMethodManager) _context.getSystemService(_context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(_view.getWindowToken(), 0);
    }

    private void addSearchResultsListViewListener() {
        ListView lv = (ListView)_view.findViewById(R.id.searchResultListView);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                SearchRow sr = _adapter.getItem(position);
                 mListener.onSearchRowItemClicked(sr);
            }
        });
    }



    //********************************************
    //* Volley section
    //********************************************

    private void searchItunes(String searchString) {

        StringRequest sr = new StringRequest(Request.Method.GET,
                searchString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        fillTable(response);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        Toast.makeText(_context,
                                "Volley Error: " + e.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                });
        VolleyQueue vq = VolleyQueue.getInstance();
        RequestQueue rq = vq.getRequestQueue();
        rq.add(sr);
    }

    public void getPodcastImage(String url, final SearchRow searchRow) {

        ImageRequest ir = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        processImageResonse(response, searchRow);
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


    //***********************************************
    //* Interfaces
    //***********************************************
    public interface OnFragmentInteractionListener {
        void onSearchRowItemClicked(SearchRow sr);
    }




}
