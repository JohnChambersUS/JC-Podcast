package us.johnchambers.podcast.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
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

    @Query("SELECT * FROM EpisodeTable WHERE pid = :podcastId order by publication_date DESC")
    List<EpisodeTable> getEpisodeTableRowByPodcastIdNewestFirst(String podcastId);

    @Query("SELECT audio_url FROM EpisodeTable WHERE eid = :episodeId")
    String getEpisodeAudioUrl(String episodeId);

    @Query("SELECT COUNT(*) FROM EpisodeTable WHERE pid = :pid and eid = :eid")
    int getPodcastEpisodeCount(String pid, String eid);

    @Update
    void updateEpisodeTableRow(EpisodeTable episodeTableRow);

    @Query("update episodetable set localdownloadurl = :downloadUrl where eid = :episodeId;")
    void updateEpisodeLocalUrl(String episodeId, String downloadUrl);

    @Delete
    void deleteEpisodeTableRow(EpisodeTable episodeTableRow);

    //**********************************************
    // Download Queue Table methods
    //**********************************************

    @Insert
    void insertDownloadQueueTableRow(DownloadQueueTable downloadQueueTableRow);

    @Query("SELECT * FROM DownloadQueueTable WHERE eid = :episodeId")
    List<DownloadQueueTable> getDownLoadQueueRowsByEpisodeId(String episodeId);

    @Query("select * from downloadqueuetable where downloadreference = 0 order by identity asc limit 1;")
    DownloadQueueTable getNextDownloadCandidate();

    @Query("select * from downloadqueuetable where downloadreference > 0;")
    List<DownloadQueueTable> getAllDownloadsInProgress();


    @Update
    void updateDownloadQueueTableRow(DownloadQueueTable downloadQueueTable);

    @Delete
    void deleteDownloadQueueTableRow(DownloadQueueTable row);

}
