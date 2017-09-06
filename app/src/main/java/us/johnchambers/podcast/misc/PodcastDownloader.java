package us.johnchambers.podcast.misc;


import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import java.util.List;

import us.johnchambers.podcast.database.DownloadQueueTable;
import us.johnchambers.podcast.database.EpisodeTable;
import us.johnchambers.podcast.database.PodcastDatabaseHelper;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by johnchambers on 8/27/17.
 */

public class PodcastDownloader {

    private static PodcastDownloader _instance = null;
    private static Context _context;
    private static int _maxParallelDownloads = 1;
    private static DownloadManager _downloadManager;

    private PodcastDownloader() {}

    public static synchronized PodcastDownloader getInstance(Context context) {


        if (_instance == null) {
            _instance = new PodcastDownloader();
        }
        _context = context;
        _downloadManager = (DownloadManager)_context.getSystemService(DOWNLOAD_SERVICE);
        return _instance;
    }

    public static synchronized PodcastDownloader getInstance() {
        return _instance;
    }


    public void wake() {
        cleanupDownloadQueue();
        if (!(PodcastDatabaseHelper.getInstance().getDownloadInProgressCount() < _maxParallelDownloads )) {
            return; //dump out if no download shots available
        }

        DownloadQueueTable downloadCandidate = PodcastDatabaseHelper.getInstance()
                .getNextDownloadCandidate();

        if (downloadCandidate == null) {
            return; //no candidates in queue so exit out
        }

        initiateNextDownload(downloadCandidate);

    }

    private void initiateNextDownload(DownloadQueueTable downloadCandidate) {
        EpisodeTable et = PodcastDatabaseHelper.getInstance().getEpisodeTableRowByEpisodeId(downloadCandidate.getEid());

        String downloadLink = et.getAudioUrl();
        String audioType = downloadLink.replaceAll(".*\\.", "").trim();

        String saveFileName = et.getPid() + "." + et.getEid() + "." + audioType;
        //Context context  = MyFileManager.getInstance().getContext();

        Uri link = Uri.parse(downloadLink);

        long downloadReference;

        //DownloadManager downloadManager = (DownloadManager)_context.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(link);

        request.setTitle(et.getTitle());
        request.setDescription(et.getEid());

        request.setDestinationInExternalFilesDir(_context,
                "podcastAudio",
                saveFileName);

        //start download
        downloadReference = _downloadManager.enqueue(request);
        //add reference to download queue so we know it is in progress
        downloadCandidate.setDownloadReference(downloadReference);
        PodcastDatabaseHelper.getInstance().setDownloadReference(et.getEid(), downloadReference);
    }

    public void cleanupDownloadQueue() {
        List<DownloadQueueTable> downloadsInProgress = PodcastDatabaseHelper.getInstance().getAllDownloadsInProgress();
        //DownloadManager downloadManager = (DownloadManager)_context.getSystemService(DOWNLOAD_SERVICE);
        for (DownloadQueueTable download : downloadsInProgress) {
            DownloadManager.Query q = new DownloadManager.Query();
            q.setFilterById(download.getDownloadReference());
            Cursor cursor = _downloadManager.query(q);
            cursor.moveToFirst();
            int status = -1;
            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            try {
                status = cursor.getInt(columnIndex);
            }
            catch (Exception e) {
                String x = "one";
            }

            if (status == DownloadManager.STATUS_FAILED) {
                PodcastDatabaseHelper.getInstance().deleteDownloadQueueTableByEid(download.getEid());
                PodcastDatabaseHelper.getInstance().setEpisodeLocalDownloadUrl(download.getEid(), null);
            }
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                PodcastDatabaseHelper.getInstance().deleteDownloadQueueTableByEid(download.getEid());
                String luri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                PodcastDatabaseHelper.getInstance().setEpisodeLocalDownloadUrl(download.getEid(), luri);
            }

        }
    }





}
