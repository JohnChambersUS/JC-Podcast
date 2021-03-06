package us.johnchambers.podcast.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by johnchambers on 8/3/17.
 */

@Database(entities = {PodcastTable.class, EpisodeTable.class,
        NowPlayingTable.class, LatestPlaylistTable.class,
        OptionsTable.class, PlaylistTable.class, TagTable.class,
        PodcastTagTable.class}, version = 20, exportSchema = false)
public abstract class PodcastDatabase extends RoomDatabase {
    public abstract PodcastDao dao();
}
