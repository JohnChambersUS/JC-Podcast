package us.johnchambers.podcast.services.player;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;


import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import us.johnchambers.podcast.R;
import us.johnchambers.podcast.activity.MainActivity;
import us.johnchambers.podcast.database.EpisodeTable;

import static com.google.android.exoplayer2.Player.STATE_IDLE;
import static us.johnchambers.podcast.misc.Constants.*;

public class PlayerService extends Service {

    private int _id = 59247;
    private final IBinder _binder = new MyBinder();
    private SimpleExoPlayer _player = null;
    private SimpleExoPlayerView _playerView;
    private Context _context = null;
    @NonNull private String _currUrl = "";
    @NonNull private EpisodeTable _currEpisode = new EpisodeTable();
    private boolean _running = false;
    private long _contentPosition = 0;

    Notification.Action action2;

    public PlayerService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initService();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return _binder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);

    }

    //********************************
    //* common private methods
    //********************************

    // Only run once for setup
    public void initService() {
        if (!_running) {
            _running = true;
            _context = getApplicationContext();
            makeForegroundService();
            createEmptyPlayer();
        }
    }
    // Only run once for setup
    private void makeForegroundService() {

        Notification theNo = getPlayerNotification("",
                "",
                "",
                "",
                "");

        startForeground(_id, theNo);
    }

    private void setNoticationToPaused() {
        Notification theNo = getPlayerNotification(PLAYER_REWIND,
                PLAYER_PLAY,
                PLAYER_FORWARD,
                _currEpisode.getTitle(),
                "");

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(_id, theNo);

    }

    private void setNoticationToPlaying() {
        Notification theNo = getPlayerNotification(PLAYER_REWIND,
                PLAYER_PAUSE,
                PLAYER_FORWARD,
                _currEpisode.getTitle(),
                "");

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(_id, theNo);
    }


    private Notification getBlankNotification() {
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("")
                .setContentText("")
                .build();

        return notification;
    }

    private Notification getPlayerNotification(String button1, String button2, String button3,
                                               String title, String contextText) {

        Notification.Builder notif;
        //NotificationManager nm;
        notif = new Notification.Builder(getApplicationContext());
        notif.setSmallIcon(R.mipmap.ic_launcher);
        notif.setContentTitle(title);
        notif.setContentText(contextText);
        notif.setAutoCancel(true);
        //Uri path = RingtoneManager.getDefaultUri();
        //notif.setSound(path);

        //nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (!button1.equals("")) {
            Intent yesReceive = new Intent(this,
                    PlayerNotificationBroadcastReceiver.class);
            yesReceive.setAction(button1);
            PendingIntent pendingIntentYes = PendingIntent.getBroadcast(this, 12345, yesReceive, PendingIntent.FLAG_UPDATE_CURRENT);
            notif.addAction(R.drawable.ic_notication_back, button1, pendingIntentYes);
        }

        if (!button2.equals("")) {
            Intent yesReceive2 = new Intent(this,
                    PlayerNotificationBroadcastReceiver.class);
            yesReceive2.setAction(button2);
            PendingIntent pendingIntentYes2 = PendingIntent.getBroadcast(this, 12345, yesReceive2, PendingIntent.FLAG_UPDATE_CURRENT);
            action2 = new Notification.Action(R.drawable.ic_notication_pause, button2, pendingIntentYes2);
            notif.addAction(action2);
        }

        if (!button3.equals("")) {
            Intent yesReceive3 = new Intent(this,
                    PlayerNotificationBroadcastReceiver.class);
            yesReceive3.setAction(button3);
            PendingIntent pendingIntentYes3 = PendingIntent.getBroadcast(this, 12345, yesReceive3, PendingIntent.FLAG_UPDATE_CURRENT);
            notif.addAction(R.mipmap.ic_launcher, button3, pendingIntentYes3);
        }

        return notif.build();

    }



    // Only run once for setup
    private void createEmptyPlayer() {
        _player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(_context),
                new DefaultTrackSelector(), new DefaultLoadControl());
        _player.addListener(eventListener);
    }

    private String safeNull(String value) {
        if (value == null) {
            return "";
        } else {
            return value;
        }
    }

    private EpisodeTable safeNull(EpisodeTable value) {
        if ((value == null) || !(value instanceof EpisodeTable)) {
            return new EpisodeTable();
        } else {
            return value;
        }
    }


    //************************************
    //* common public methods
    //************************************

    public void attachPlayerToView(SimpleExoPlayerView playerView) {
            _playerView = playerView;
            _playerView.setPlayer(_player);
            _playerView.setControllerAutoShow(false);
            _playerView.setControllerShowTimeoutMs(0);
    }

    public String getCurrentUrl() {
        return _currUrl;
    }

    public EpisodeTable getCurrentEpisode() {
        return _currEpisode;
    }

    public void shutdownService() {
        _player.stop();
        stopForeground(true);
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(_id);
        nm.cancelAll();
        stopSelf();
    }

    public void stopPlayer() {
        _player.stop();
    }

    public void pausePlayer() {
        _player.setPlayWhenReady(false);
    }

    public void resumePlayer() {
        _player.setPlayWhenReady(true);
    }

    public void flipPlayerState() {
        if (_player.getPlayWhenReady()) {
            _player.setPlayWhenReady(false);
        } else {
            _player.setPlayWhenReady(true);
        }
    }

    public void forwardPlayer() {
        _player.seekTo(_player.getContentPosition() + 30000);
    }

    public void rewindPlayer() {
        _player.seekTo(_player.getContentPosition() - 30000);
    }

    public void playEpisode(EpisodeTable episode) {

        episode = safeNull(episode);
        String episodeAudioUrl = safeNull(episode.getAudioUrl());
        String currAudioUrl = safeNull(_currEpisode.getAudioUrl());

        if (episodeAudioUrl.equals("") && currAudioUrl.equals("")) {
            //nothing to play
            //show blank screen
            return;
        }

        if (episodeAudioUrl.equals("") && !currAudioUrl.equals("")) {
            _player.prepare(makeMediaSource(_currEpisode.getAudioUrl()),
                    true,
                    false);
            _player.seekTo(_contentPosition);
            _player.setPlayWhenReady(true);
            return;
        }

        if (episodeAudioUrl.equals(currAudioUrl)) {
            _player.prepare(makeMediaSource(_currEpisode.getAudioUrl()),
                    true,
                    false);
            _player.seekTo(_contentPosition);
            _player.setPlayWhenReady(true);
            return;
        }

        //if get this far then change to new url

        _currEpisode = episode;
        _player.prepare(makeMediaSource(episode.getAudioUrl()),
                true,
                false);
        _player.setPlayWhenReady(true);

        setNoticationToPlaying();
    }

    private MediaSource makeMediaSource(String url) {

        MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(url),
                createDataSourceFactory(),
                new DefaultExtractorsFactory(),
                null,
                null);

        return mediaSource;
    }

    private DefaultDataSourceFactory createDataSourceFactory() {
        String userAgent = Util.getUserAgent(_context, "jcpodcast-exoplayer-agent");

        // Default parameters, except allowCrossProtocolRedirects is true
        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(
                userAgent,
                null,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true /* allowCrossProtocolRedirects */
        );

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                _context,
                null,
                httpDataSourceFactory
        );

        return dataSourceFactory;
    }

    //***************************************
    //* classes
    //***************************************
    public class MyBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }

    //*********************************************
    //* listeners
    //*********************************************

    Player.EventListener eventListener = new Player.EventListener() {

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            _contentPosition = _player.getContentPosition();

            if (playWhenReady == false) {
                setNoticationToPaused();
            }

            if (playWhenReady == true) {
                setNoticationToPlaying();
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {

        }

        @Override
        public void onPositionDiscontinuity() {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }
    };




}
