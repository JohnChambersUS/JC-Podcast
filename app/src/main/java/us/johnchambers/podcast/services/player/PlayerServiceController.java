package us.johnchambers.podcast.services.player;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import us.johnchambers.podcast.database.EpisodeTable;
import us.johnchambers.podcast.database.NowPlaying;
import us.johnchambers.podcast.database.PodcastDatabaseHelper;

/**
 * Created by johnchambers on 1/22/18.
 */

public class PlayerServiceController {

    private static PlayerServiceController _instance = null;
    private static PlayerService _service = null;
    private static boolean _serviceBound = false;
    private static Context _context = null;

    public PlayerServiceController() {}

    public static PlayerServiceController getInstance() {
        return _instance;
    }

    public static PlayerServiceController getInstance(Context context) {
        _context = context;
        if (_instance == null) {
            _instance = new PlayerServiceController();
            startService();
        }

        return _instance;
    }

    //**********************************
    //* common methods
    //**********************************

    public void init() {
        if (_service == null) {
            startService();
        }
    }

    public void attachPlayerToView(SimpleExoPlayerView playerView) {
        init();
        while (_service == null) {
            try {
                "".wait(1000);
            } catch(Exception e) {}

        }
        _service.attachPlayerToView(playerView);
    }

    public String getCurrentUrl() {
        return _service.getCurrentUrl();
    }

    public EpisodeTable getCurrentEpisode() {
        return _service.getCurrentEpisode();
    }

    public void stopService() {
       _service.shutdownService();
    }

    public void stopPlayer() {
        _service.stopPlayer();
    }

    public void pausePlayer() {
        _service.pausePlayer();
    }

    public void resumePlayer() {
        _service.resumePlayer();
    }

    public void flipPlayerState() {
        Toast.makeText(_context, "IN PLAYER", Toast.LENGTH_LONG).show();
        _service.flipPlayerState();
    }

    public void forwardPlayer() {
        _service.forwardPlayer();
    }

    public void rewindPlayer() {
        _service.rewindPlayer();
    }

    // play episode without playlist
    // assume it to be the podcast
    public void playEpisode(EpisodeTable episode) {
        PodcastDatabaseHelper.getInstance().updateNowPlayingEpisode(episode.getEid());
        PodcastDatabaseHelper.getInstance().updateNowPlayingPlaylist(episode.getPid());
        _service.playEpisode(episode);
    }

    //*******************************************************
    //* service not started with new
    //* started with intent
    //* _service assigned value in serviceConnection method
    //********************************************************
    private static void startService() {
        if (_service == null) {
            Intent intent = new Intent(_context, PlayerService.class);
            _context.startService(intent);
            _context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    //***************************
    //* connection
    //***************************
    private static ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PlayerService.MyBinder myBinder = (PlayerService.MyBinder) iBinder;
            _service = myBinder.getService();
            _serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            _serviceBound = false;
        }
    };



}
