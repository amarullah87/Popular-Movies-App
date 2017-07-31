package com.amarullah87.popularmovies.data;

import android.provider.BaseColumns;

/**
 * Created by apandhis on 31/07/17.
 */

public class MovieContract {

    public static final class MovieEntry implements BaseColumns {

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
    }
}
