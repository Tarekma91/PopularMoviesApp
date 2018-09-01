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

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;

import com.example.tarek.popularmoviesapp.data.MovieContract.MovieEntry;
import com.example.tarek.popularmoviesapp.utils.MovieNotificationUtils;
import com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils;

public class MovieReminderTasks implements MoviesConstantsUtils {

    public static final String UPDATE_FAVOURED = "UPDATE_FAVOURED";
    public static final String START_SYNC_MOVIES_IMMEDIATELY = "START_SYNC_MOVIES_IMMEDIATELY ";
    public static final String ACTION_DISMISS_NOTIFICATION = "ACTION_DISMISS_NOTIFICATION ";
    public static final String ACTION_SYNC_UPDATING_MOVIES = "ACTION_SYNC_UPDATING_MOVIES";
    private static final String TAG = MovieReminderTasks.class.getSimpleName();

    public static void executeTask(Context context, String action, int id, int value) {
        switch (action) {
            case UPDATE_FAVOURED:
                ContentValues contentValues = new ContentValues();
                contentValues.put(MovieEntry.COLUMN_FAVOURITE, value);
                context.getContentResolver().update(ContentUris.withAppendedId(MovieEntry.CONTENT_URI, id)
                        , contentValues, MovieEntry.COLUMN_ID + EQUALS, new String[]{String.valueOf(id)});
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
