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
package com.example.tarek.popularmoviesapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tarek.popularmoviesapp.adapter.RoomAdapter;
import com.example.tarek.popularmoviesapp.room.MainViewModel;
import com.example.tarek.popularmoviesapp.room.database.MovieEntry;
import com.example.tarek.popularmoviesapp.sync.ReminderUtilities;
import com.example.tarek.popularmoviesapp.utils.BackgroundColorUtils;
import com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils;
import com.example.tarek.popularmoviesapp.utils.ScreenSizeUtils;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity
        implements MoviesConstantsUtils, RoomAdapter.MovieClickListener {

    private final static String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.tv_no_data)
    TextView noDataTV;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar indicator;
    @BindView(R.id.recycle_view)
    RecyclerView recyclerView;
    @BindView(R.id.btn_retrieve_data_from_db)
    Button retrieveDataBtn;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindString(R.string.no_connection)
    String noConnectionMsg;
    @BindString(R.string.updating_msg)
    String updatingMsg;
    @BindString(R.string.loading_data_msg)
    String loadingDataMsg;
    @BindString(R.string.no_data_in_db_msg)
    String noDataInDbMsg;
    @BindString(R.string.savedInstanceState_not_null)
    String savedInstanceNotNullMsg;
    @BindString(R.string.key_list_types)
    String MOVIES_KEY_LIST_PREFERENCES;
    @BindString(R.string.key_background_color_list)
    String COLORS_KEY_LIST_PREFERENCES;

    private RoomAdapter roomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //  moved this block of code here to apply changes in SettingsActivity and click back button
        setViewsAndInitiateVariables();

        if (isConnected()) {
            ReminderUtilities.initialize(this);
            setupViewModel();
        } else showErrorMsg(noConnectionMsg);
    }

    private void setViewsAndInitiateVariables() {
        getSharedPreferenceWhenActivityStarts();

        BackgroundColorUtils backgroundColorUtils = new BackgroundColorUtils(this,null);
        backgroundColorUtils.getSharedPreferenceColor(PreferenceManager.getDefaultSharedPreferences(this));
        roomAdapter = new RoomAdapter(this);
        setRecyclerView();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadData();
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, REFRESH_TIME_MS);
            }
        });
    }

    private void setRecyclerView() {
        // wiring recycle view
        ScreenSizeUtils screenSizeUtils = new ScreenSizeUtils(this);
        int spanCount = screenSizeUtils.calculateSpanCount();

        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, spanCount));
        // to set all views in the recycle view with the same width and size
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(roomAdapter);
    }

    private void setupViewModel() {
        showIndicator(TRUE_VALUE);
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getData().observe(this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntry> movieEntries) {
                showIndicator(FALSE_VALUE);
                showRetrieveBtn(FALSE_VALUE);
                if (null == movieEntries || ZERO >= movieEntries.size()) {
                    showErrorMsg(noDataInDbMsg);
                } else {
                    showIndicator(FALSE_VALUE);
                    roomAdapter.setMovieEntries(movieEntries);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return TRUE_VALUE;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                openSettingsActivity();
                break;
            case R.id.action_refresh:
                reloadData();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void reloadData (){
        if (isConnected()) {
            showIndicator(FALSE_VALUE);
            showToastMsg(updatingMsg);
            ReminderUtilities.startSyncMoviesImmediately(this);
            setupViewModel();
        }else {
            showToastMsg(noConnectionMsg);
        }
    }

    private void showToastMsg (String msg){
        Toast.makeText(this, msg,Toast.LENGTH_LONG).show();
    }

    private void openSettingsActivity() {
        Intent openSettingsActivity = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(openSettingsActivity);
    }

    private void setActionBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) actionBar.setTitle(title);
    }

    private void showErrorMsg(String reason) {
        indicator.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        noDataTV.setVisibility(View.VISIBLE);
        noDataTV.setText(reason);
        if (noConnectionMsg.equals(reason)) showRetrieveBtn(TRUE_VALUE);
        else showRetrieveBtn(FALSE_VALUE);
        Log.d(TAG, reason);
    }

    private void openMovieDetails(MovieEntry movie) {
        Intent openMovieDetails = new Intent(MainActivity.this, DetailsActivity.class);
        openMovieDetails.putExtra(MoviesConstantsUtils.MOVIE_KEY, movie);
        startActivity(openMovieDetails);
    }
    @Override
    public void onMovieClickListener(MovieEntry movie) {
        openMovieDetails(movie);
    }

    @OnClick(R.id.btn_retrieve_data_from_db)
    void onClickRetrieveDataFromDbBtn(){
        setupViewModel();
    }

    private void showIndicator(boolean value) {
        if (value) {
            recyclerView.setVisibility(View.GONE);
            indicator.setVisibility(View.VISIBLE);
        } else {
            indicator.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        noDataTV.setVisibility(View.GONE); // to be GONE always except when there is no data msg
    }

    private void showRetrieveBtn (boolean value){
        if (value){
            retrieveDataBtn.setVisibility(View.VISIBLE); // to be GONE always except when there is no connection
        }else {
            retrieveDataBtn.setVisibility(View.GONE); // to be GONE always except when there is no connection
        }
    }

    private boolean isConnected (){
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        Log.d(TAG, IS_CONNECTED + isConnected);
        return isConnected;
    }

    private void getSharedPreferenceWhenActivityStarts(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String listType = sharedPreferences.getString(MOVIES_KEY_LIST_PREFERENCES, POPULAR);

        String[] listTypeKeys = getResources().getStringArray(R.array.keys_list_types);
        String[] listTypeValues = getResources().getStringArray(R.array.values_list_types);
        String title = null;
        for (int i = ZERO ; i < listTypeKeys.length ; i++)
            if (listType.equals(listTypeValues[i])) title = listTypeKeys[i];

        setActionBarTitle(title);
    }
}
