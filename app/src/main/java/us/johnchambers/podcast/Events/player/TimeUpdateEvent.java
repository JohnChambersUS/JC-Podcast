package us.johnchambers.podcast.Events.player;

/**
 * Created by johnchambers on 2/24/18.
 */

public class TimeUpdateEvent {

    private long _currPosition;
    private long _length;

    public TimeUpdateEvent(long currPosition, long length) {
        _currPosition = currPosition;
        _length = length;
    }

    public long getCurrPosition() {
        return _currPosition;
    }

    public long getLength() {
        return _length;
    }

}
