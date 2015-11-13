package com.antym.popularmovies2;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by matthewmcgivney on 9/20/15.
 */
public class FavoriteContract {
    //TODO need to update project name to popularmoviesstage2

    //Content authority is used in the URI
    public static final String CONTENT_AUTHORITY = "com.antym.popularmoviesstage2";

    //Scheme ("content://") and content authority make up the first part of the URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Possible paths
    public static final String PATH_FAVORITES = "favorites";

    /*Define the contents of the favorite table in an inner class*/
    public static final class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                                                .appendPath(PATH_FAVORITES).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                                                    CONTENT_AUTHORITY + "/" + PATH_FAVORITES;

        public static final String CONTENT_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                                                    CONTENT_AUTHORITY + "/" + PATH_FAVORITES;
        //Database table name
        public static final String TABLE_NAME = "favorites";

        //The movie's ID from The Movie Database API
        public static final String COLUMN_MOVIE_ID = "movie_id";

    }

}
