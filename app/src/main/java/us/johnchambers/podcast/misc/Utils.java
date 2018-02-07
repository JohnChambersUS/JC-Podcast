package us.johnchambers.podcast.misc;

/**
 * Created by johnchambers on 2/5/18.
 */

public class Utils {

    private Utils() {}

    public static String safeNull(String value) {
        if (value == null) {
            return "";
        }
        else {
            return value;
        }
    }
}
