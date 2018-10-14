package us.johnchambers.podcast.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

//@Entity(indices = {@Index(value = {"pid"}, unique = false)})
@Entity(indices = {@Index(value = {"pid", "option"}, unique = true)})
public class OptionsTable {

    @PrimaryKey(autoGenerate = true)
    public int identity; //identity column
    private String pid; //podast id or special id like global
    private String option;
    private String setting;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }

}
