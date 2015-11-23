package com.antym.popularmovies2;

import java.util.ArrayList;

/**
 * Created by matthewmcgivney on 10/31/15.
 */
public class MovieTrailers {

    private String movie_id;
    private ArrayList<MovieTrailer> list;

    public MovieTrailers(/*String movie_id*/) {
        //this.movie_id = movie_id;
        this.list = new ArrayList<>();
    }

    public ArrayList<MovieTrailer> getTrailers() {
        return this.list;
    }

    public void addTrailer(MovieTrailer mt) {
        this.list.add(mt);
    }

    public int size() {
        return this.list.size();
    }
}
