package us.johnchambers.podcast.screens.fragments.search;

/**
 * Created by johnchambers on 7/15/17.
 */

import android.graphics.Bitmap;

public class SearchRow {

    private Bitmap image = null;
    private String title = null;
    private String feedUrl = null;

    public SearchRow() {
    }

    public SearchRow setImage(Bitmap i) {
        image = i;
        return this;
    }

    public SearchRow setTitle(String t) {
        title = t;
        return this;
    }

    public SearchRow setFeedUrl(String u) {
        feedUrl = u;
        return this;
    }

    public Bitmap getImage() {
        return this.image;
    }

    public String getTitle() {
        return this.title;
    }

    public String getFeedUrl() {
        return this.feedUrl;
    }

}
