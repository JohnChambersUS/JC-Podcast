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

    @Query("SELECT COUNT(*) FROM EpisodeTable WHERE pid = :pid and eid = :eid")
    int getPodcastEpisodeCount(String pid, String eid);

    @Update
    void updateEpisodeTableRow(EpisodeTable episodeTableRow);

    @Delete
    void deleteEpisodeTableRow(EpisodeTable episodeTableRow);

}
