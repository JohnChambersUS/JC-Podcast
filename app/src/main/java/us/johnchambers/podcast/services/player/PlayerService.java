package us.johnchambers.podcast.services.player;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
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

import org.greenrobot.eventbus.EventBus;

import java.net.URL;
import java.util.Iterator;

import us.johnchambers.podcast.Events.keys.AnyKeyEvent;
import us.johnchambers.podcast.Events.player.MediaEndedEvent;
import us.johnchambers.podcast.Events.player.TimeUpdateEvent;
import us.johnchambers.podcast.R;
import us.johnchambers.podcast.database.EpisodeTable;
import us.johnchambers.podcast.database.PodcastDatabaseHelper;
import us.johnchambers.podcast.database.PodcastTable;
import us.johnchambers.podcast.misc.VolleyQueue;

import static android.media.AudioManager.STREAM_MUSIC;
import static com.google.android.exoplayer2.Player.STATE_ENDED;
import static com.google.android.exoplayer2.Player.STATE_READY;
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

    private PowerManager.WakeLock wakeLock;

    Notification.Action action2;

    EventBus _eventBus = EventBus.getDefault();

    MediaSessionCompat _mediaSession;
    AudioManager _audioManager;

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
        setMediaReceiver();
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
        notif = new Notification.Builder(getApplicationContext());
        notif.setSmallIcon(R.mipmap.ic_launcher);
        notif.setContentTitle(title);
        notif.setContentText(contextText);
        notif.setAutoCancel(true);

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

        setDefaultImage(episode);
        if (episode.isEmpty()) {
            return;
        }

        String episodeAudioUrl = safeNull(episode.getAudioUrl());

        if (episodeAudioUrl.equals("")) {
            return;
        }
        else {
            _player.prepare(makeMediaSource(episode.getAudioUrl()),
                    true,
                    false);
            _player.seekTo(episode.getPlayPointAsLong());
        }

        setNoticationToPlaying();
        _player.setPlayWhenReady(true);
        requestAudioFocus();

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
            if (_player.getAudioFormat() == null) {
                return;
            }
            _eventBus.post(new TimeUpdateEvent(_player.getContentPosition(),
                    _player.getDuration()));

            if (playWhenReady == false) {
                setNoticationToPaused();
                _eventBus.post(new AnyKeyEvent());
            }

            if (playWhenReady == true) {
                setNoticationToPlaying();
            }

            if ((playbackState == STATE_ENDED) && (playWhenReady == true)) {
                _eventBus.post(new MediaEndedEvent());
            }

            if ((playbackState != STATE_ENDED)
                    && (playbackState != STATE_READY)
                    && (playWhenReady == true)) {
                _eventBus.post(new AnyKeyEvent());
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

    //******************************
    //* volley section
    //******************************
    public void setDefaultImage(EpisodeTable episode) {

        if (episode.isEmpty()) {
            Bitmap podcastPicture = BitmapFactory.decodeResource(_context.getResources(),
                    R.raw.nopodcast);
            _playerView.setDefaultArtwork(podcastPicture);
            return;
        }

        PodcastTable pt = PodcastDatabaseHelper.getInstance().getPodcastRow(episode.getPid());
        String url = pt.getLogoUrl();
        if (url.equals("")) {
            Bitmap podcastPicture = BitmapFactory.decodeResource(_context.getResources(),
                    R.raw.missing_podcast_image);
            _playerView.setDefaultArtwork(podcastPicture);
            return;
        }

        try {
            url = new URL(url).toString();
        } catch (Exception e) {
            Bitmap podcastPicture = BitmapFactory.decodeResource(_context.getResources(),
                    R.raw.missing_podcast_image);
            _playerView.setDefaultArtwork(podcastPicture);
            return;
        }

        ImageRequest ir = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        _playerView.setDefaultArtwork(response);;
                    }
                },
                500,
                500,
                Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        Bitmap podcastPicture = BitmapFactory.decodeResource(_context.getResources(),
                                R.raw.missing_podcast_image);
                        _playerView.setDefaultArtwork(podcastPicture);
                    }
                }
        );

        VolleyQueue.getInstance().getRequestQueue().add(ir);
    }

    //*******************************************
    //* Catch media buttons
    //*******************************************

    public void setMediaReceiver() {
        ComponentName mediaButtonReceiver = new ComponentName(_context, RemoteControlReceiver.class);

        _mediaSession = new MediaSessionCompat(_context, "jc.podcast.player",
                mediaButtonReceiver, null);

        _mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
            MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        _mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
                Bundle extras = mediaButtonEvent.getExtras();
                java.util.Set keys = extras.keySet();
                Iterator itr = keys.iterator();
                while (itr.hasNext()) {
                    Object curr = itr.next();
                    Object val = extras.get(curr.toString());
                    processMediaKey((KeyEvent) val);
                }
                _eventBus.post(new AnyKeyEvent());
                return super.onMediaButtonEvent(mediaButtonEvent);
            }
        });
        _mediaSession.setActive(true);
    }

    private void processMediaKey(KeyEvent k) {
        int code = k.getKeyCode();
        switch(code) {
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            case KeyEvent.KEYCODE_MEDIA_PLAY:
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                flipPlayerState();
                break;
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
            case KeyEvent.KEYCODE_MEDIA_NEXT:
            case KeyEvent.KEYCODE_MEDIA_SKIP_FORWARD:
            case KeyEvent.KEYCODE_MEDIA_STEP_FORWARD:
                forwardPlayer();
                break;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
            case KeyEvent.KEYCODE_MEDIA_REWIND:
            case KeyEvent.KEYCODE_MEDIA_SKIP_BACKWARD:
            case KeyEvent.KEYCODE_MEDIA_STEP_BACKWARD:
                rewindPlayer();
                break;
        }

    }

    private boolean requestAudioFocus() {
        try {
            _audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            Integer result = _audioManager.requestAudioFocus(null, STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            Integer r = AudioManager.AUDIOFOCUS_REQUEST_GRANTED;

            if (result.intValue() == r.intValue()) {
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }




}
