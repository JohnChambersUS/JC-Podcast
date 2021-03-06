package us.johnchambers.podcast.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by johnchambers on 8/3/17.
 */

@Dao
public interface PodcastDao {

    //***************************************
    //* podcast table methods
    //***************************************

    @Insert
    void insertPodcastTableRow(PodcastTable podcastTableRow);

    @Query("SELECT * FROM PodcastTable")
    List<PodcastTable> getAllPodcastRows();

    @Query("SELECT * FROM PodcastTable WHERE pid = :podcastId")
    List<PodcastTable> getPodcastTableRowByPodcastId(String podcastId);

    @Query("delete from podcastTable where pid = :podcastId;")
    void deletePodcastRowsByPid(String podcastId);

    @Update
    void updatePodcastTableRow(PodcastTable podcastTableRow);

    @Delete
    void deletePodcastTableRow(PodcastTable podcastTableRow);

    //*******************************************
    //* Episode table methods
    //*******************************************

    @Insert
    void insertEpisodeTableRow(EpisodeTable episodeTableRow);

    @Query("SELECT * FROM EpisodeTable")
    List<EpisodeTable> getAllEpisodeRows();

    @Query("SELECT * FROM EpisodeTable WHERE pid = :podcastId")
    List<EpisodeTable> getEpisodeTableRowByPodcastId(String podcastId);


    @Query("SELECT * FROM EpisodeTable WHERE eid = :episodeId")
    List<EpisodeTable> getEpisodeTableRowByEpisodeId(String episodeId);

    @Query("SELECT * FROM EpisodeTable WHERE pid = :podcastId order by identity DESC")
    List<EpisodeTable> getEpisodeTableRowByPodcastIdNewestFirst(String podcastId);

    @Query("SELECT * FROM EpisodeTable WHERE pid = :podcastId order by identity ASC")
    List<EpisodeTable> getEpisodeTableRowByPodcastIdOldestFirst(String podcastId);

    @Query("SELECT audio_url FROM EpisodeTable WHERE eid = :episodeId")
    String getEpisodeAudioUrl(String episodeId);

    @Query("SELECT COUNT(*) FROM EpisodeTable WHERE pid = :pid and eid = :eid")
    int getPodcastEpisodeCount(String pid, String eid);

    @Query("SELECT pid FROM EpisodeTable WHERE audio_url = :audioUrl LIMIT 1")
    String getPodcastIdByAudioUrl(String audioUrl);

    @Update
    void updateEpisodeTableRow(EpisodeTable episodeTableRow);

    @Query("update episodeTable set localdownloadurl = :downloadUrl where eid = :episodeId;")
    void updateEpisodeLocalUrl(String episodeId, String downloadUrl);

    @Query("update episodeTable set play_point = :playPoint where eid = :episodeId;")
    void updateEpisodePlayPoint(String episodeId, String playPoint);

    @Query("update episodeTable set length = :duration where eid = :episodeId;")
    void updateEpisodeDuration(String episodeId, String duration);

    @Query("delete from episodeTable where pid = :podcastId;")
    void deleteEpisodeRowsByPid(String podcastId);

    @Query("delete from episodeTable where eid = :episodeId;")
    void deleteEpisodeRowsByEid(String episodeId);

    @Query("SELECT * FROM episodetable WHERE pid = :podcastId and identity > :identity  ORDER BY identity ASC LIMIT 1;")
    EpisodeTable getNextMediaPodcastPlaylist(String podcastId, int identity);

    @Delete
    void deleteEpisodeTableRow(EpisodeTable episodeTableRow);

    //*********************************************************
    //* Now Playing Table methods
    //*********************************************************

    @Update
    void updateNowPlayingTableRow(NowPlayingTable nowPlayingTable);

    @Query("SELECT * FROM NowPlayingTable WHERE keyname = :key LIMIT 1")
    NowPlayingTable getNowPlayingTableByKey(String key);

    @Query("SELECT count(*) FROM NowPlayingTable WHERE keyname = :key")
    int getNowPlayingTableByKeyCount(String key);

    @Insert
    void insertNowPlayingTableRow(NowPlayingTable nowPlayingTable);

    //*******************************************
    //* Latest Playlist Table methods
    //*******************************************

    @Update
    void updateLatestPlaylistRow(LatestPlaylistTable latestPlaylistTable);

    @Insert
    void insertLatestPlaylistTableRow(LatestPlaylistTable latestPlaylistTable);

    @Query("SELECT * FROM LatestPlaylistTable")
    List<LatestPlaylistTable> getLatestPlaylistTable();

    @Delete
    void deleteLatestPlaylistTableRow(LatestPlaylistTable latestPlaylistTable);

    @Query("DELETE FROM LatestPlaylistTable")
    void deleteAllFromLatestPlaylistTable();

