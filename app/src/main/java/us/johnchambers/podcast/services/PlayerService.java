package us.johnchambers.podcast.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.view.View;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
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
import us.johnchambers.podcast.database.PodcastDatabaseHelper;
import us.johnchambers.podcast.misc.MyFileManager;

public class PlayerService extends Service {

    private int _id = 59247;
    private final IBinder _binder = new MyBinder();
    private SimpleExoPlayer _player = null;
    private SimpleExoPlayerView _playerView;
    private Context _context = null;
    @NonNull private String _currUrl = "";
    private boolean _running = false;
    private long _contentPosition = 0;

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
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Podcast in progress")
                .setContentText("")
                .setContentIntent(pendingIntent)
                .build();

        startForeground(_id, notification);
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

    public void shutdownService() {
        _player.stop();
        stopForeground(true);
        stopSelf();
    }

    public void stopPlayer() {
        _player.stop();
    }

    public void pausePlayer() {
        _player.setPlayWhenReady(false);
    }

    public void play(String url) {

        String passedUrl = safeNull(url);

        if (passedUrl.equals("") && (_currUrl.equals(""))) {
            //nothing to play
            //show blank screen
            return;
        }

        if (passedUrl.equals("") && (_currUrl != "")) {
            _player.prepare(makeMediaSource(_currUrl),
                    true,
                    false);
            _player.seekTo(_contentPosition);
            _player.setPlayWhenReady(true);
            return;
        }

        if (passedUrl.equals(_currUrl)) {
            _player.prepare(makeMediaSource(_currUrl),
                    true,
                    false);
            _player.seekTo(_contentPosition);
            _player.setPlayWhenReady(true);
            return;
        }

        //if get this far then change to new url

        _currUrl = passedUrl;
        _player.prepare(makeMediaSource(passedUrl),
                true,
                false);
        _player.setPlayWhenReady(true);

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
