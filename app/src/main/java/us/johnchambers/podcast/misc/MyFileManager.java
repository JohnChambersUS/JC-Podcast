package us.johnchambers.podcast.misc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import us.johnchambers.podcast.R;

/**
 * Created by johnchambers on 7/25/17.
 */

public class MyFileManager {

    private static MyFileManager _instance = null;
    private static Context _context;

    private static File _appDir;

    private static final String PODCAST_IMAGES_DIR = "podcastImages";
    private static final String PODCAST_AUDIO_DIR = "podcastAudio";

    private MyFileManager() {}

    public static synchronized MyFileManager getInstance(Context context) {
        _context = context;
        if (_instance == null) {
            _instance = new MyFileManager();
        }
        return _instance;
    }

    public static synchronized MyFileManager getInstance() {
        return _instance;
    }

    public void makeStorageDirectories() {
        _appDir = _context.getApplicationContext().getFilesDir(); //inits this value;
        File subDir = new File(_appDir, PODCAST_IMAGES_DIR);
        if( !subDir.exists()) { subDir.mkdir(); }
        subDir = new File(_appDir, PODCAST_AUDIO_DIR);
        if( !subDir.exists()) { subDir.mkdir(); }

    }

    public void addPodcastImage(Bitmap image, String podcastId) {
        File subDir = new File(_appDir, PODCAST_IMAGES_DIR);
        File outputPathWithName = new File(subDir, podcastId + ".png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outputPathWithName);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        }
        catch (Exception e) {
            String ex = e.toString();
        }
    }

    public Bitmap getPodcastImage(String pid) {
        File subDir = new File(_appDir, PODCAST_IMAGES_DIR);
        Bitmap bitmap;
        try {
            File f = new File(subDir, pid + ".png");
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (Exception e) {
            bitmap = BitmapFactory.decodeResource(_context.getResources(),
                    R.raw.missing_podcast_image);
        }
        if (bitmap == null) {
            return BitmapFactory.decodeResource(_context.getResources(),
                    R.raw.missing_podcast_image);
        }
        else {
            return bitmap;
        }
    }

    public String getPodcastDownloadDir() {
        return _appDir + "/" + PODCAST_AUDIO_DIR;
    }

    public Context getContext() {
        return _context;
    }

} //END OF MY FILE MANAGER
