package us.johnchambers.podcast.database;

import android.arch.persistence.room.Dao;
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

    public List<EpisodeTable> getEpisodesSortedNewest(String podcastId) {
        return _database.dao().getEpisodeTableRowByPodcastIdNewestFirst(podcastId);
    }

    public EpisodeTable getEpisodeTableRowByEpisodeId(String eid) {
        List<EpisodeTable> rows = _database.dao().getEpisodeTableRowByEpisodeId(eid);
        if (rows.size() > 0) {
            return rows.get(0);
        }
        else {
            return null;
        }
    }

    public String getEpisodeAudioUrl(String eid) {
        return _database.dao().getEpisodeAudioUrl(eid);
    }

    public void setEpisodeLocalDownloadUrl(String eid, String localUrl) {
        _database.dao().updateEpisodeLocalUrl(eid, localUrl);
    }

    //***********************************
    //* Download Queue Table methods
    //***********************************

    public void insertDownloadQueueTableRow(DownloadQueueTable downloadQueueTableRow) {
        _database.dao().insertDownloadQueueTableRow(downloadQueueTableRow);
    }

    public boolean isEpisodeInDownloadQueue(String eid) {
        List<DownloadQueueTable> rows = _database.dao().getDownLoadQueueRowsByEpisodeId(eid);
        if (rows.size() > 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public DownloadQueueTable getNextDownloadCandidate() {
        return _database.dao().getNextDownloadCandidate();
    }

    public void setDownloadReference(String eid, long ref) {
        List<DownloadQueueTable> rows = _database.dao().getDownLoadQueueRowsByEpisodeId(eid);
        if (rows.size() > 0) {
            DownloadQueueTable row = rows.get(0);
            row.setDownloadReference(ref);
            _database.dao().updateDownloadQueueTableRow(row);
        }
    }

    public int getDownloadInProgressCount() {
        return _database.dao().getAllDownloadsInProgress().size();
    }

    public List<DownloadQueueTable> getAllDownloadsInProgress() {
        return _database.dao().getAllDownloadsInProgress();
    }

    public void deleteDownloadQueueTableByEid(String eid) {
        List<DownloadQueueTable> rows = _database.dao().getDownLoadQueueRowsByEpisodeId(eid);
        if (rows.size() > 0) {
            _database.dao().deleteDownloadQueueTableRow(rows.get(0));
        }
    }

}
