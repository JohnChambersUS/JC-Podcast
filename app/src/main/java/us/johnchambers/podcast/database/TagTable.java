package us.johnchambers.podcast.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(indices = {@Index(value = {"tag"}, unique = true)})
public class TagTable {

    @PrimaryKey(autoGenerate = true)
    public int identity; //identity column
    @NonNull
    private String tag; //podast id or special id like global

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

}


