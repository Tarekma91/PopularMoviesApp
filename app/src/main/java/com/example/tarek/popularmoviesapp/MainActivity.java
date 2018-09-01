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

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tarek.popularmoviesapp.adapter.MovieRecyclerCursorAdapter;
import com.example.tarek.popularmoviesapp.adapter.MovieRecyclerCursorAdapter.MovieAdapterOnClickHandler;
import com.example.tarek.popularmoviesapp.data.MovieContract.MovieEntry;
import com.example.tarek.popularmoviesapp.model.Movie;
import com.example.tarek.popularmoviesapp.sync.ReminderUtilities;
import com.example.tarek.popularmoviesapp.utils.BackgroundColorUtils;
import com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils;
import com.example.tarek.popularmoviesapp.utils.ScreenSizeUtils;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity
        implements MoviesConstantsUtils, LoaderManager.LoaderCallbacks<Cursor>,
        NavigationView.OnNavigationItemSelectedListener, MovieAdapterOnClickHandler {

    private final static String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.tv_no_data)
    TextView noDataTV;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar indicator;
    @BindView(R.id.recycle_view)
    RecyclerView recyclerView;
    @BindView(R.id.btn_retrieve_data_from_db)
    Button retrieveDataBtn;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindString(R.string.no_connection)
    String noConnectionMsg;
    @BindString(R.string.no_data_msg)
    String loadingDataMsg;
    @BindString(R.string.no_data_in_db_msg)
    String noDataInDbMsg;
    @BindString(R.string.savedInstanceState_not_null)
    String savedInstanceNotNullMsg;
    @BindString(R.string.key_list_types)
    String MOVIES_KEY_LIST_PREFERENCES;
    @BindString(R.string.key_background_color_list)
    String COLORS_KEY_LIST_PREFERENCES;

    private MovieRecyclerCursorAdapter moviesAdapter;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setViewsAndInitiateVariables();

        if (null == savedInstanceState) {
            // to check firstly if the db id empty it will load data from the internet , then display them with Loader
            if (isConnected()) ReminderUtilities.initialize(this);
            else showErrorMsg(noConnectionMsg);
        } else {
            Log.d(TAG, savedInstanceNotNullMsg);
            bundle = savedInstanceState.getBundle(BUNDLE_KEY);
        }
        if (bundle != null) {
            bundle.putInt(COUNTER_KEYWORD, ZERO); //  used it while loop @onLoadFinished() to prevent still looping forever.
        }
        initiateLoaderOrRestartIfExists(bundle);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BUNDLE_KEY, bundle);
        super.onSaveInstanceState(outState);
    }

    private void setViewsAndInitiateVariables() {
        bundle = new Bundle();

        setSupportActionBar(toolbar);
        getSharedPreferenceWhenActivityStarts();
        // to change background of layout and toolbar
        BackgroundColorUtils backgroundColorUtils = new BackgroundColorUtils(this, toolbar);
        backgroundColorUtils.getSharedPreferenceColor(PreferenceManager.getDefaultSharedPreferences(this));

        setMoviesAdapter();
        setRecyclerView();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setMoviesAdapter() {
        moviesAdapter = new MovieRecyclerCursorAdapter(null, this);
        moviesAdapter.notifyDataSetChanged();
    }

    private void setRecyclerView() {
        // wiring recycle view
        ScreenSizeUtils screenSizeUtils = new ScreenSizeUtils(this);
        int spanCount = screenSizeUtils.calculateSpanCount();

        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, spanCount));
        // to set all views in the recycle view with the same width and size
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(moviesAdapter);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            openSettingsActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void openSettingsActivity() {
        Intent openSettingsActivity = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(openSettingsActivity);
    }

    private void setActionBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) actionBar.setTitle(title);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_settings) {
            openSettingsActivity();
        } else {
            switch (id) {
                case R.id.nav_now_playing_movies:
                    bundle.putString(PATH3, NOW_PLAYING);
                    break;
                case R.id.nav_upcoming_movies:
                    bundle.putString(PATH3, UPCOMING);
                    break;
                case R.id.nav_top_rated_movies:
                    bundle.putString(PATH3, TOP_RATED);
                    break;
                case R.id.nav_favoured_movies:
                    bundle.putString(PATH3, FAVOURITE);
                    break;
                case R.id.nav_popular_movies:
                default:
                    bundle.putString(PATH3, POPULAR);
            }
            resetLoaderWithNewBundle(bundle);
            setActionBarTitle(item.getTitle().toString());
        }

        drawer.closeDrawer(GravityCompat.START);
        return TRUE_VALUE;
    }

    private void showErrorMsg(String reason) {
        indicator.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        noDataTV.setVisibility(View.VISIBLE);
        noDataTV.setText(reason);
        if (noConnectionMsg.equals(reason)) retrieveDataBtn.setVisibility(View.VISIBLE);
        Log.d(TAG, reason);
    }

    private void openMovieDetails(Movie movie) {
        Intent openMovieDetails = new Intent(MainActivity.this, DetailsActivity.class);
        openMovieDetails.putExtra(MoviesConstantsUtils.MOVIE_KEY, movie);
        startActivity(openMovieDetails);
    }

    @Override
    public void onClick(Movie movie) {
        openMovieDetails(movie);
    }

    @OnClick(R.id.btn_retrieve_data_from_db)
    void onClickRetrieveDataFromDbBtn() {
        initiateLoaderOrRestartIfExists(bundle);
    }

    private void showIndicator(boolean value) {
        if (value) {
            recyclerView.setVisibility(View.GONE);
            indicator.setVisibility(View.VISIBLE);
        } else {
            indicator.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        noDataTV.setVisibility(View.GONE); // to be GONE always except when there is no data
        retrieveDataBtn.setVisibility(View.GONE); // to be GONE always except when there is no connection
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        Log.d(TAG, IS_CONNECTED + isConnected);
        return isConnected;
    }

    private String getListType() {
        if (null == bundle.get(PATH3)) {
            return POPULAR;
        } else {
            return bundle.getString(PATH3);
        }
    }

    private void ifConnectedDownLoadData() {
        if (isConnected()) {
            // to check firstly if the db is empty it will load data from the internet , then display them with Loader
            ReminderUtilities.initialize(this);
            initiateLoaderOrRestartIfExists(bundle);
        } else showErrorMsg(noConnectionMsg);
    }

    private void initiateLoaderOrRestartIfExists(Bundle bundle) {
        if (null == getLoaderManager().getLoader(MAIN_LOADER_ID)) {
            getLoaderManager().initLoader(MAIN_LOADER_ID, bundle, this);
            Log.d(TAG, getString(R.string.loader_initiated));
        } else {
            Log.d(TAG, getString(R.string.loader_restarted));
            resetLoaderWithNewBundle(bundle);
        }
    }

    private void resetLoaderWithNewBundle(Bundle bundle) {
        getLoaderManager().restartLoader(MAIN_LOADER_ID, bundle, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String listType = getListType();
        bundle.putString(PATH3, listType);
        showIndicator(TRUE_VALUE);
        return new CursorLoader(this, MovieEntry.CONTENT_URI,
                null, listType + EQUALS, new String[]{String.valueOf(ONE)}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (null == data || ZERO >= data.getCount()) {
            if (FAVOURITE.equals(bundle.get(PATH3)) || !isConnected()) {
                showIndicator(FALSE_VALUE);
                showErrorMsg(noDataInDbMsg);
            } else if (isConnected()) {
                //in case it the 1st time to run the app..
                //It already downloaded the movies but in back ground thread so it just need to reload the Loader
                ifConnectedDownLoadData();
                initiateLoaderOrRestartIfExists(bundle);
                bundle.putInt(COUNTER_KEYWORD, ZERO);
            }
        } else {
            showIndicator(FALSE_VALUE);
            moviesAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void getSharedPreferenceWhenActivityStarts() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String listType = sharedPreferences.getString(MOVIES_KEY_LIST_PREFERENCES, POPULAR);
        bundle.putString(PATH3, listType);

        String[] listTypeKeys = getResources().getStringArray(R.array.keys_list_types);
        String[] listTypeValues = getResources().getStringArray(R.array.values_list_types);
        String title = null;
        for (int i = ZERO; i < listTypeKeys.length; i++)
            if (listType.equals(listTypeValues[i])) title = listTypeKeys[i];

        setActionBarTitle(title);
    }
}
