package us.johnchambers.podcast.Events.player;

/**
 * Created by johnchambers on 2/24/18.
 */

public class TimeUpdateEvent {

    private long _millis;

    public TimeUpdateEvent(long millis) {
        _millis = millis;
    }

    public long getMillis() {
        return _millis;
    }

}
