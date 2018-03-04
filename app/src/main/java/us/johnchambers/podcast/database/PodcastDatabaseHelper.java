package us.johnchambers.podcast.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import us.johnchambers.podcast.misc.MyFileManager;
import us.johnchambers.podcast.objects.FeedResponseWrapper;


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
            initTables();
        }
        return _instance;
    }

    public static synchronized PodcastDatabaseHelper getInstance() {
        return _instance;
    }

    private static void initTables() {


        if (_database.dao().getNowPlayingTableByKeyCount(NowPlaying.EID) < 1) {
            NowPlayingTable np1 = new NowPlayingTable();
            np1.setKey(NowPlaying.EID);
            np1.setValue("");
            _database.dao().insertNowPlayingTableRow(np1);
        }
        if (_database.dao().getNowPlayingTableByKeyCount(NowPlaying.PLAYLIST) < 1) {
            NowPlayingTable np2 = new NowPlayingTable();
            np2.setKey(NowPlaying.PLAYLIST);
            np2.setValue("");
            _database.dao().insertNowPlayingTableRow(np2);
        }
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

    public PodcastTable getPodcastRow(String podcastId) {
        List<PodcastTable> ptl = _database.dao().getPodcastTableRowByPodcastId(podcastId);
        if (ptl.size() > 0) {
            return ptl.get(0);
        }
        else {
            return null;
        }
    }


    public String getPodcastFeedUrl(String podcastId) {
        List<PodcastTable> ptl = _database.dao().getPodcastTableRowByPodcastId(podcastId);
        if (ptl.size() > 0) {
            return ptl.get(0).getFeedUrl();
        }
        else {
            return "";
        }
    }

    public void deletePodcastRow(String pid) {
        _database.dao().deletePodcastRowsByPid(pid);
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

    public void updateEpisodeTableRow(EpisodeTable episodeTable) {
        _database.dao().updateEpisodeTableRow(episodeTable);
    }

    public void updateEpisodePlayPoint(String eid, String playPoint) {
        _database.dao().updateEpisodePlayPoint(eid, playPoint);
    }

    public void updateEpisodePlayPoint(String eid, long playPoint) {
        String pp = Long.valueOf(playPoint).toString();
        _database.dao().updateEpisodePlayPoint(eid, pp);
    }

    public void updateEpisodeDuration(String eid, long duration) {
        String dur = Long.valueOf(duration).toString();
        _database.dao().updateEpisodeDuration(eid, dur);
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

    public void deleteEpisodeRow(EpisodeTable episodeTable) {
        _database.dao().deleteEpisodeTableRow(episodeTable);
    }


    public void deleteEpisodeRow(String episodeId) {
        _database.dao().deleteEpisodeRowsByEid(episodeId);
    }

    public String getPodcastIdByAudioUrl(String audioUrl) {
        return _database.dao().getPodcastIdByAudioUrl(audioUrl);
    }

    public void deleteEpisodeRows(String pid) {
        _database.dao().deleteEpisodeRowsByPid(pid);
    }

    public EpisodeTable getNextMediaPodcastPlaylist(EpisodeTable et) {
        return _database.dao().getNextMediaPodcastPlaylist(et.getPid(), et.getIdentity());
    }

    //***********************************
    //* Now playing table methods
    //***********************************

    public void updateNowPlayingEpisode(String eid) {

        NowPlayingTable np = new NowPlayingTable();
        np.setKey(NowPlaying.EID);
        np.setValue(eid);
        try {
            _database.dao().updateNowPlayingTableRow(np);
        }
        catch (Exception e){

        }
    }

    public void updateNowPlayingPlaylist(String playlist) {

        NowPlayingTable np = new NowPlayingTable();
        np.setKey(NowPlaying.PLAYLIST);
        np.setValue(playlist);
        try {
            _database.dao().updateNowPlayingTableRow(np);
        }
        catch (Exception e){

        }
    }

    public String getNowPlayingEpisodeId() {
        NowPlayingTable np = _database.dao().getNowPlayingTableByKey(NowPlaying.EID);
        return np.getValue();
    }







    //***********************************
    //* Download Queue Table methods
    //***********************************
/*
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
*/

    //*******************************************************
    //* wrapper methods to add new rows
    //*******************************************************
/*
    public void addNewPodcastRow(FeedResponseWrapper feedResponseWrapper) {
        PodcastTable newRow = getNewPodcastTableRow();
        //Store image on disk
        MyFileManager.getInstance().addPodcastImage(feedResponseWrapper.getPodcastImage(),
                feedResponseWrapper.getPodcastId());
        //add items to podcast table row from wrapper
        newRow.setPid(feedResponseWrapper.getPodcastId());
        newRow.setName(feedResponseWrapper.getPodcastTitle());
        newRow.setFeedUrl(feedResponseWrapper.getFeedUrl());
        newRow.setSubscriptionTypeViaPodcastMode(PodcastMode.Manual);
        newRow.setDownloadInterval(0);
        newRow.setLastDownloadDateViaDate(new Date());
        //insert podcast table row
        insertPodcastTableRow(newRow);
    }

    public void addNewEpisodeRow(FeedResponseWrapper feedResponseWrapper) {
        EpisodeTable currEpisode = getNewEpisodeTableRow();

        currEpisode.setPid(feedResponseWrapper.getPodcastId());
        currEpisode.setEid(feedResponseWrapper.getEpisodeId());
        currEpisode.setTitle(feedResponseWrapper.getCurrEpisodeTitle());
        currEpisode.setSummary(feedResponseWrapper.getCurrEpisodeSummary());
        currEpisode.setAudioUrl(feedResponseWrapper.getEpisodeDownloadLink());
        currEpisode.setPubDate(feedResponseWrapper.getCurrEpisodeDate());
        currEpisode.setLength("1");
        currEpisode.setPlayedViaBoolean(false);
        currEpisode.setInProgressViaBoolean(false);
        currEpisode.setPlayPoint("0");
        currEpisode.setLocalDownloadUrl(null);

        insertEpisodeTableRow(currEpisode);
    }
    */

    //*************************************************
    //* Common public utility methods
    //*************************************************

    public void removeEntirePodcast(String pid) {
        try {
            String name = getPodcastRow(pid).getName();
            Toast.makeText(_context,
                    "Removing podcast: " + name ,
                    Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {}

        String npEid = getNowPlayingEpisodeId();
        String npPid = getEpisodeTableRowByEpisodeId(npEid).getPid();

        if (pid.equals(npPid)) {
            updateNowPlayingEpisode(NowPlaying.NO_EPISODE_FLAG);
            updateNowPlayingPlaylist(NowPlaying.NO_PLAYLIST_FLAG);
        }

        deleteEpisodeRows(pid);
        deletePodcastRow(pid);
    }

}
