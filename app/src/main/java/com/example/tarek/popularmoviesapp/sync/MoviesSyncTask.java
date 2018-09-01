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
import com.example.tarek.popularmoviesapp.data.MovieContract.MovieEntry;
import com.example.tarek.popularmoviesapp.utils.MovieJsonUtils;
import com.example.tarek.popularmoviesapp.utils.MovieNotificationUtils;

import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.EQUALS;
import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.FAVOURITE;
import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.ZERO;

class MoviesSyncTask {

    private static final String TAG = MoviesSyncTask.class.getSimpleName();

    static public void syncMovies(Context context) {
        try {
            // will load all 4 types of movies's list popular, top rated, upcoming, now playing
            // this approach will load the data scheduled every 6-12 hours
            // and when the user opens the app
            // and if the user changed the listType in sharedPreferences
            String[] listTypes = context.getResources().getStringArray(R.array.values_list_types);
            boolean isMoviesDeleted = false;
            for (String listType : listTypes) {
                if (!FAVOURITE.equals(listType)) {
                    ContentValues[] contentValues = new MovieJsonUtils().fetchMoviesData(context, listType);
                    if (null != contentValues && ZERO < contentValues.length) {
                        if (!isMoviesDeleted) {
                            int deletedMovies = context.getContentResolver().delete(
                                    MovieEntry.CONTENT_URI, MovieEntry.COLUMN_FAVOURITE + EQUALS, new String[]{String.valueOf(ZERO)});
                            isMoviesDeleted = true;
                            Log.d(TAG, context.getString(R.string.deleted_movies) + deletedMovies);
                        }
                        // after deleting all movies for once time .. begin to insert again the new movies
                        int insertedRows = context.getContentResolver().bulkInsert(
                                MovieEntry.CONTENT_URI, contentValues);
                        Log.d(TAG, context.getString(R.string.count_inserted_rows_logs) + insertedRows);
                    }
                }
            }
            MovieNotificationUtils.setNotification(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
