package us.johnchambers.podcast.screens.fragments.subscribed_detail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import us.johnchambers.podcast.R;
import us.johnchambers.podcast.database.DownloadQueueTable;
import us.johnchambers.podcast.database.EpisodeTable;
import us.johnchambers.podcast.database.PodcastDatabaseHelper;
import us.johnchambers.podcast.database.PodcastTable;
import us.johnchambers.podcast.fragments.MyFragment;
import us.johnchambers.podcast.misc.MyFileManager;
//todo delete import us.johnchambers.podcast.misc.PodcastDownloader;
import us.johnchambers.podcast.misc.PodcastUpdater;
import us.johnchambers.podcast.objects.FragmentBackstackType;

public class SubscribedDetailFragment extends MyFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static PodcastTable _podcastTable = null;
    private static View _view;
    private static SubscribedDetailEpisodeListAdapter _adapter;
    private static Context _context;

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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _view = inflater.inflate(R.layout.fragment_subscribed_detail, container, false);
        //todo populate image
        displayPodcastImage();
        //todo populate table
        populateEpisodeListView();
        addSubscribedDetailPlayListener();
        setSubscribedButtonListener();
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

    private void setSubscribedButtonListener() {
        FloatingActionButton fab = (FloatingActionButton) _view.findViewById(R.id.fab_unsubscribe);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PodcastUpdater(_context, _podcastTable.getPid());
                mListener.onSubscribedDetailFragmentUnsubscribe();
            }
        });
    }

    public FragmentBackstackType getBackstackType() {
        return FragmentBackstackType.BRANCH;
    }

    private void displayPodcastImage() {
        ImageView image = (ImageView) _view.findViewById(R.id.subscribe_detail_image);
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
                //EpisodeTable row = _adapter.getItem(position);

                //todo do something
                processRowTap(adapter, position);
            }
        });
    }

    //todo redo to remove download tap
    private void processRowTap(AdapterView listView, int position) {

        EpisodeTable panelRow = _adapter.getItem(position);
        String audioUrl = PodcastDatabaseHelper.getInstance().getEpisodeAudioUrl((panelRow.getEid()));

        if (audioUrl == null) {
            Toast.makeText(_context, "Url for this episode is null", Toast.LENGTH_LONG).show();
            return;
        }

        if (audioUrl.length() > 0) {
            mListener.onSubscribedDetailFragmentDoesSomething(audioUrl);

        }
        else {
            Toast.makeText(_context, "Url for this episode is 0 length", Toast.LENGTH_LONG).show();

        }

    }

    //******************************
    //* interfaces
    //******************************

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSubscribedDetailFragmentDoesSomething(String path);
        void onSubscribedDetailFragmentUnsubscribe();
    }
}
