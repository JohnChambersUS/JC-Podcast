package us.johnchambers.podcast.misc;

/**
 * Created by johnchambers on 2/4/18.
 */

public final class Constants {

    public static String PLAYER_REWIND = "Rewind";
    public static String PLAYER_STOP = "Stop";
    public static String PLAYER_PLAY = "Play";
    public static String PLAYER_PAUSE = "Pause";
    public static String PLAYER_END = "End";
    public static String PLAYER_FORWARD = "Forward";
    public static String PLAYER_PLAY_PAUSE = "Play Pause";

    public static Boolean DEBUG = true;

    public static int UPDATE_HOUR = 5;
    public static int UPDATE_MINUTE = 11;
    public static long UPDATE_INTERVAL = 24*60*60*1000; //milliseconds

    public static String PID_FLAG = "pid";
    public static String EID_FLAG = "eid";
    public static String PLAYLIST_FLAG = "playlist";

    public static int EPISODE_LIMIT = 6;

    public static String PLAYBACK_MODE_BOOK = "book";
    public static String PLAYBACK_MODE_PODCAST = "podcast";

    public static String NO_CURRENT_EPISODE = "dummy";

    public static String NO_PID_FLAG = "no_pid";
    public static String NO_EID_FLAG = "no_eid";

    public static int MINIMUM_MILLISECONDS_BETWEEN_TAPS = 1000;
    public static int MINIMUM_MILLISECONDS_BETWEEN_TAPS_SHORT = 250;

    private Constants() {}

}
