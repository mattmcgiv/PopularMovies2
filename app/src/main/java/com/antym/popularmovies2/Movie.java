package com.antym.popularmovies2;

import java.io.Serializable;

/**
 * Created by matthewmcgivney on 9/5/15.
 */
public class Movie implements Comparable<Movie>, Serializable {
    private String id;
    private String backdropPath;
    private String backdropUrl;
    private String originalTitle;
    private String overview;
    private String releaseDate;
    private String posterPath;
    private String posterURL;
    private double popularity;
    private double voteAverage;
    private int voteCount;

    public Movie() {
        //nothing
    }

    String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    String getBackdropPath() {
        return backdropPath;
    }

    void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    String getBackdropUrl() {
        return this.backdropUrl;
    }

//    public void setBackdropUrl (String relativePath) throws MalformedURLException {
//        String apiUrlBase = "image.tmdb.org";
//        String apiUrl0 = "t";
//        String apiUrl1 = "p";
//        String apiUrl2 = "w300";
//        String api_key = "YOUR_API_KEY";
//        String backdropPath = relativePath;
//
//        // Construct the URL for the query
//        Uri.Builder builder = new Uri.Builder();
//        builder.scheme("https")
//                .authority(apiUrlBase)
//                .appendPath(apiUrl0)
//                .appendPath(apiUrl1)
//                .appendPath(apiUrl2)
//                .appendEncodedPath(backdropPath)
//                .appendQueryParameter("api_key", api_key);
//
//        String stringUrl = builder.build().toString();
//        URL url = new URL(stringUrl);
//        this.backdropUrl = url.toString();
//    }

    String getOriginalTitle() {
        return originalTitle;
    }

    void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    String getOverview() {
        return overview;
    }

    void setOverview(String overview) {
        this.overview = overview;
    }

    String getReleaseDate() {
        return releaseDate;
    }

    void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    String getPosterPath() {
        return posterPath;
    }

    void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    double getPopularity() {
        return popularity;
    }

    void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    double getVoteAverage() {
        return voteAverage;
    }

    void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    int getVoteCount() {
        return voteCount;
    }

    void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public String getPosterURL() {
        return posterURL;
    }

    public void setPosterURL(String posterURL) {
        this.posterURL = posterURL;
    }

    @Override
    public int compareTo(Movie anotherMovie) {
        if (this.getVoteAverage() > anotherMovie.getVoteAverage()) {
            return -1;
        }
        else if (this.getVoteAverage() < anotherMovie.getVoteAverage()) {
            return 1;
        }
        else {
            return 0;
        }
    }
}
