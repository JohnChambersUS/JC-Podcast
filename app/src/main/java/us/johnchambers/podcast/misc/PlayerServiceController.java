package us.johnchambers.podcast.misc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.View;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import us.johnchambers.podcast.R;
import us.johnchambers.podcast.services.PlayerService;

/**
 * Created by johnchambers on 1/22/18.
 */

public class PlayerServiceController {

    private static PlayerServiceController _instance = null;
    private static PlayerService _service = null;
    private static boolean _serviceBound = false;
    private static Context _context = null;

    public PlayerServiceController() {}

    public static PlayerServiceController getInstance() {
        return _instance;
    }

    public static PlayerServiceController getInstance(Context context) {
        _context = context;
        if (_instance == null) {
            _instance = new PlayerServiceController();
        }
        startService();
        return _instance;
    }

    //**********************************
    //* common methods
    //**********************************

    public void init() {
        startService();
        //_service.initService(_context);
    }

    public void init2() {
        //_service.initService(_context);
    }

    public void attachPlayerToView(SimpleExoPlayerView playerView) {
        _service.attachPlayerToView(playerView);
    }

    public String getCurrentUrl() {
        return _service.getCurrentUrl();
    }

    public void stopService() {
       _service.shutdownService();
    }

    public void stopPlayer() {
        _service.stopPlayer();
    }

    public void playUrl(String url) {
        _service.play(url);
    }

    //*******************************************************
    //* service not started with new
    //* started with intent
    //* _service assigned value in serviceConnection method
    //********************************************************
    private static void startService() {
        if (_service == null) {
            Intent intent = new Intent(_context, PlayerService.class);
            _context.startService(intent);
            _context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    //***************************
    //* connection
    //***************************
    private static ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PlayerService.MyBinder myBinder = (PlayerService.MyBinder) iBinder;
            _service = myBinder.getService();
            _serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            _serviceBound = false;
        }
    };

}
