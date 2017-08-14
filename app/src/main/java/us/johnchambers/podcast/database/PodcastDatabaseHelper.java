package us.johnchambers.podcast.database;

import android.arch.persistence.room.Room;
import android.content.Context;

import java.util.List;


/**
 * Created by johnchambers on 8/3/17.
 */

public class PodcastDatabaseHelper {

    private static PodcastDatabaseHelper _instance = null;
    private static Context _context;
    private static PodcastDatabase _database;

    private PodcastDatabaseHelper(){}

    public static synchronized PodcastDatabaseHelper getInstance(Context context) {
        _context = context;
        if (_instance == null) {
            _instance = new PodcastDatabaseHelper();
            _database = Room.databaseBuilder(context,
                    PodcastDatabase.class, "podcast-db")
                    .allowMainThreadQueries()
                    .build();
        }
        return _instance;
    }

    public static synchronized PodcastDatabaseHelper getInstance() {
        return _instance;
    }

    //******************************
    //* podcast table methods
    //******************************

    public List<PodcastTable> getAllPodcastRows() {
        return _database.dao().getAllPodcastRows();
    }

    public PodcastTable getNewPodcastTableRow() {
        return new PodcastTable();
    }

    public void insertPodcastTableRow(PodcastTable podcastTable) {
        _database.dao().insertPodcastTableRow(podcastTable);
    }

    public boolean alreadySubscribedToPodcast(String podcastId) {
        List<PodcastTable> ptl = _database.dao().getPodcastTableRowByPodcastId(podcastId);
        if (ptl.size() > 0) {
            return true;
        }
        else {
            return false;
        }
    }

    //********************************
    //* episode table methods
    //********************************

    public EpisodeTable getNewEpisodeTableRow() {
        return new EpisodeTable();
    }

    public void insertEpisodeTableRow(EpisodeTable episodeTable) {
        _database.dao().insertEpisodeTableRow(episodeTable);
    }

    public boolean doesEpisodeExist(String pid, String eid) {
        return false;
    }


}
