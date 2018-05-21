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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import us.johnchambers.podcast.Events.player.PlayerClosedEvent;
import us.johnchambers.podcast.R;
import us.johnchambers.podcast.database.EpisodeTable;
import us.johnchambers.podcast.database.PodcastDatabaseHelper;
import us.johnchambers.podcast.fragments.MyFragment;
import us.johnchambers.podcast.misc.MyFileManager;
import us.johnchambers.podcast.objects.Docket;
import us.johnchambers.podcast.services.player.PlayerServiceController;
import us.johnchambers.podcast.objects.FragmentBackstackType;


public class PlayerFragment extends MyFragment {

    private static Context _context = null;
    private View _view;

    private SimpleExoPlayerView _playerView;

    private static Docket _currDocket;

    public PlayerFragment() {
        // Required empty public constructor
    }

    public static PlayerFragment newInstance(Docket docket) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        _currDocket = docket;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    } //end of onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _view =  inflater.inflate(R.layout.fragment_player, container, false);
        _playerView = (SimpleExoPlayerView) _view.findViewById(R.id.video_view);
        attachPlayerToView();
        playEpisode();
        return _view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _context = context;
    }

    @Override
    public void onDetach() {
        PlayerServiceController.getInstance().pausePlayer();
        super.onDetach();
        PlayerServiceController.getInstance().stopPlayer();
        EventBus.getDefault().post(new PlayerClosedEvent());
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

    private void playEpisode() {
        PlayerServiceController.getInstance().playPlaylist(_currDocket);
    }

}
