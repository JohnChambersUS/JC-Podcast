package us.johnchambers.podcast.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.system.ErrnoException;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by johnchambers on 8/26/17.
 */
@Entity(indices = {@Index(value = {"eid"}, unique = true)},
        foreignKeys = @ForeignKey(entity = EpisodeTable.class,
                parentColumns = "eid",
                childColumns = "eid", onDelete = CASCADE))
public class DownloadQueueTable {

    @PrimaryKey(autoGenerate = true)
    private int identity; //identity column
    String eid;
    long downloadReference;

    public long getDownloadReference() {
        return downloadReference;
    }

    public void setDownloadReference(long downloadReference) {
        this.downloadReference = downloadReference;
    }

    public int getIdentity() {
        return identity;
    }

    public void setIdentity(int identity) {
        this.identity = identity;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public boolean downloading() {
        if (downloadReference != 0) {
            return true;
        }
        return false;
    }



}
