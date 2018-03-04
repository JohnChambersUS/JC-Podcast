package us.johnchambers.podcast.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by johnchambers on 8/3/17.
 */

@Database(entities = {PodcastTable.class, EpisodeTable.class,
        NowPlayingTable.class}, version = 4)
public abstract class PodcastDatabase extends RoomDatabase {
    public abstract PodcastDao dao();
}
