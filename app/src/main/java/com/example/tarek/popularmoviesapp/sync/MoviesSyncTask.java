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
package com.example.tarek.popularmoviesapp.sync;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.example.tarek.popularmoviesapp.R;
import com.example.tarek.popularmoviesapp.data.MovieContract;
import com.example.tarek.popularmoviesapp.data.MovieContract.MovieEntry;
import com.example.tarek.popularmoviesapp.model.Movie;
import com.example.tarek.popularmoviesapp.model.MoviesResponse;
import com.example.tarek.popularmoviesapp.rest.ApiClient;
import com.example.tarek.popularmoviesapp.rest.ApiInterface;
import com.example.tarek.popularmoviesapp.utils.MovieDateUtils;
import com.example.tarek.popularmoviesapp.utils.MovieNotificationUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.API_KEY_VALUE;
import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.EMPTY_STRING;
import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.FAVOURITE;
import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.NOW_PLAYING;
import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.ONE;
import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.POPULAR;
import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.TOP_RATED;
import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.UPCOMING;
import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.ZERO;

class MoviesSyncTask {

    private static final String TAG = MoviesSyncTask.class.getSimpleName();
    private static boolean isMoviesDeleted = false;


    synchronized static public void syncMovies(final Context context) {
        try {
            // will load all 4 types of movies's list popular, top rated, upcoming, now playing
            // this approach will load the data scheduled every 6-12 hours
            // and when the user opens the app
            // and if the user changed the listType in sharedPreferences
            String[] listTypes = context.getResources().getStringArray(R.array.values_list_types);
            ApiInterface apiService = ApiClient.getClientForListMovies().create(ApiInterface.class);
            Call<MoviesResponse> call;

            for (final String listType : listTypes) {
                if (!FAVOURITE.equals(listType)) {
                    switch (listType) {
                        case NOW_PLAYING:
                            call = apiService.getNowPlayingMovies(API_KEY_VALUE);
                            break;
                        case TOP_RATED:
                            call = apiService.getTopRatedMovies(API_KEY_VALUE);
                            break;
                        case UPCOMING:
                            call = apiService.getUpcomingMovies(API_KEY_VALUE);
                            break;
                        case POPULAR:
                        default:
                            call = apiService.getPopularMovies(API_KEY_VALUE); // as default case
                            break;
                    }

                    call.enqueue(new Callback<MoviesResponse>() {
                        List<Movie> movies;

                        @Override
                        public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                            movies = response.body().getResults();
                            ContentValues[] contentValues = new ContentValues[movies.size()];
                            if (ZERO < movies.size()) {
                                deleteOldMoviesForOnceTime(context);
                                long normalizedUtcStartDay = MovieDateUtils.getNormalizedUtcDateForToday();
                                long dateTimeMillis = normalizedUtcStartDay + MovieDateUtils.DAY_IN_MILLIS;
                                for (int i = ZERO; i < movies.size(); i++) {
                                    ContentValues movieRow = new ContentValues();
                                    Movie movie = movies.get(i);
                                    movieRow.put(MovieEntry.COLUMN_DATE, dateTimeMillis);
                                    movieRow.put(MovieEntry.COLUMN_TITLE, movie.getTitle());
                                    movieRow.put(MovieEntry.COLUMN_MOVIE_ID, movie.getMovieId());
                                    movieRow.put(MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
                                    movieRow.put(MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
                                    movieRow.put(MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
                                    movieRow.put(MovieEntry.COLUMN_ORIGINAL_LANGUAGE, movie.getOriginalLanguage());
                                    movieRow.put(MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
                                    movieRow.put(MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
                                    movieRow.put(MovieEntry.COLUMN_VOTE_COUNT, movie.getVoteCount());
                                    movieRow.put(MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
                                    movieRow.put(MovieEntry.COLUMN_BACKDROP_PATH, EMPTY_STRING); // may be solved later if needed
                                    movieRow.put(MovieEntry.COLUMN_ADULT, movie.isAdult()); // as placeholder may be changed later
                                    movieRow.put(listType, ONE);
                                    movieRow.put(MovieEntry.COLUMN_VIDEO, EMPTY_STRING); // can't get it here, so when user want to watch trailer it will load in Details Activity

                                    contentValues[i] = movieRow;
                                }
                                // after deleting all movies for once time .. begin to insert again the new movies
                                int insertedRows = context.getContentResolver().bulkInsert(
                                        MovieContract.MovieEntry.CONTENT_URI, contentValues);
                                Log.d(TAG, context.getString(R.string.count_inserted_rows_logs) + insertedRows);
                            }
                        }

                        @Override
                        public void onFailure(Call<MoviesResponse> call, Throwable t) {
                            // Log error here since request failed
                            Log.e(TAG, t.toString());
                        }
                    });
                }
            }
            MovieNotificationUtils.setNotification(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void deleteOldMoviesForOnceTime(Context context) {
        if (!isMoviesDeleted) {
            int deletedMovies = context.getContentResolver().delete(
                    MovieContract.MovieEntry.CONTENT_URI, null, null);
            isMoviesDeleted = true;
            Log.d(TAG, context.getString(R.string.deleted_movies) + deletedMovies);
        }
    }
}
