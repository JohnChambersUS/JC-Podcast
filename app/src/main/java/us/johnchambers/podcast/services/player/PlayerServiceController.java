package us.johnchambers.podcast.services.player;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import us.johnchambers.podcast.Events.keys.AnyKeyEvent;
import us.johnchambers.podcast.Events.player.ClosePlayerEvent;
import us.johnchambers.podcast.Events.player.MediaEndedEvent;
import us.johnchambers.podcast.Events.player.TimeUpdateEvent;
import us.johnchambers.podcast.database.EpisodeTable;
import us.johnchambers.podcast.objects.Docket;
import us.johnchambers.podcast.playlists.NowPlaying;
import us.johnchambers.podcast.database.PodcastDatabaseHelper;
import us.johnchambers.podcast.misc.Constants;
import us.johnchambers.podcast.misc.L;
import us.johnchambers.podcast.playlists.Playlist;
import us.johnchambers.podcast.playlists.PlaylistFactory;

/**
 * Created by johnchambers on 1/22/18.
 */

public class PlayerServiceController {

    private static PlayerServiceController _instance = null;
    private static PlayerService _service = null;
    private static boolean _serviceBound = false;
    private static Context _context = null;

    private int _episodeCount = 0;
    private int _episodeLimit = Constants.EPISODE_LIMIT;

    private Playlist _playlist;

    private Boolean showDoneDialog = false;

    public PlayerServiceController() {}

    public static PlayerServiceController getInstance() {
        return _instance;
    }

    public static PlayerServiceController getInstance(Context context) {
        _context = context;
        if (_instance == null) {
            _instance = new PlayerServiceController();
            startService();
            EventBus.getDefault().register(_instance);
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

    public String getNowPlayingPodcastId() {
        String eid = PodcastDatabaseHelper.getInstance().getNowPlayingEpisodeId();
        if (eid == NowPlaying.NO_EPISODE_FLAG) {
            return "";
        }
        else {
            return PodcastDatabaseHelper.getInstance().getEpisodeTableRowByEpisodeId(eid).getPid();
        }
    }

    public void stopService() {
       _service.shutdownService();
    }

    public void stopPlayer() {
        _episodeCount = 0;
        _service.stopPlayer();
    }

    public void pausePlayer() {
        _service.pausePlayer();
    }

    public void resumePlayer() {
        _service.resumePlayer();
    }

    public void flipPlayerState() {
        _service.flipPlayerState();
    }

    public void forwardPlayer() {
        _service.forwardPlayer();
    }

    public void rewindPlayer() {
        _service.rewindPlayer();
    }

    private void playNextPlaylistEpisode() {
        if (episodeLimitReached()) {
            showStillWatchingDialog();
            return;
        }

        EpisodeTable nextEpisode = _playlist.getNextEpisode();
        if (nextEpisode.isEmpty()) {
            showEndOfPlaylistDialog();
            EventBus.getDefault().post(new ClosePlayerEvent());
            return; //get out
        }
        String p = _playlist.getPlaylistId();
        NowPlaying.INSTANCE.update(_playlist.getPlaylistId(), nextEpisode.getEid());
        _service.playEpisode(nextEpisode);
    }

    private boolean episodeLimitReached() {
        _episodeCount++;
        if (_episodeCount > _episodeLimit) {
            _episodeCount = 0;
            return true;
        }
        return false;
    }

    //*********************************
    //* playlist related functionality
    //*********************************
    public void playPlaylist(Docket docket) {
        showDoneDialog = false;
        _playlist = PlaylistFactory.INSTANCE.getPlaylist(docket);
        if (!_playlist.isEmpty()) {
            playNextPlaylistEpisode();
        } else {
            showEmptyPlaylistDialog();
        }
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

    //****************************
    //* Events
    //****************************
    @Subscribe
    public void onEvent(TimeUpdateEvent event){
        try {
            NowPlaying.INSTANCE
                    .updateEpisodePlayPointAndLength(event.getCurrPosition(), event.getLength());
        }
        catch (Exception e) {
            L.INSTANCE.i((Object) this, e.toString());
        }
    }

    @Subscribe
    public void onEvent(MediaEndedEvent event) {
        playNextPlaylistEpisode();
    }

    @Subscribe
    public void onEvent(AnyKeyEvent event) {
        //any interaction with the phone assumes user is aware of episodes playing
        _episodeCount = 0;
    }

    //***************************
    //* Dialog
    //***************************
    private void showStillWatchingDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(_context);
        builder.setMessage("")
                .setTitle("Are you still listening?");

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                //todo is causing MEDIA_ENDING event in player and makes it skip episode
                //todo figure out how to do this without triggering double event
                EventBus.getDefault().post(new ClosePlayerEvent());
            }
        });

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                _episodeCount = 0;
                playNextPlaylistEpisode();
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void showEndOfPlaylistDialog() {

        //flip so only shows on first ending
        showDoneDialog = !showDoneDialog;
        if (!showDoneDialog) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(_context);
        builder.setMessage("There are no more episodes in the episode queue.")
                .setTitle("You have reached the end of the playlist.");

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void showEmptyPlaylistDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(_context);
        builder.setMessage("")
                .setTitle("There are no episodes in this playlist.");

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }


}
