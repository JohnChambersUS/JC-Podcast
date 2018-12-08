package us.johnchambers.podcast.screens.fragments.subscribe;

/**
 * Created by johnchambers on 7/18/17.
 */


public class SubscribeEpisodeRow {

    String _date;
    String _title;
    String _downloadLink;

    public SubscribeEpisodeRow() {}

    public void setDate(String date) {
        _date = date;
    }

    public String getDate() {
        return _date;
    }

    public String getDateAsString() {
        return _date.toString();
    }

    public void setTitle(String title) {
        _title = title;
    }

    public String getTitle() {
        return _title;
    }

    public void setDownloadLink(String downloadLink) {
        _downloadLink = downloadLink;
    }

    public String getDownloadLink() {
        return _downloadLink;
    }

}
