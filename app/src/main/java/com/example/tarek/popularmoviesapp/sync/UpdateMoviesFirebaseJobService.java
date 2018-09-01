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

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.ZERO;

public class UpdateMoviesFirebaseJobService extends JobService {

    private AsyncTask asyncTask;

    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(final JobParameters job) {
        asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Context context = UpdateMoviesFirebaseJobService.this;
                MovieReminderTasks.executeTask(context, MovieReminderTasks.ACTION_SYNC_UPDATING_MOVIES, ZERO, ZERO);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);

                jobFinished(job, false);
            }
        };

        asyncTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (asyncTask != null) asyncTask.cancel(true);
        return true;
    }
}
