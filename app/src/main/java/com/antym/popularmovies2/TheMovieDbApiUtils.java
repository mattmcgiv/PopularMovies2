package com.antym.popularmovies2;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by matthewmcgivney on 12/5/15.
 */
public class TheMovieDbApiUtils {
    public static final String TAG = "TheMovieDbApiUtils";

    public static String getPosterPath(JSONObject movieJson) throws JSONException {
        String posterPath = movieJson.getString("poster_path");
        Log.d(TAG, "getPosterPath: " + posterPath);
        return posterPath;
    }

    public static String getValue(JSONObject movieJson, String key) throws JSONException{
        String val = movieJson.getString(key);
        return val;
    }

    public static JSONObject getMovieJSON(String movieJsonString, int movieIndex) throws JSONException{
        JSONObject results = new JSONObject(movieJsonString);
        JSONArray movies = results.getJSONArray("results");
        JSONObject movie = movies.getJSONObject(movieIndex);
        return movie;
    }

    public static int getNumResults(String movieJsonString) throws JSONException {
        JSONObject results = new JSONObject(movieJsonString);
        JSONArray movies = results.getJSONArray("results");
        return movies.length();
    }

    public static String query(Uri.Builder builder) throws IOException{
        HttpURLConnection urlConnection = null;
        String apiResponseJsonStr = null;

        String stringUrl = builder.build().toString();
        Log.d(TAG, "stringURL: " + stringUrl);
        URL url = new URL(stringUrl);

        // Create the request, and open the connection
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        // Read the input stream into a String
        InputStream inputStream = urlConnection.getInputStream();
        StringBuffer buffer = new StringBuffer();
        if (inputStream == null) {
            // Nothing to do.
            apiResponseJsonStr = null;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line + "\n");
        }

        if (buffer.length() == 0) {
            // Stream was empty.  No point in parsing.
            apiResponseJsonStr = null;
        }
        apiResponseJsonStr = buffer.toString();
        return apiResponseJsonStr;
    }

}