    @Query("select episodetable.eid from episodetable inner join podcasttable on podcasttable.pid = episodetable.pid where podcasttable.mode = 'podcast' group by episodetable.pid order by episodetable.identity desc")
    List<String> fillLatestPlaylistTable();

    @Query("DELETE FROM LatestPlaylistTable WHERE eid = :epidoseId")
    void removeEpisodeFromLatestPlaylistTable(String epidoseId);

    //*****************************************
    //* options table
    //*****************************************

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertOptionsRow(OptionsTable row);

    @Delete
    void deleteOptionsRow(OptionsTable row);

    @Query("Delete from optionstable where pid = :podcastId")
    void deletePodcastFromOptionsTable(String podcastId);

    @Query("Select * from optionstable where pid = :podcastId")
    List<OptionsTable> getOptionsTableRowsByPodcastId(String podcastId);

    @Query("Select count(*) from optionstable where pid = :podcastId")
    Integer getOptionsTableRowsCountByPodcastId(String podcastId);

    @Query("Select setting from optionstable where pid = :podcastId and option = :option")
    String getOptionsTableSetting(String podcastId, String option);

    @Query("DELETE FROM OptionsTable WHERE pid = :podcastId")
    void removePodcastFromOptionsTable(String podcastId);

    //****************************************
    //* playlist table routines
    //****************************************

    @Query("Select * from playlisttable where playlistName = :playlist order by identity")
    List<PlaylistTable> getPlaylistTableRows(String playlist);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertPlaylistTableRow(PlaylistTable row);

    @Query("DELETE FROM playlisttable WHERE playlistName = :playlistName")
    void removePlaylistFromPlaylistTable(String playlistName);

    @Query("delete from playlisttable where playlisttable.eid in (select playlisttable.eid from playlisttable inner join episodetable where pid = :podcastId and playlisttable.eid = episodetable.eid)")
    void removeAnEntirePodcastFromPlaylistTable(String podcastId);

    @Query("delete from playlistTable where playlistName = :playlistName and eid = :eid ")
    void removeItemFromPlaylistTable(String playlistName, String eid);

    @Query("select * from playlisttable where playlistname = :tag order by identity")
    List<PlaylistTable> getCurrentTagPlaylistEpisodes(String tag);

    @Query("select * from episodetable e where pid in (select pid from podcasttagtable where tag = :tag) order by e.publication_date DESC;")
    List<EpisodeTable> getRefreshedListOfTagPlaylistEpisodes(String tag);

    @Query("select * from episodetable e where pid in (select pid from podcasttagtable where tag = :tag) group by e.pid order by e.publication_date DESC  limit (select count(*) from podcasttagtable where tag = :tag);")
    List<EpisodeTable> getRefreshedListOfTagPlaylistEpisodesTop(String tag);

    @Query("select count(*) from playlisttable where playlistName = :playlistId")
    int getPlaylistCount(String playlistId);


    //******************************************
    //* TagTable table routines
    //******************************************

    @Delete
    void deleteTagTableRow(TagTable tagTable);

    @Insert
    void insertTagTableRow(TagTable tagTable);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertTagRow(TagTable row);

    @Query("Select * from tagtable order by tag")
    List<TagTable> getTagTableRows();

    @Query("Select * from tagtable where tag = :tag order by tag")
    List<TagTable> getTagTableRows(String tag);

    //*********************************************
    //* PodcastTagTable table routines
    //*********************************************
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertPodcastTagRow(PodcastTagTable row);

    @Delete
    void deletePodcastTagTableRow(PodcastTagTable row);

    @Query("DELETE FROM podcasttagtable WHERE pid = :pid and tag = :tag")
    void deletePodcastTagTable2(String pid, String tag);

    @Query("DELETE FROM podcasttagtable WHERE pid = :pid")
    void deletePodcastTagTableByPodcastId(String pid);

    @Query("DELETE FROM podcasttagtable WHERE tag = :tag")
    void deletePodcastTagTableByTag(String tag);


    //*******************************************
    //* mixed and joined returns
    //*******************************************

    //* join podcast & podcastTagTable

    @Query("SELECT p.pid, t.tag FROM podcasttable p LEFT JOIN podcasttagtable t ON p.pid = t.pid")
    List<PodcastTagJoinedObject> getPodcastsAndTags();

    @Query("SELECT p.pid, t.tag FROM podcasttable p LEFT JOIN (select * from podcasttagtable where tag = :tag) t ON p.pid = t.pid;")
    List<PodcastTagJoinedObject> getPodcastsAndTags(String tag);

    @Query("SELECT p.pid, t.tag FROM tagtable t LEFT JOIN (select * from podcasttagtable where pid = :pid) p ON p.tag = t.tag order by t.tag;")
    List<PodcastTagJoinedObject> getPodcastsAndTagsByPid(String pid);

}
