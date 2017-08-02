package com.amarullah87.popularmovies.utilities;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.amarullah87.popularmovies.utilities.MovieContract.MovieEntry.TABLE_NAME;

/**
 * Created by apandhis on 31/07/17.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.amarullah87.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String TABLE_NAME = "favourite";
        public static final String COL_VOTE = "voteCount";
        public static final String COL_ID = "id";
        public static final String COL_VOTEAVG = "voteAverage";
        public static final String COL_TITLE = "title";
        public static final String COL_POPULARITY = "popularity";
        public static final String COL_POSTER = "posterPath";
        public static final String COL_ORIGINALLANG = "originalLanguage";
        public static final String COL_ORIGINALTITLE = "originalTitle";
        public static final String COL_BACKDROP = "backdropPath";
        public static final String COL_ADULT = "adult";
        public static final String COL_OVERVIEW = "overview";
        public static final String COL_RELEASEDATE = "releaseDate";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getMovieIDFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
