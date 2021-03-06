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
import android.content.Intent;
import android.util.Log;
import com.example.tarek.popularmoviesapp.R;
import com.example.tarek.popularmoviesapp.room.database.AppDatabase;
import com.example.tarek.popularmoviesapp.room.database.MovieEntry;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.ZERO;

public class ReminderUtilities {

    private static final String TAG = ReminderUtilities.class.getSimpleName();
    private static final int REMINDER_INTERVAL_HOURS = 12;
    private static final int REMINDER_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(REMINDER_INTERVAL_HOURS);
    private static final int SYNC_FLEX_TIME_SECONDS = REMINDER_INTERVAL_SECONDS; // INTERVAL 12 - 24 hours
    private static final String REMINDER_JOB_TAG = "notificationUpdateMovies";

    private static boolean isInitialized;

    private static void scheduleFireBaseJobDispatcherSync(Context context) {
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job updateMovies = dispatcher.newJobBuilder()
                .setService(UpdateMoviesFirebaseJobService.class)
                .setTag(REMINDER_JOB_TAG)
                .setLifetime(Lifetime.FOREVER)
                .setConstraints(Constraint.ON_UNMETERED_NETWORK)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(REMINDER_INTERVAL_SECONDS,
                        REMINDER_INTERVAL_SECONDS + SYNC_FLEX_TIME_SECONDS))
                .setReplaceCurrent(true)
                .build();
        dispatcher.schedule(updateMovies);
    }

    synchronized public static void initialize(final Context context) {
        if (isInitialized) return;

        isInitialized = true;

        scheduleFireBaseJobDispatcherSync(context);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase appDatabase = AppDatabase.getInstance(context);

                List<MovieEntry> movies = appDatabase.movieDao().getAllMoviesExceptFavoured();
                if (null == movies || ZERO >= movies.size()) {
                    startSyncMoviesImmediately(context);
                } else {
                    Log.d(TAG,context.getString(R.string.loading_data_msg) + movies.size());
                }
            }
        });
        thread.start();
    }

    public static void startSyncMoviesImmediately(Context context) {
        Log.d(TAG, context.getString(R.string.start_sync_service_immediately));
        Intent openMovieIntentService = new Intent(context, MoviesIntentService.class);
        openMovieIntentService.setAction(MovieReminderTasks.START_SYNC_MOVIES_IMMEDIATELY);
        context.startService(openMovieIntentService);
    }

}
