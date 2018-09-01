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

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.tarek.popularmoviesapp.R;

import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.ID_KEYWORD;
import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.INVALID;
import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.VALUE;
import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.ZERO;


public class MoviesIntentService extends IntentService {

    private final static String TAG = MoviesIntentService.class.getSimpleName();

    public MoviesIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action;
        if (intent != null) {
            action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case MovieReminderTasks.UPDATE_FAVOURED:
                        int rowId = intent.getIntExtra(ID_KEYWORD, INVALID);
                        int isFavoured = intent.getIntExtra(VALUE, INVALID);
                        MovieReminderTasks.executeTask(this, action, rowId, isFavoured);
                        break;
                    case MovieReminderTasks.ACTION_DISMISS_NOTIFICATION:
                        MovieReminderTasks.executeTask(this, action, ZERO, ZERO);
                        break;
                    case MovieReminderTasks.START_SYNC_MOVIES_IMMEDIATELY:
                        MoviesSyncTask.syncMovies(this);
                        break;
                }
            }
        } else {
            Log.d(TAG, getString(R.string.onHandleIntent_intent_is_null));
        }

    }
}
