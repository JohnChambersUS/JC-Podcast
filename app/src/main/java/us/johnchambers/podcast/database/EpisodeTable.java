package us.johnchambers.podcast.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by johnchambers on 8/5/17.
 */

@Entity(indices = {@Index(value = {"eid"}, unique = true)},
    foreignKeys = @ForeignKey(entity = PodcastTable.class,
        parentColumns = "pid",
        childColumns = "pid", onDelete = CASCADE))

public class EpisodeTable {

    @PrimaryKey(autoGenerate = true)
    private int identity; //identity column

    String pid;
    String eid;
    String title;
    String summary;
    @ColumnInfo(name = "audio_url")
    String audioUrl;
    @ColumnInfo(name = "publication_date")
    String pubDate;
    String length;
    int played;
    @ColumnInfo(name = "in_progress")
    int inProgress;
    @ColumnInfo(name = "play_point")
    String playPoint;
    String localDownloadUrl;

    public String getLocalDownloadUrl() {
        return localDownloadUrl;
    }

    public void setLocalDownloadUrl(String localDownloadUrl) {
        this.localDownloadUrl = localDownloadUrl;
    }

    public int getIdentity() {
        return identity;
    }

    public String getPid() {
        return pid;
    }

    public String getEid() {
        return eid;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public String getPubDate() {
        return pubDate;
    }

    public Date getPubDateAsDate() {
        Date returnDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-DDD HH:mm");
        try {
            returnDate = sdf.parse(pubDate);
        }
        catch (Exception e) {
            returnDate = new Date();
        }
        return returnDate;
    }

    public String getLength() {
        return length;
    }

    public int getPlayed() {
        return played;
    }

    @Ignore
    public boolean getInProgressAsBoolean() {
        if (inProgress == 1) {
            return true;
        }
        else {
            return false;
        }
    }

    public int getInProgress() {
        return inProgress;
    }

    public String getPlayPoint() {
        return playPoint;
    }

    public long getPlayPointAsLong() {
        return Long.parseLong(playPoint);
    }


    public void setIdentity(int identity) {
        //this.identity = identity;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public void setPlayed(int played) {
        this.played = played;
    }

    public void setPlayedViaBoolean(boolean played) {
        if (played) {
            this.played = 1;
        }
        else {
            this.played = 0;
        }
    }

    public void setInProgress(int ip) {
        inProgress = 0;
        if (ip == 1) {
            inProgress = 1;
        }
    }

    public void setInProgressViaBoolean(boolean inProgress) {
        //if (false) {
        //    this.inProgress = 1;
        //}
        //else {
        //    this.inProgress = 0;
        //}
    }

    public void setPlayPoint(String playPoint) {
        this.playPoint = playPoint;
    }

    @Ignore
    public void setPlayPoint(long playPoint) {
        String pp = Long.valueOf(playPoint).toString();
        this.playPoint = pp;
    }


}
