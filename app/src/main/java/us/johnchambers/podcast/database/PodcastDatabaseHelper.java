package us.johnchambers.podcast.database;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.widget.Toast;

import java.util.List;

import us.johnchambers.podcast.misc.C;
import us.johnchambers.podcast.misc.Constants;
import us.johnchambers.podcast.playlists.LatestPlaylist;
import us.johnchambers.podcast.playlists.NowPlaying;


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
                    .fallbackToDestructiveMigration()
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

    public void updatePodcastTableRow(PodcastTable row) {
        _database.dao().updatePodcastTableRow(row);
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

    public List<EpisodeTable> getEpisodesSortedOldest(String podcastId) {
        return _database.dao().getEpisodeTableRowByPodcastIdOldestFirst(podcastId);
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

    public String getNowPlayingPlaylist() {
        NowPlayingTable np = _database.dao().getNowPlayingTableByKey(NowPlaying.PLAYLIST);
        return np.getValue();
    }

    public void conditionalyClearNowPlaying(String playlistId) {
        String npPL = getNowPlayingPlaylist();
        if ((npPL == null) || (npPL.equals(playlistId))) {
            updateNowPlayingPlaylist(NowPlaying.NO_PLAYLIST_FLAG);
            updateNowPlayingEpisode(NowPlaying.NO_EPISODE_FLAG);
        }
    }

    //************************************
    //* Latest Playlist methods
    //************************************

    public List<LatestPlaylistTable> getUpdatedLatestPlaylist() {
        _database.dao().deleteAllFromLatestPlaylistTable();
        List<String> episodes = _database.dao().fillLatestPlaylistTable();
        for(String episode : episodes) {
            LatestPlaylistTable lt = new LatestPlaylistTable();
            lt.setEid(episode);
            _database.dao().insertLatestPlaylistTableRow(lt);
        }
        return getCurrentLatestPlaylist();
    }

    public List<LatestPlaylistTable> getCurrentLatestPlaylist() {
        return _database.dao().getLatestPlaylistTable();
    }

    public void removeEpisodeFromLatestTable(String episodeId) {
        _database.dao().removeEpisodeFromLatestPlaylistTable(episodeId);
    }

    public void deleteAllFromLatestTable() {
        _database.dao().deleteAllFromLatestPlaylistTable();
    }

    public void addToLatestTable(EpisodeTable episode) {
        LatestPlaylistTable lt = new LatestPlaylistTable();
        lt.setEid(episode.eid);
        _database.dao().insertLatestPlaylistTableRow(lt);
    }


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

        //remove from playlist table if required
        _database.dao().removeAnEntirePodcastFromPlaylistTable(pid);

        //********* remove latest if needed
        List<LatestPlaylistTable> latestEpisodes = getCurrentLatestPlaylist();
        for (LatestPlaylistTable latest :latestEpisodes) {
            EpisodeTable et = getEpisodeTableRowByEpisodeId(latest.getEid());
            if (et.pid.equals(pid)) {
                _database.dao().deleteLatestPlaylistTableRow(latest);
            }
        }
        EpisodeTable et = getEpisodeTableRowByEpisodeId(getNowPlayingEpisodeId());
        if (et != null) {
            if (et.pid.equals(pid)) {
                List<LatestPlaylistTable> lt = getCurrentLatestPlaylist();
                if (lt.size() > 0) {
                    updateNowPlayingEpisode(lt.get(0).getEid());
                } else {
                    updateNowPlayingEpisode(NowPlaying.NO_EPISODE_FLAG);
                    updateNowPlayingPlaylist(NowPlaying.NO_PLAYLIST_FLAG);
                }
            }
        }

        //************* remove now playing if needed *************
        String npEid = getNowPlayingEpisodeId();
        String npPid = "";
        try {
            npPid = getEpisodeTableRowByEpisodeId(npEid).getPid();
        } catch (Exception e) {
            npPid = "";
        }

        if (pid.equals(npPid)) {
            updateNowPlayingEpisode(NowPlaying.NO_EPISODE_FLAG);
            updateNowPlayingPlaylist(NowPlaying.NO_PLAYLIST_FLAG);
        }

        //********** remove options if needed
        _database.dao().removePodcastFromOptionsTable(pid);

        deleteEpisodeRows(pid);
        deletePodcastRow(pid);

    }

    //*****************************************
    //* options table methods
    //*****************************************

    public Integer getOptionsTableGlobalCount() {
        return _database.dao().getOptionsTableRowsCountByPodcastId(C.options.INSTANCE.getGLOBAL());
    }

    public List<OptionsTable> getOptionsByPodcastId(String pid) {
        return _database.dao().getOptionsTableRowsByPodcastId(pid);
    }

    public void upsertOption(OptionsTable row) {
        _database.dao().upsertOptionsRow(row);
    }

    public String getOptionValue(String pid, String option) {
        return _database.dao().getOptionsTableSetting(pid, option);
    }

    //***********************************
    //* playlist table methods
    //***********************************

    public List<PlaylistTable> getManualPlaylistEntries() {
        return _database.dao().getPlaylistTableRows(C.playlist.INSTANCE.getMANUAL_PLAYLIST());
    }

    public void upsertPlaylistRow(String playlistId, String episodeId) {
        PlaylistTable pt = new PlaylistTable();
        pt.setPlaylistName(playlistId);
        pt.setEid(episodeId);
        _database.dao().upsertPlaylistTableRow(pt);
    }

    public void removePlaylistFromPlaylistTable(String playlistName) {
        _database.dao().removePlaylistFromPlaylistTable(playlistName);
    }

    public void removeItemFromPlaylistTable(String playlistName, String episodeId) {
        _database.dao().removeItemFromPlaylistTable(playlistName, episodeId);
    }

}
