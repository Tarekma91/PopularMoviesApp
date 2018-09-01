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

import android.content.Context;

import com.example.tarek.popularmoviesapp.room.database.AppDatabase;
import com.example.tarek.popularmoviesapp.room.database.MovieEntry;
import com.example.tarek.popularmoviesapp.room.AppExecutors;
import com.example.tarek.popularmoviesapp.utils.MovieNotificationUtils;
import com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils;

public class MovieReminderTasks implements MoviesConstantsUtils {

    private static final String TAG = MovieReminderTasks.class.getSimpleName();
    public static final String UPDATE_FAVOURED = "UPDATE_FAVOURED";
    public static final String START_SYNC_MOVIES_IMMEDIATELY = "START_SYNC_MOVIES_IMMEDIATELY ";
    public static final String ACTION_DISMISS_NOTIFICATION = "ACTION_DISMISS_NOTIFICATION ";
    public static final String ACTION_SYNC_UPDATING_MOVIES = "ACTION_SYNC_UPDATING_MOVIES";

    public static void executeTask(final Context context , String action , final int rowId , final int binaryValue) {
        switch (action) {
            case UPDATE_FAVOURED:
                final AppDatabase appDatabase = AppDatabase.getInstance(context);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        MovieEntry movieEntry = appDatabase.movieDao().getMovieByRowId(rowId);
                        movieEntry.setFavoured(binaryValue);
                        appDatabase.movieDao().updateMovie(movieEntry);
                    }
                });
                break;
            case ACTION_DISMISS_NOTIFICATION:
                MovieNotificationUtils.clearAllNotifications(context);
                break;
            case ACTION_SYNC_UPDATING_MOVIES:
                MoviesSyncTask.syncMovies(context);
                break;
        }
    }

}
