package us.johnchambers.podcast.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by johnchambers on 2/21/18.
 */

@Entity(indices = {@Index(value = {"keyname"}, unique = true)})

public class NowPlayingTable {

    @PrimaryKey
    String keyname;
    String value;

    public String getKey() {
        return keyname;
    }

    public void setKey(String key) {
        this.keyname = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
