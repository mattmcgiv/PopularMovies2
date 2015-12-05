package com.antym.popularmovies2;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by matthewmcgivney on 9/5/15.
 */
public class MovieManager {

    protected Context context;

    public MovieManager(Context context) {
        this.context = context;
    }

    public MovieManager(ArrayList<com.antym.popularmovies2.Movie> myMovies) {
        this.myMovies = myMovies;
    }

    private static final String TAG = "PopularMovies2";

    protected ArrayList<com.antym.popularmovies2.Movie> myMovies = new ArrayList<>();

    public void addMovie(com.antym.popularmovies2.Movie movie) {
        this.myMovies.add(movie);
    }

    public ArrayList<com.antym.popularmovies2.Movie> getMovies() {
        return this.myMovies;
    }

    public int getNumMovies() {
        return this.myMovies.size();
    }

    public com.antym.popularmovies2.Movie getMovie(int position) {
        return myMovies.get(position);
    }

    public Movie getMovieById(String id) {
        for (int i = 0; i < this.myMovies.size(); i++) {
            Movie tempMovie = this.getMovie(i);
            if (tempMovie.getId().matches(id)) {
                return tempMovie;
            }
        }
        return null;
    }

    public void sortBy(String method) {
        if (method == "rating") {
            Collections.sort(this.myMovies);
        }
        else if (method == "popularity") {
            Collections.sort(this.myMovies, new Comparator<Movie>() {
                @Override
                public int compare(Movie lhs, Movie rhs) {
                    if (lhs.getPopularity() > rhs.getPopularity()) {
                        return -1;
                    }
                    else if (lhs.getPopularity() < rhs.getPopularity()) {
                        return 1;
                    }
                    else {
                        return 0;
                    }
                }
            });
        }
        else if (method == "favorites") {
            Collections.sort(this.myMovies, (new Comparator<Movie>() {
                @Override
                public int compare(Movie lhs, Movie rhs) {
                    if (isFavorite(lhs) && !isFavorite(rhs)) {
                        return -1;
                    }
                    else if (!isFavorite(lhs) && isFavorite(rhs)) {
                        return 1;
                    }
                    else {
                        return 0;
                    }
                }
            }));
        }
    }

    public void clear() {
        this.myMovies.clear();
    }

    //wrapper function for FavoriteController's isFavorite
    //used in comparison for search
    public boolean isFavorite(Movie movie) {
        FavoriteController fc = new FavoriteController(this.context);
        return fc.isFavorite(movie.getId());
    }

    public ArrayList<Movie> getFavorites() {
        FavoriteController fc = new FavoriteController(this.context);
        ArrayList<String> movieIds = fc.getAllFavorites();
        Log.d(TAG,"favorites movieIds are: " + movieIds.toString());
        ArrayList<Movie> movies = new ArrayList<Movie>();
        for (String id:movieIds) {
            movies.add(this.getMovieById(id));
        }
        //for all movies in this object
        //if it is a favorite, add it to al
        return movies;
    }

    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < this.myMovies.size(); i++) {
            Movie temp = getMovie(i);
            result += temp.getOriginalTitle() + ":" + temp.getVoteAverage() +", ";
        }
        return result;
    }


}
