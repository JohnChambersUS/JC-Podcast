package us.johnchambers.podcast.services.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static us.johnchambers.podcast.misc.Constants.PLAYER_FORWARD;
import static us.johnchambers.podcast.misc.Constants.PLAYER_PLAY;
import static us.johnchambers.podcast.misc.Constants.PLAYER_PAUSE;
import static us.johnchambers.podcast.misc.Constants.PLAYER_REWIND;

/**
 * Created by johnchambers on 2/4/18.
 */

public class PlayerNotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(action.equals(PLAYER_REWIND)){
            PlayerServiceController.getInstance().rewindPlayer();
        }
        if(action.equals(PLAYER_PAUSE)){
            PlayerServiceController.getInstance().pausePlayer();
        }

        if(action.equals(PLAYER_PLAY)){
            PlayerServiceController.getInstance().resumePlayer();

        }
        if(action.equals(PLAYER_FORWARD)){
            PlayerServiceController.getInstance().forwardPlayer();
        }
    }

}
