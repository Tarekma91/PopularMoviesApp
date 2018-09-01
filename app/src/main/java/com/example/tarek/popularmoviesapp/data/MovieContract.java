/*

Copyright 2018 tarekmabdallah91@gmail.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

 */
package com.example.tarek.popularmoviesapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import com.example.tarek.popularmoviesapp.utils.MovieDateUtils;
import com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils;

import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.MORE_THAN;

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.tarek.popularmoviesapp.data";
    public static final String MOVIES_PATH = "Movies";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, MOVIES_PATH);

        public static final String MOVIES_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + MOVIES_PATH;
        public static final String MOVIES_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + MOVIES_PATH;

        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_ID = _ID;
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_MOVIE_ID = "movieID";
        public static final String COLUMN_POSTER_PATH = "posterPath";
        public static final String COLUMN_ADULT = "adult";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_ORIGINAL_TITLE = "originalTitle";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "originalLanguage";
        public static final String COLUMN_BACKDROP_PATH = "backdropPath";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_COUNT = "voteCount";
        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";
        public static final String COLUMN_VIDEO = "video";
        public static final String COLUMN_FAVOURITE = MoviesConstantsUtils.FAVOURITE;
        // to make it easy to set fetched data in the the right list of popular , upcoming, topRated, nowPlaying
        public static final String COLUMN_MOVIES_POPULAR = MoviesConstantsUtils.POPULAR;
        public static final String COLUMN_MOVIES_UPCOMING = MoviesConstantsUtils.UPCOMING;
        public static final String COLUMN_MOVIES_TOP_RATED = MoviesConstantsUtils.TOP_RATED;
        public static final String COLUMN_MOVIES_NOW_PLAYING = MoviesConstantsUtils.NOW_PLAYING;

        /**
         * Returns just the selection part of the weather query from a normalized today value.
         * This is used to get a weather forecast from today's date. To make this easy to use
         * in compound selection, we embed today's date as an argument in the query.
         *
         * @return The selection part of the weather query for today onwards
         */
        public static String getSqlSelectForTodayOnwards() {
            long normalizedUtcNow = MovieDateUtils.normalizeDate(System.currentTimeMillis());
            return MovieEntry.COLUMN_DATE + MORE_THAN + normalizedUtcNow;
        }
    }
}
