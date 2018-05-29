package us.johnchambers.podcast.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(indices = {@Index(value = {"playlistName", "eid"}, unique = true)})
public class PlaylistTable {

    @PrimaryKey(autoGenerate = true)
    public int identity; //identity column
    @NonNull
    private String playlistName;
    @NonNull
    private String eid; //episode id

    public int getIdentity() {
        return identity;
    }

    public void setIdentity(int identity) {
        this.identity = identity;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

}
