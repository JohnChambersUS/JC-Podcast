package us.johnchambers.podcast.objects;

/**
 * Created by johnchambers on 7/16/17.
 */

import android.graphics.Bitmap;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndImage;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.impl.XmlFixerReader;

import java.io.StringReader;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeedResponseWrapper {


    private static SyndFeed _feed;
    private String _response;
    private Integer _currEpisode = -1;
    private String _podcastId;
    private Bitmap _podcastImage = null;
    private String _feedUrl;


    public FeedResponseWrapper(String response, String feedUrl) {
        _feedUrl = feedUrl;
        _response = response;
        loadFeedInfo();
    }

    public String getLogoUrl() {
        Pattern pat = Pattern.compile("http(s)?:\\/\\/.*\\.(jpg|png|svg|jpeg|gif)");
        Matcher mat = pat.matcher(_response);
        boolean f = mat.find();
        String one = mat.group(0);
        return one;
    }

    public void processEpisodesFromTop() {
        _currEpisode = -1;
    }

    public boolean anyEpisodes() {
        return !_feed.getEntries().isEmpty();
    }

    public boolean nextEpisode() {
        _currEpisode++;
        return (_currEpisode < (Integer)_feed.getEntries().size());
    }

    public String getCurrEpisodeTitle() {
        return _feed.getEntries().get(_currEpisode).getTitle();
    }

    public Date getCurrEpisodeDate() {
        Date pd = _feed.getEntries().get(_currEpisode).getPublishedDate();
        return pd;
    }

    public String getDownloadLink() {
        String u;
        try {
            u = new URL(_feed.getEntries().get(_currEpisode).getUri()).toString();
        }
        catch (Exception e) {
            u = null;
        }
        return u;
    }

    private void loadFeedInfo() {
        StringReader stringReader = new StringReader(_response);
        SyndFeedInput input = new SyndFeedInput();
        try {
            _feed = input.build(new XmlFixerReader(stringReader));
        }
        catch(Exception e) {
            int y = 1;
        }
    }

    public String getPodcastId() {
        if (_podcastId == null) {
            try {
                _podcastId = "pid" + ((Integer) _feedUrl.hashCode()).toString();
            }
            catch (Exception e) {
                Long epoch =  (new Date()).getTime();
                _podcastId = "pid" + epoch.toString();
            }
        }
        return _podcastId;
    }

    public void setPodcastImage(Bitmap image) {
        _podcastImage = image;
    }

    public Bitmap getPodcastImage() {
        return _podcastImage;
    }



}
