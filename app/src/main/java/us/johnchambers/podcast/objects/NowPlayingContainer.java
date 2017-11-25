package us.johnchambers.podcast.objects;

import com.google.android.exoplayer2.source.MediaSource;

/**
 * Created by johnchambers on 11/12/17.
 */

public class NowPlayingContainer {

    private String _url;
    private String _dbLookup;
    private int _playBackPoint = 0;
    private MediaSource _mediaSource = null;
    private String _mediaSourceType;

    public NowPlayingContainer(String url, String dbLookup) {
        _url = url;
        _dbLookup = dbLookup;

    }

    public String getUrl() {
        return _url;
    }

    public void setUrl(String url) {
        _url = url;
    }

    public String getDbLookup() {
        return _dbLookup;
    }

    public void setDbLookup(String dbLookup) {
        _dbLookup = dbLookup;
    }

    public void setPlayBackPoint(int playBackPoint) {
        _playBackPoint = playBackPoint;
    }

    public int getPlayBackPoint() {
        return _playBackPoint;
    }

    public MediaSource getMediaSource() {
        //todo create source

        return _mediaSource;
    }

    public String getMediaSourceType() {
        //todo set type

        return _mediaSourceType;
    }






}
