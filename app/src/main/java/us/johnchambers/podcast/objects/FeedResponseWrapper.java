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
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndImage;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.impl.XmlFixerReader;

import java.io.StringReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import us.johnchambers.podcast.misc.HashMaker;

public class FeedResponseWrapper {


    private static SyndFeed _feed;
    private String _response;
    private Integer _currEpisode = -1;
    private String _podcastId;
    private Bitmap _podcastImage = null;
    private String _feedUrl;


    public FeedResponseWrapper(String response, String feedUrl) {
        try {
            _feedUrl = new URL(feedUrl).toString();
        }
        catch (Exception e) {
            //todo put toast error here
            _feedUrl = "";
        }
        _response = response;
        loadFeedInfo();
    }

    public String getLogoUrl() {
        try {
            Pattern pat = Pattern.compile("http(s)?:\\/\\/.*\\.(jpg|png|svg|jpeg|gif)");
            Matcher mat = pat.matcher(_response);
            boolean f = mat.find();
            String one = mat.group(0);
            return one;
        } catch (Exception e) {
            return null;
        }
    }

    public String getFeedUrl() {
        return _feedUrl;
    }

    public String getPodcastTitle() {
        return _feed.getTitle();
    }

    public void processEpisodesFromTop() {
        _currEpisode = -1;
    }

    public void processEpisodesFromBottom() {
        _currEpisode = _feed.getEntries().size();
    }

    public boolean anyEpisodes() {
        return !_feed.getEntries().isEmpty();
    }

    public boolean nextEpisode() {
        _currEpisode++;
        return (_currEpisode < (Integer)_feed.getEntries().size());
    }

    public boolean prevEpisode() {
        _currEpisode--;
        return (_currEpisode > -1);
    }

    public String getCurrEpisodeTitle() {
        return _feed.getEntries().get(_currEpisode).getTitle();
    }

    public String getCurrEpisodeSummary() {
        return "dummy-summary";
    }

    public String getCurrEpisodeDate() {
        String formattedDate = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yy-DDD HH:mm");
        Date pd;
        try {
            pd = _feed.getEntries().get(_currEpisode).getPublishedDate();
        }
        catch(Exception e) {
            pd = new Date();

        }
        try {
            formattedDate = sdf.format(pd);
        }
        catch (Exception e) {
            formattedDate = "00-000 00:00";
        }

        return formattedDate;
    }

    public String getEpisodeDownloadLink() {
        String u = null;
        boolean foundIt = false;
        SyndEntry currEntry = _feed.getEntries().get(_currEpisode);

        if (!foundIt) {
            try {
                List<SyndEnclosure> enc = currEntry.getEnclosures();
                SyndEnclosure sec = enc.get(0);
                String newUrl = sec.getUrl();
                u = new URL(newUrl).toString();
                foundIt = true;
            } catch (Exception e) {

            }
        }

        if (!foundIt) {
            try {
                u = new URL(currEntry.getUri()).toString();
                foundIt = true;
            } catch (Exception e) {

            }
        }

        if (!foundIt) {
            try {
                String link = currEntry.getLink();
                u = new URL(link).toString();
                foundIt = true;
            } catch (Exception e) {

            }
        }

        if (!foundIt) {
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
        String x = "asdf";
    }

    public String getPodcastId() {
        if (_podcastId == null) {
            String hash = "";
            try {
                HashMaker hm = new HashMaker();
                hash = hm.md5(getFeedUrl());
            }
            catch (Exception e) {
                Long epoch =  (new Date()).getTime();
                hash = epoch.toString() + "_EPOCH";
            }
            _podcastId = "pid_" + hash;
        }
        return _podcastId;
    }

    public String getEpisodeId() {
        String badUrl = "";
        String episodeLink = getEpisodeDownloadLink();
        if (episodeLink == null) {
            Random r = new Random();
            episodeLink = Integer.toString(r.nextInt(100000) + 1);
            badUrl = "_BADURL";
        }
        String episodeDate = getCurrEpisodeDate().toString();
        HashMaker hm = new HashMaker();
        String hash = hm.md5(episodeLink + episodeDate);
        return "eid_" + hash + badUrl;
    }

    public void setPodcastImage(Bitmap image) {
        _podcastImage = image;
    }

    public Bitmap getPodcastImage() {
        return _podcastImage;
    }

    public HashMap<String, Boolean> getEpisodeIdHashList() {
        HashMap<String, Boolean> episodeMap = new HashMap();
        processEpisodesFromTop();
        while(nextEpisode()) {
            episodeMap.put(getEpisodeId(), true);
        }
        return episodeMap;
    }

}
