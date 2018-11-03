package us.johnchambers.podcast.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(indices = {@Index(value = {"pid", "tag"}, unique = true)})
public class PodcastTagTable {

    @PrimaryKey(autoGenerate = true)
    public int identity; //identity column

    @NonNull
    private String tag; //tag from tag table

    @NonNull
    private String pid; //podcast id from podcast table

    public void setTag(@NonNull String tag) {
        this.tag = tag;
    }

    @NonNull
    public String getTag() {
        return tag;
    }

    @NonNull
    public String getPid() {
        return pid;
    }

    public void setPid(@NonNull String pid) {
        this.pid = pid;
    }

}
