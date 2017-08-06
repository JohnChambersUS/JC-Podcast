package us.johnchambers.podcast.database;

/**
 * Created by johnchambers on 8/3/17.
 */

public enum PodcastMode {
    Manual("M"),
    Book("B"),
    Podcast("P"),
    Interval("I");

   PodcastMode(String v) {
        value = v;
    }

    private String value;

    public String getValue() {
        return value;
    }
}
