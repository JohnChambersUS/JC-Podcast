package us.johnchambers.podcast.services.player;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import us.johnchambers.podcast.Events.keys.AnyKeyEvent;
import us.johnchambers.podcast.Events.player.ClosePlayerEvent;
import us.johnchambers.podcast.Events.player.MediaEndedEvent;
import us.johnchambers.podcast.Events.player.TimeUpdateEvent;
import us.johnchambers.podcast.database.EpisodeTable;
import us.johnchambers.podcast.database.NowPlaying;
import us.johnchambers.podcast.database.NowPlayingTable;
import us.johnchambers.podcast.database.PodcastDatabase;
import us.johnchambers.podcast.database.PodcastDatabaseHelper;
import us.johnchambers.podcast.misc.Constants;
import us.johnchambers.podcast.misc.L;

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

        //set the episode
        if (episode != null) {
            //put episode in Now Playing
            PodcastDatabaseHelper.getInstance().updateNowPlayingEpisode(episode.getEid());
            PodcastDatabaseHelper.getInstance().updateNowPlayingPlaylist(episode.getPid());
        }
        else { //null episode
            String nowPlayingEid = PodcastDatabaseHelper.getInstance().getNowPlayingEpisodeId();
            if (nowPlayingEid != NowPlaying.NO_EPISODE_FLAG) {
                episode = PodcastDatabaseHelper.getInstance().getEpisodeTableRowByEpisodeId(nowPlayingEid);
            } else {
                episode = new EpisodeTable(); //pass empty episode table, never null
            }
        }
        //reset to beginning if has already been played
        if (episode.getPlayPointAsLong() >= episode.getLengthAsLong()) {
            episode.setPlayPoint(0);
        }

        _service.playEpisode(episode);

    }

    private void playNextEpisode() {

        if (episodeLimitReached()) {
            showStillWatchingDialog();
            return;
        }

        String currEid = PodcastDatabaseHelper.getInstance().getNowPlayingEpisodeId();
        if (currEid == NowPlaying.NO_EPISODE_FLAG) {
            return;
        }

        EpisodeTable currEpisode = PodcastDatabaseHelper.getInstance().getEpisodeTableRowByEpisodeId(currEid);
        EpisodeTable nextEpisode = PodcastDatabaseHelper.getInstance().getNextMediaPodcastPlaylist(currEpisode);
        while ((nextEpisode != null) &&
                (nextEpisode.getPlayPointAsLong() >= nextEpisode.getLengthAsLong())) {
            nextEpisode = PodcastDatabaseHelper.getInstance().getNextMediaPodcastPlaylist(nextEpisode);
        }

        if (nextEpisode != null) {
            playEpisode(nextEpisode);
        } else {
            //todo play end of playlist message
        }
    }

    private boolean episodeLimitReached() {
        _episodeCount++;
        if (_episodeCount > _episodeLimit) {
            _episodeCount = 0;
            return true;
        }
        return false;
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
            String eid = PodcastDatabaseHelper.getInstance().getNowPlayingEpisodeId();
            PodcastDatabaseHelper.getInstance().updateEpisodePlayPoint(eid, event.getCurrPosition());
            PodcastDatabaseHelper.getInstance().updateEpisodeDuration(eid, event.getLength());
        }
        catch (Exception e) {
            L.INSTANCE.i((Object) this, e.toString());
        }
    }

    @Subscribe
    public void onEvent(MediaEndedEvent event) {
        playNextEpisode();
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
                EventBus.getDefault().post(new ClosePlayerEvent());
            }
        });

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                _episodeCount = 0;
                playNextEpisode();
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }


}
