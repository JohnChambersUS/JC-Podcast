package us.johnchambers.podcast.services.player;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.widget.RemoteViews;
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
import us.johnchambers.podcast.misc.MyFileManager;
import us.johnchambers.podcast.misc.VolleyQueue;
import us.johnchambers.podcast.objects.GlobalOptions;
import us.johnchambers.podcast.objects.PodcastOptions;

import static android.media.AudioManager.STREAM_MUSIC;
import static android.media.session.PlaybackState.STATE_SKIPPING_TO_NEXT;
import static com.google.android.exoplayer2.Player.STATE_ENDED;
import static com.google.android.exoplayer2.Player.STATE_READY;
import static us.johnchambers.podcast.misc.Constants.*;

public class PlayerService extends Service {

    private String _notificationChannelId = "us.johnchambers.player.notification";
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
    PhoneStateListener _phoneStateListener;
    TelephonyManager _telephoneManager;

    boolean _playerPhoneState = false;

    GlobalOptions _globalOptions = new GlobalOptions();
    Bitmap _podcastPicture = null;


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
        setPhoneListener();
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

    private Notification getPlayerNotification(String button1, String button2, String button3,
                                               String title, String contextText) {



        // create channel
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(_notificationChannelId,
                    "Diffcast",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("A Different Podcast App");
            channel.enableLights(false);
            channel.enableVibration(false);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Notification.Builder notif;
        notif = new Notification.Builder(getApplicationContext());
        notif.setSmallIcon(R.mipmap.ic_launcher);
        notif.setContentTitle(title);
        notif.setContentText(contextText);
        notif.setAutoCancel(true);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notif.setChannelId(_notificationChannelId);
        }

        RemoteViews customView = new RemoteViews(getPackageName(), R.layout.controls_notification);

        notif.setContent(customView);

        //rewind button
        customView.setImageViewResource(R.id.notification_rewind_button, R.drawable.ic_rewind_dark);
        Intent yesReceiveRewind = new Intent(this,
                PlayerNotificationBroadcastReceiver.class);
        yesReceiveRewind.setAction(PLAYER_REWIND);
        PendingIntent pendingIntentYesRewind = PendingIntent.getBroadcast(this, 12345,
                yesReceiveRewind, PendingIntent.FLAG_UPDATE_CURRENT);
        customView.setOnClickPendingIntent(R.id.notification_rewind_button, pendingIntentYesRewind);

        //play pause button
        if (button2.equals(PLAYER_PLAY)) { //play button showing
            customView.setImageViewResource(R.id.notification_play_pause_button, R.drawable.ic_play_dark);
            Intent yesReceivePlayPause = new Intent(this,
                    PlayerNotificationBroadcastReceiver.class);
            yesReceivePlayPause.setAction(PLAYER_PLAY);
            PendingIntent pendingIntentYesPlayPause = PendingIntent.getBroadcast(this, 12345,
                    yesReceivePlayPause, PendingIntent.FLAG_UPDATE_CURRENT);
            customView.setOnClickPendingIntent(R.id.notification_play_pause_button, pendingIntentYesPlayPause);
        } else { //pause button showing
            customView.setImageViewResource(R.id.notification_play_pause_button, R.drawable.ic_pause_dark);
            Intent yesReceivePlayPause = new Intent(this,
                    PlayerNotificationBroadcastReceiver.class);
            yesReceivePlayPause.setAction(PLAYER_PAUSE);
            PendingIntent pendingIntentYesPlayPause = PendingIntent.getBroadcast(this, 12345,
                    yesReceivePlayPause, PendingIntent.FLAG_UPDATE_CURRENT);
            customView.setOnClickPendingIntent(R.id.notification_play_pause_button, pendingIntentYesPlayPause);
        }

        //forward button
        customView.setImageViewResource(R.id.notification_forward_button, R.drawable.ic_forward_dark);
        Intent yesReceiveForward = new Intent(this,
                PlayerNotificationBroadcastReceiver.class);
        yesReceiveForward.setAction(PLAYER_FORWARD);
        PendingIntent pendingIntentYesForward = PendingIntent.getBroadcast(this, 12345,
                yesReceiveForward, PendingIntent.FLAG_UPDATE_CURRENT);
        customView.setOnClickPendingIntent(R.id.notification_forward_button, pendingIntentYesForward);

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
        setNoticationToPaused();
    }

    public void resumePlayer() {
        _player.setPlayWhenReady(true);
        setNoticationToPlaying();
    }

    public void flipPlayerState() {
        if (_player.getPlayWhenReady()) {
            _player.setPlayWhenReady(false);
        } else {
            _player.setPlayWhenReady(true);
        }
    }

    public void forwardPlayer() {
        _player.seekTo(_player.getContentPosition() + _globalOptions.getForwardMinutesAsMilliseconds());
    }

    public void rewindPlayer() {
        _player.seekTo(_player.getContentPosition() - _globalOptions.getRewindMinutesAsMilliseconds());
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

        //****** set playback speed ********
        PodcastOptions options = new PodcastOptions(episode.getPid());
        Float playbackSpeed = options.getCurrentSpeedAsFloat();
        PlaybackParameters pbParams = new PlaybackParameters(playbackSpeed, 1.0f);
        _player.setPlaybackParameters(pbParams);

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
       public void onShuffleModeEnabledChanged(boolean b) {}

        @Override
        public void onSeekProcessed() {}

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest, int num) {

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

            if (playbackState == STATE_SKIPPING_TO_NEXT) {
                int x = 1;

            }

        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {

        }

        @Override
        public void onPositionDiscontinuity(int pos) {
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }
    };

    //******************************
    //* volley section
    //******************************
    public void setDefaultImage(EpisodeTable episode) {

        //if empty episode table
        if (episode.isEmpty()) {
            Bitmap defaultPodcastPicture = BitmapFactory.decodeResource(_context.getResources(),
                    R.raw.nopodcast);
            _playerView.setDefaultArtwork(defaultPodcastPicture);
            return;
        }
        //if we have data in the episode table
        PodcastTable pt = PodcastDatabaseHelper.getInstance().getPodcastRow(episode.getPid());
        final Bitmap podcastPicture = MyFileManager.getInstance().getPodcastImage(episode.getPid());

        String url = pt.getLogoUrl();
        if (url.equals("")) { //then use stored image
            _playerView.setDefaultArtwork(podcastPicture);
            return;
        }

        try {
            url = new URL(url).toString();
        } catch (Exception e) { //if bad url use stored image
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
                    public void onErrorResponse(VolleyError e) { //if error retreiving, use stored image
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
        if (k.getAction() != KeyEvent.ACTION_DOWN) {
            return;
        }
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

    //**************************************
    //* phone listener
    //**************************************
    private void setPhoneListener() {

        _phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber){
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE: requestAudioFocus();
                        _player.setPlayWhenReady(_playerPhoneState);
                        break;
                    default: _playerPhoneState = _player.getPlayWhenReady();
                        _player.setPlayWhenReady(false);
                        break;
                }
            }

        };
        _telephoneManager =  (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        _telephoneManager.listen(_phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

} //end of service
