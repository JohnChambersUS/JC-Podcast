package us.johnchambers.podcast.screens.fragments.subscribed;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import us.johnchambers.podcast.R;
import us.johnchambers.podcast.database.PodcastDatabaseHelper;
import us.johnchambers.podcast.database.PodcastTable;
import us.johnchambers.podcast.fragments.MyFragment;
import us.johnchambers.podcast.objects.FragmentBackstackType;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SubscribedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SubscribedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubscribedFragment extends MyFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    SubscribedAdapter _adapter;
    private View _view;

    public SubscribedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SubscribedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SubscribedFragment newInstance() {
        SubscribedFragment fragment = new SubscribedFragment();
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
        _view = inflater.inflate(R.layout.fragment_subscribed, container, false);
        loadSubscribedListView();
        addSubscribedListViewListener();
        return _view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            //mListener.onFragmentInteraction(uri);
        }
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

    //*******************************************
    //* Common methods
    //*******************************************

    public FragmentBackstackType getBackstackType() {
        return FragmentBackstackType.ROOT;
    }

    private void loadSubscribedListView() {
        _adapter = new SubscribedAdapter(_view.getContext());
        ListView listView = (ListView) _view.findViewById(R.id.subscribedListView);
        listView.setAdapter(_adapter);

        List<PodcastTable> podcasts = PodcastDatabaseHelper.getInstance().getAllPodcastRows();
        for (PodcastTable podcast : podcasts) {
            _adapter.add(podcast);
        }
    }

    private void addSubscribedListViewListener() {
        ListView lv = (ListView)_view.findViewById(R.id.subscribedListView);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                PodcastTable pt = _adapter.getItem(position);
                mListener.onSubscribedFragmentRowItemClicked(pt);
            }
        });
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
        void onSubscribedFragmentRowItemClicked(PodcastTable pt);
    }
}
