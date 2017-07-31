package com.amarullah87.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.amarullah87.popularmovies.data.MovieContract.MovieEntry.COL_BACKDROP;
import static com.amarullah87.popularmovies.data.MovieContract.MovieEntry.COL_ID;
import static com.amarullah87.popularmovies.data.MovieContract.MovieEntry.COL_OVERVIEW;
import static com.amarullah87.popularmovies.data.MovieContract.MovieEntry.COL_POSTER;
import static com.amarullah87.popularmovies.data.MovieContract.MovieEntry.COL_RELEASEDATE;
import static com.amarullah87.popularmovies.data.MovieContract.MovieEntry.COL_TITLE;
import static com.amarullah87.popularmovies.data.MovieContract.MovieEntry.COL_VOTEAVG;
import static com.amarullah87.popularmovies.data.MovieContract.MovieEntry.TABLE_NAME;

/**
 * Created by apandhis on 31/07/17.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "popular_movies.db";
    private static final int DATABASE_VERSION = 2;


    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE_FAVORITE = "CREATE TABLE " + TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ID + " INTEGER NOT NULL, " +
                COL_VOTEAVG + " REAL NOT NULL, " +
                COL_TITLE + " TEXT NOT NULL, " +
                COL_POSTER + " TEXT NOT NULL, " +
                COL_BACKDROP + " TEXT NOT NULL, " +
                COL_OVERVIEW + " TEXT NOT NULL, " +
                COL_RELEASEDATE + " TEXT NOT NULL" +
                "); ";
        db.execSQL(CREATE_TABLE_FAVORITE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
