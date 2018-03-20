package us.johnchambers.podcast.screens.fragments.search;

/**
 * Created by johnchambers on 7/15/17.
 */

import org.json.JSONArray;
import org.json.JSONObject;

public class ITunesCatalogResponse {

    private JSONObject _jsonObj;
    private JSONArray _results;
    private JSONObject _currResult;
    private int _resultCount = 0;
    private int _currRecord = -1;

    public Boolean ParseFail = false;

    public ITunesCatalogResponse(String rawJson) {
        convertRawToJson(rawJson);
        initVars();
    }

    private void convertRawToJson(String rawJson) {
        try {
            _jsonObj = new JSONObject(rawJson);
        }
        catch(Exception e) {
            ParseFail = true;
        }
    }

    private void initVars() {
        try {
            _resultCount = _jsonObj.getInt("resultCount");
            _results = _jsonObj.getJSONArray("results");
        }
        catch(Exception e) {

        }
    }

    public boolean moreRecords() {
        if (_resultCount == 0)
            return false;

        if (_currRecord < (_resultCount - 1))
            return true;
        else
            return false;
    }

    public void next() {
        _currRecord++;

        try {
            _currResult = _results.getJSONObject(_currRecord);
        }
        catch (Exception e) {
            _currResult = null;
        }
    }

    public String getTitle() {
        try {
            return _currResult.getString("collectionName");
        }
        catch(Exception e) {
            return "dummy";
        }
    }

    public String getArtworkUrl() {
        try {
            return _currResult.getString("artworkUrl100");
        }
        catch(Exception e) {
            return "https://3.bp.blogspot.com/-C8oyq3HIArQ/WVqg3pUO1qI/AAAAAAAAAPo/GqsfU03IgiY9VvgwKo-LBv4xFcnc-L-wACLcBGAs/s1600/JC.png";
        }
    }

    public String getFeedUrl() {
        try {
            return _currResult.getString("feedUrl");
        }
        catch(Exception e) {
            return "dummy";
        }
    }

    public int getresultCount() {
        return _resultCount;
    }

}
