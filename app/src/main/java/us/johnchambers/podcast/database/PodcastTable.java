package us.johnchambers.podcast.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by johnchambers on 8/3/17.
 */

@Entity(indices = {@Index(value = {"pid"}, unique = true)})
public class PodcastTable {

    @PrimaryKey(autoGenerate = true)
    private int identity; //identity column
    private String pid; //podcast id
    private String name;
    @ColumnInfo(name = "feed_url")
    private String feedUrl;
    @ColumnInfo(name = "subscription_type")
    private String subscriptionType;
    @ColumnInfo(name = "download_interval")
    private int downloadInterval = 0;
    @ColumnInfo(name = "last_download_date")
    private String lastDownloadDate;

    private String mode;
    private String currentEpisode;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getCurrentEpisode() {
        return currentEpisode;
    }

    public void setCurrentEpisode(String currentEpisode) {
        this.currentEpisode = currentEpisode;
    }



    public int getIdentity() {
        return identity;
    }

    public String getPid() {
        return pid;
    }

    public String getName() {
        return name;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public PodcastMode getSubscriptionTypeAsPodcastMode() {
        return PodcastMode.valueOf(subscriptionType);
    }

    public int getDownloadInterval() {
        return downloadInterval;
    }

    public String getLastDownloadDate() {
        return lastDownloadDate;
    }

    public Date getLastDownloadDateAsDate() {
        Date returnDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yy-DDD HH:mm");
        try {
            returnDate = sdf.parse(lastDownloadDate);
        }
        catch (Exception e) {
            returnDate = new Date();
        }
        return returnDate;
    }

    public void setIdentity(int identity) {
        //this.identity = identity;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFeedUrl(String url) {
        this.feedUrl = url;
    }

    public void setSubscriptionType(String subscription_type) {
            this.subscriptionType = subscription_type.toString();
    }

    public void setSubscriptionTypeViaPodcastMode(PodcastMode subscription_type) {
        this.subscriptionType = subscription_type.toString();
    }

    public void setDownloadInterval(int download_interval) {
        this.downloadInterval = download_interval;
    }

    public void setLastDownloadDate(String last_download_date) {
        this.lastDownloadDate = last_download_date;
    }

    public void setLastDownloadDateViaDate(Date last_download_date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-DDD HH:mm");
        this.lastDownloadDate = sdf.format(last_download_date);
    }

}
