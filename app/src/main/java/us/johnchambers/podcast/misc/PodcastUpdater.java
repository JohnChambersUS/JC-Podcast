package us.johnchambers.podcast.misc;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rometools.rome.feed.atom.Feed;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import us.johnchambers.podcast.database.EpisodeTable;
import us.johnchambers.podcast.database.PodcastDatabaseHelper;
import us.johnchambers.podcast.database.PodcastTable;
import us.johnchambers.podcast.objects.FeedResponseWrapper;

/**
 * Created by johnchambers on 12/16/17.
 */

public class PodcastUpdater {

    private Context _context;
    private Stack<PodcastTable> podcastStack = new Stack();


    public PodcastUpdater(Context context) {
        _context = context;
        List<PodcastTable> podcastList = PodcastDatabaseHelper.getInstance().getAllPodcastRows();
        podcastStack.addAll(podcastList);
        updateNextItem();
    }

    private void updateNextItem() {
        if (!podcastStack.empty()) {
            makeUpdateCall(podcastStack.pop());
        }
    }

    private void makeUpdateCall(PodcastTable currPodcastTableRow)  {

        final PodcastTable pctr = currPodcastTableRow;
        StringRequest sr = new StringRequest(Request.Method.GET,
                currPodcastTableRow.getFeedUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        fillTable(response, pctr);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        Toast.makeText(_context,
                                "Volley Error: " + e.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                });
        VolleyQueue vq = VolleyQueue.getInstance();
        RequestQueue rq = vq.getRequestQueue();
        rq.add(sr);
    }

    public void fillTable(String response, PodcastTable currPodcastTableRow) {

        FeedResponseWrapper feedResponseWrapper = new FeedResponseWrapper(response,
                currPodcastTableRow.getFeedUrl());
        feedResponseWrapper.processEpisodesFromTop();
        while (feedResponseWrapper.nextEpisode()) {
            String currEpisodeId = feedResponseWrapper.getEpisodeId();
            EpisodeTable dbEpisode = PodcastDatabaseHelper.getInstance().getEpisodeTableRowByEpisodeId(currEpisodeId);
            if (dbEpisode == null) {
                PodcastDatabaseHelper.getInstance().addNewEpisodeRow(feedResponseWrapper);
            }
        }
        removeDeletedEpisodesFromDB(feedResponseWrapper);
        updateNextItem();
    }

    private void removeDeletedEpisodesFromDB(FeedResponseWrapper feedResponseWrapper) {
        List<EpisodeTable> dbEpisodes =
                PodcastDatabaseHelper.getInstance()
                        .getEpisodesSortedNewest(feedResponseWrapper.getPodcastId());

        HashMap<String, Boolean> epList = feedResponseWrapper.getEpisodeIdHash();
        for (EpisodeTable currEpisode : dbEpisodes) {
            if (!epList.containsKey(currEpisode.getEid()) ||
                    currEpisode.getEid().toUpperCase().contains("BAD")) {
                PodcastDatabaseHelper.getInstance().deleteEpisodeRow(currEpisode);
            }
        }
    }







}
