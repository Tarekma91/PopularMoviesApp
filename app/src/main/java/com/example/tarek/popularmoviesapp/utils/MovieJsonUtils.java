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
package com.example.tarek.popularmoviesapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.example.tarek.popularmoviesapp.R;
import com.example.tarek.popularmoviesapp.data.MovieContract.MovieEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieJsonUtils implements MoviesConstantsUtils {

    private final static String TAG = MovieJsonUtils.class.getName();

    public ContentValues[] fetchMoviesData(Context context, String listType) {

        String jsonResponse = MovieNetworkUtils.getJsonResponseForListMovies(context, listType);
        ContentValues[] moviesContentValues = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray resultsArray = jsonObject.getJSONArray(RESULTS);
            moviesContentValues = new ContentValues[resultsArray.length()];

            for (int i = ZERO; i < resultsArray.length(); i++) {
                JSONObject movieJson = resultsArray.getJSONObject(i);
                int voteCount = movieJson.getInt(VOTE_COUNT); //"vote_count": 1958,
                int id = movieJson.getInt(ID_KEYWORD); // id": 351286,
                Double voteAverage = movieJson.getDouble(VOTE_AVERAGE); // "vote_average": 6.6,
                String title = movieJson.getString(TITLE); // "title": "Jurassic World: Fallen Kingdom",
                Double popularity = movieJson.getDouble(POPULARITY); // "popularity": 219.075,
                String posterPath = movieJson.getString(POSTER_PATH_KEYWORD); // "poster_path": "/c9XxwwhPHdaImA2f1WEfEsbhaFB.jpg",
                String originalLanguage = movieJson.getString(ORIGINAL_LANGUAGE); // "original_language": "en",
                String originalTitle = movieJson.getString(ORIGINAL_TITLE); // "original_title": "Jurassic World: Fallen Kingdom",
                String backdropPath = movieJson.getString(BACKDROP_PATH); // "backdrop_path": "/gBmrsugfWpiXRh13Vo3j0WW55qD.jpg",
                boolean adult = movieJson.getBoolean(ADULT); // "adult": false,
                String overview = movieJson.getString(OVERVIEW); // "overview": "Several years after the demise of Jurassic World.."
                String releaseDate = movieJson.getString(RELEASE_DATE); //"release_date": "2018-06-06"
                String movieVideoKey = fetchMovieVideoKey(context, id);
                moviesContentValues[i] = setMovieRow(i, listType, posterPath, adult, overview, releaseDate, id,
                        originalTitle, originalLanguage, title, backdropPath,
                        popularity, voteCount, voteAverage, movieVideoKey);
            }

        } catch (JSONException e) {
            // e.printStackTrace();
        } catch (NullPointerException e) {
            // e.printStackTrace();
        }
        return moviesContentValues;
    }

    private ContentValues setMovieRow(
            int i, String listType, String posterPath, boolean adult, String overview, String releaseDate,
            int id, String originalTitle, String originalLanguage, String title, String backdropPath,
            Double popularity, int voteCount, Double voteAverage, String movieVideoKey) {

        long normalizedUtcStartDay = MovieDateUtils.getNormalizedUtcDateForToday();
        long dateTimeMillis = normalizedUtcStartDay + MovieDateUtils.DAY_IN_MILLIS * i;
        ContentValues movieRow = new ContentValues();
        movieRow.put(MovieEntry.COLUMN_DATE, dateTimeMillis);
        movieRow.put(MovieEntry.COLUMN_TITLE, title);
        movieRow.put(MovieEntry.COLUMN_MOVIE_ID, id);
        movieRow.put(MovieEntry.COLUMN_POSTER_PATH, posterPath);
        movieRow.put(MovieEntry.COLUMN_ADULT, adult);
        movieRow.put(MovieEntry.COLUMN_OVERVIEW, overview);
        movieRow.put(MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
        movieRow.put(MovieEntry.COLUMN_ORIGINAL_LANGUAGE, originalLanguage);
        movieRow.put(MovieEntry.COLUMN_ORIGINAL_TITLE, originalTitle);
        movieRow.put(MovieEntry.COLUMN_BACKDROP_PATH, backdropPath);
        movieRow.put(MovieEntry.COLUMN_POPULARITY, popularity);
        movieRow.put(MovieEntry.COLUMN_VOTE_COUNT, voteCount);
        movieRow.put(MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);
        movieRow.put(MovieEntry.COLUMN_VIDEO, movieVideoKey);
        movieRow.put(listType, String.valueOf(ONE)); // listType here is equal column name as MoviesContract

        return movieRow;
    }

    private String fetchMovieVideoKey(Context context, int movieId) {
        String jsonResponse = MovieNetworkUtils.getJsonResponseForMovie(context, movieId);
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray results = jsonObject.getJSONArray(RESULTS);
            JSONObject firstObjectInResults = results.getJSONObject(ZERO);
            return firstObjectInResults.getString(KEY_KEYWORD);
        } catch (JSONException e) {
            Log.d(TAG, context.getString(R.string.error_in_movie_id_msg) + movieId);
            // e.printStackTrace();
        } catch (NullPointerException e) {
            Log.d(TAG, context.getString(R.string.error_in_movie_id_msg) + movieId);
            // e.printStackTrace();
        }
        return EMPTY_STRING; // not null to avoid errors
    }
}
