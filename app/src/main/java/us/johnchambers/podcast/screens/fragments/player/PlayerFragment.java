package us.johnchambers.podcast.screens.fragments.player;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import us.johnchambers.podcast.R;
import us.johnchambers.podcast.database.EpisodeTable;
import us.johnchambers.podcast.database.PodcastDatabaseHelper;
import us.johnchambers.podcast.database.PodcastTable;
import us.johnchambers.podcast.fragments.MyFragment;
import us.johnchambers.podcast.misc.MyFileManager;
import us.johnchambers.podcast.misc.MyPlayer;
import us.johnchambers.podcast.misc.Utils;
import us.johnchambers.podcast.services.player.PlayerService;
import us.johnchambers.podcast.services.player.PlayerServiceController;
import us.johnchambers.podcast.objects.FragmentBackstackType;
import us.johnchambers.podcast.misc.Utils.*;


public class PlayerFragment extends MyFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static Context _context = null;
    private View _view;

    private OnFragmentInteractionListener mListener;

    private SimpleExoPlayerView _playerView;
    public static EpisodeTable _currEpisode = null;

    private static MyPlayer _player;

    public PlayerFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static PlayerFragment newInstance() {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        _currEpisode = null;
        return fragment;
    }

    public static PlayerFragment newInstance(String episodeId) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        _currEpisode = PodcastDatabaseHelper.getInstance().getEpisodeTableRowByEpisodeId(episodeId);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    } //end of onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _view =  inflater.inflate(R.layout.fragment_player, container, false);
        _playerView = (SimpleExoPlayerView) _view.findViewById(R.id.video_view);
        attachPlayerToView();
        playEpisode();
        setImage();
        return _view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onPlayerFragmentDoesSomething();
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
        _context = context;
    }

    @Override
    public void onDetach() {
        PlayerServiceController.getInstance().pausePlayer();
        super.onDetach();
        mListener = null;
        PlayerServiceController.getInstance().stopPlayer();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    //*************************************
    //* methods
    //*************************************
    public FragmentBackstackType getBackstackType() {
        return FragmentBackstackType.BRANCH;
    }

    public void attachPlayerToView() {
        PlayerServiceController pc = PlayerServiceController.getInstance(_context);
        pc.attachPlayerToView(_playerView);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onPlayerFragmentDoesSomething();
    }

    private void setImage() {
        Bitmap podcastPicture = null;

        String pid = PlayerServiceController.getInstance().getNowPlayingPodcastId();

        if (pid.equals("")) {
            podcastPicture = BitmapFactory.decodeResource(_context.getResources(),
                    R.raw.nopodcast);
        } else {
            podcastPicture = MyFileManager.getInstance().getPodcastImage(pid);
            if (podcastPicture == null) {
                podcastPicture = BitmapFactory.decodeResource(_context.getResources(),
                        R.raw.missing_podcast_image);
            }
        }
        _playerView.setDefaultArtwork(podcastPicture);
    }

    private void playEpisode() {
        PlayerServiceController.getInstance().playEpisode(_currEpisode);
    }

}
