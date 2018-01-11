package us.johnchambers.podcast.misc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

import us.johnchambers.podcast.R;
import us.johnchambers.podcast.database.PodcastDatabase;
import us.johnchambers.podcast.database.PodcastDatabaseHelper;

/**
 * Created by johnchambers on 11/11/17.
 */

public class MyPlayer {

    private static MyPlayer _instance = null;
    private static Context _context = null;
    private static View _view = null;

    private SimpleExoPlayer _player;
    private static SimpleExoPlayerView _playerView;
    private static String upNextUrl;

    private Deque <String> playQueue = new LinkedList();

    private Bitmap _currPodcastPicture = null;
    private String _nextUrl = null;

    private MyPlayer() {}

    public static synchronized MyPlayer getInstance(Context context, SimpleExoPlayerView playerView) {
        if (_context == null) {
            _context = context;
        }

        _playerView = playerView;

        if (_instance == null) {
            _instance = new MyPlayer();
        }
        return _instance;
    }

    public static synchronized MyPlayer getInstance() {
        return _instance;
    }

    //todo change to play url
    public void initializePlayer() {
        if (_player == null) {
            _player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(_context),
                    new DefaultTrackSelector(), new DefaultLoadControl());
        }
        _playerView.setPlayer(_player);
        _playerView.setControllerAutoShow(false);
        _playerView.setControllerShowTimeoutMs(0);
        playNext();
    }

    private void playNext() {
        MediaSource nextItem = getNextPlaylistMediaSource();
        if (nextItem != null) {
            _player.prepare(nextItem, true, false);
            _player.setPlayWhenReady(true);
        }
    }

    private MediaSource getNextPlaylistMediaSource() {

        MediaSource mediaSource = null;

        if (!playQueue.isEmpty()) {
            _nextUrl = playQueue.poll();
        }
        if (_nextUrl != null) {
            mediaSource = buildMediaSource(Uri.parse(_nextUrl));
            String pid = PodcastDatabaseHelper.getInstance().getPodcastIdByAudioUrl(_nextUrl);
            _currPodcastPicture = MyFileManager.getInstance().getPodcastImage(pid);
        }
        if (_currPodcastPicture == null) {
            _currPodcastPicture = BitmapFactory.decodeResource(_context.getResources(),
                    R.raw.missing_podcast_image);
        }

        if ((mediaSource == null) &&  (_nextUrl == null)) {
            _currPodcastPicture = BitmapFactory.decodeResource(_context.getResources(),
                    R.raw.nopodcast);
        }

        _playerView.setDefaultArtwork(_currPodcastPicture);
        return mediaSource;
    }

    public void addToEndOfPlayQueue(String url) {
        if (url != null) {
            playQueue.offerLast(url);
        }
    }

    public void addToTopOfPlayQueue(String url) {
        if (url != null) {
            playQueue.offerFirst(url);
        }
    }

    public void clearPlayQueue() {
        playQueue.clear();
    }

    private MediaSource buildMediaSource(Uri uri) {

        String userAgent = Util.getUserAgent(_context, "jcpodcast-exoplayer-agent");
        //return new ExtractorMediaSource(uri, new DefaultHttpDataSourceFactory(userAgent),
        //        new DefaultExtractorsFactory(), null, null);
        return new ExtractorMediaSource(uri, new DefaultDataSourceFactory(_context, userAgent),
                new DefaultExtractorsFactory(), null, null);

    }

    public void stop() {
        _player.stop();
    }

}
