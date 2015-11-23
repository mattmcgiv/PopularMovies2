package com.antym.popularmovies2;

/**
 * Created by matthewmcgivney on 10/31/15.
 */
public class MovieTrailer {
    private String youtube_id;
    private String title;

    public MovieTrailer(String id, String title) {
        this.youtube_id = id;
        this.title = title;
    }

    public String get_youtube_id() {
        return this.youtube_id;
    }

    public String get_title() {
        return this.title;
    }
}
