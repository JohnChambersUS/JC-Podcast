package us.johnchambers.podcast.screens.fragments.subscribed;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import us.johnchambers.podcast.Events.fragment.RefreshManualPlaylistFragment;
import us.johnchambers.podcast.Events.fragment.SubscribedFragmentRowItemClickedEvent;
import us.johnchambers.podcast.R;
import us.johnchambers.podcast.database.PodcastDatabaseHelper;
import us.johnchambers.podcast.database.PodcastTable;
import us.johnchambers.podcast.fragments.MyFragment;
import us.johnchambers.podcast.misc.Constants;
import us.johnchambers.podcast.misc.TapGuard;
import us.johnchambers.podcast.objects.FragmentBackstackType;

public class SubscribedFragment extends MyFragment {

    SubscribedAdapter _adapter;
    private View _view;
    private FragmentBackstackType _fragmentBackstackType = FragmentBackstackType.BRANCH;

    private TapGuard _tapGuard = new TapGuard(Constants.MINIMUM_MILLISECONDS_BETWEEN_TAPS);

    public SubscribedFragment() {
        // Required empty public constructor
    }

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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().post(new RefreshManualPlaylistFragment());
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    //*******************************************
    //* Common methods
    //*******************************************

    public void setBackstackType(FragmentBackstackType type) {
        _fragmentBackstackType = type;
    }

    public FragmentBackstackType getBackstackType() {
        return _fragmentBackstackType;
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
                if (_tapGuard.tooSoon()) return;
                PodcastTable pt = _adapter.getItem(position);
                EventBus.getDefault().post(new SubscribedFragmentRowItemClickedEvent(pt));
            }
        });
    }



}
