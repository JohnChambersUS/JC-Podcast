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
import android.widget.TextView;

import java.util.List;

import us.johnchambers.podcast.R;
import us.johnchambers.podcast.database.PodcastDatabaseHelper;
import us.johnchambers.podcast.database.PodcastTable;
import us.johnchambers.podcast.fragments.MyFragment;
import us.johnchambers.podcast.objects.FragmentBackstackType;

public class SubscribedFragment extends MyFragment {

    private OnFragmentInteractionListener mListener;

    SubscribedAdapter _adapter;
    private View _view;

    public SubscribedFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SubscribedFragment newInstance() {
        SubscribedFragment fragment = new SubscribedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.fragment_subscribed, container, false);
        loadSubscribedListView();
        addSubscribedListViewListener();
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
        TextView noSubscriptionsView = (TextView) _view.findViewById(R.id.noSubscriptionsMessage);
        List<PodcastTable> podcasts = PodcastDatabaseHelper.getInstance().getAllPodcastRows();
        if (podcasts.size() > 0) {
            noSubscriptionsView.setVisibility(TextView.INVISIBLE);
            for (PodcastTable podcast : podcasts) {
                _adapter.add(podcast);
            }
        } else {
            noSubscriptionsView.setVisibility(TextView.VISIBLE);
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

    //****************************
    //* listeners
    //****************************

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSubscribedFragmentRowItemClicked(PodcastTable pt);
    }
}
