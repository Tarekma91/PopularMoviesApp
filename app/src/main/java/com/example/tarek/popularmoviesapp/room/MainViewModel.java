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
package com.example.tarek.popularmoviesapp.room;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.tarek.popularmoviesapp.R;
import com.example.tarek.popularmoviesapp.room.database.AppDatabase;
import com.example.tarek.popularmoviesapp.room.database.MovieEntry;

import java.util.List;

import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.FAVOURITE;
import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils._PREFERENCES;


public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();
    private LiveData<List<MovieEntry>> data;

    public MainViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        final String name = application.getPackageName()+_PREFERENCES;
        SharedPreferences sharedPreferences = application.getSharedPreferences(name, Context.MODE_PRIVATE);
        String listType = sharedPreferences.getString(application.getString(R.string.key_list_types),application.getString(R.string.default_value_list_types));
        if (FAVOURITE.equals(listType))data = database.movieDao().getFavouredMovies();
        else data = database.movieDao().getMovies(listType);
    }

    public LiveData<List<MovieEntry>> getData() {
        return data;
    }
}
