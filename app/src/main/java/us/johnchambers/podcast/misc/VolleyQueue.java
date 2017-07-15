package us.johnchambers.podcast.misc;

/**
 * Created by johnchambers on 7/15/17.
 */

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyQueue {

    private static VolleyQueue _instance = null;
    RequestQueue _requestQueue = null;
    private static Context _context;

    private VolleyQueue() {};

    public static synchronized  VolleyQueue getInstance(Context context) {
        _context = context;
        return getInstance();
    }

    public static synchronized VolleyQueue getInstance() {
        if (_instance == null) {
            init();
        }
        return _instance;
    }

    private static void init() {
        _instance = new VolleyQueue();
    }

    public RequestQueue getRequestQueue() {
        if (_requestQueue == null) {
            try {
                _requestQueue = Volley.newRequestQueue(_context);
            } catch(Exception e) {
                int x = 1;
            }
        }
        return _requestQueue;
    }

}
