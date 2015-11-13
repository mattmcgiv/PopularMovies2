package com.antym.popularmovies2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by matthewmcgivney on 10/17/15.
 */
public class FavoriteController {
    private Context context;

    FavoriteController(Context context) {
        this.context = context;
    }

    boolean isFavorite(String movieId) {

        //get ref to readable db
        SQLiteDatabase db = this.getReadableDb();

        //build query
        String table = FavoriteContract.FavoriteEntry.TABLE_NAME;
        String[] column = {FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID};
        String selection = FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID + " = " + movieId;
        String[] selectionArgs = {};
        String groupBy = null;
        String having = null;
        String orderBy = null;

        //query
        Cursor resultCursor = db.query(table, column, selection, selectionArgs, groupBy, having, orderBy);

        //check cursor for movie id
        while (resultCursor.moveToNext()) {
            return true;
        }

        //if the cursor was empty, return false
        return false;
    }

    //returns a list of movie ids that have been
    //marked as favorites by the user
    ArrayList<String> getAllFavorites() {
        ArrayList<String> favorites = new ArrayList();

        //get ref to readable db
        SQLiteDatabase db = this.getReadableDb();

        //Build query for all movie_ids
        String table = FavoriteContract.FavoriteEntry.TABLE_NAME;
        String[] column = {FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID};
        String selection = null;
        String[] selectionArgs = {};
        String groupBy = null;
        String having = null;
        String orderBy = null;

        //Query and get back all rows
        Cursor resultCursor = db.query(table,
                column, selection, selectionArgs, groupBy, having, orderBy);

        while (resultCursor.moveToNext()) {
            favorites.add(resultCursor.getString(0));
        }

        return favorites;
    }

    void setFavorite(String movieId) {

        //Get a reference to a writable db
        SQLiteDatabase db = getWritableDb();

        //Create ContentValues to insert
        ContentValues vals = new ContentValues();
        vals.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID, movieId);

        //Insert ContentValues, get a row id back
        long favoriteRowId;
        favoriteRowId = db.insert(FavoriteContract.FavoriteEntry.TABLE_NAME, null, vals);

        //Verify that the row id isn't -1
        if (favoriteRowId == -1) {
            throw new SQLException("Inserting the favorite into db failed.");
        }

    }

    void unSetFavorite(String movieId) {
        //Get a reference to a writable db
        SQLiteDatabase db = getWritableDb();

        //table
        String table = FavoriteContract.FavoriteEntry.TABLE_NAME;
        //column
        String column = FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID;
        //where clause
        String whereClause = column + "=" + movieId;
        //where args
        String[] whereArgs = null;

        int result = db.delete(table, whereClause, whereArgs);

        if (result != 1) {
            throw new SQLException("Removing favorite with id "
                    + movieId
                    + " from db failed");
        }
    }

    SQLiteDatabase getWritableDb() {
        //Get reference to writable database
        FavoriteDbHelper dbHelper = new FavoriteDbHelper(this.context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db;
    }

    SQLiteDatabase getReadableDb() {
        //set up db
        FavoriteDbHelper dbHelper = new FavoriteDbHelper(this.context);
        //query and get cursor
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db;
    }


}
