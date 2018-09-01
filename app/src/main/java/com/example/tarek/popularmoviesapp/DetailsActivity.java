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

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tarek.popularmoviesapp.model.MovieReviewsKey;
import com.example.tarek.popularmoviesapp.model.MovieReviewsKeyResponse;
import com.example.tarek.popularmoviesapp.model.MovieTrailerKey;
import com.example.tarek.popularmoviesapp.model.MovieTrailerKeyResponse;
import com.example.tarek.popularmoviesapp.rest.ApiClient;
import com.example.tarek.popularmoviesapp.rest.ApiInterface;
import com.example.tarek.popularmoviesapp.room.database.MovieEntry;
import com.example.tarek.popularmoviesapp.sync.MovieReminderTasks;
import com.example.tarek.popularmoviesapp.sync.MoviesIntentService;
import com.example.tarek.popularmoviesapp.utils.BackgroundColorUtils;
import com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DetailsActivity extends AppCompatActivity implements MoviesConstantsUtils {

    private static final String TAG = DetailsActivity.class.getSimpleName() ;
    @BindView(R.id.details_poster)
    ImageView posterIV;
    @BindView(R.id.details_overview)
    TextView overviewTV;
    @BindView(R.id.details_title)
    TextView titleTV;
    @BindView(R.id.details_original_title)
    TextView originalTitleTV;
    @BindView(R.id.details_original_language)
    TextView originalLanguageTv;
    @BindView(R.id.details_popularity)
    TextView popularityTV;
    @BindView(R.id.details_vote_count)
    TextView voteCountTV;
    @BindView(R.id.details_release_date)
    TextView releaseDateTV;
    @BindView(R.id.tv_item_rating_value)
    TextView ratingValueTV;
    @BindView(R.id.details_icons_share)
    ImageView iconShare;
    @BindView(R.id.details_icons_add_to_favourite_list)
    ImageView iconAddToFavouriteList;
    @BindView(R.id.details_icons_play_btn)
    ImageView iconPlayBtn;
    @BindView(R.id.details_icons_reviews)
    ImageView iconReviews;
    @BindView(R.id.details_icons_adult)
    ImageView iconIsAdult;

    @BindString(R.string.no_trailer_msg)
    String noTrailerMsg;
    @BindString(R.string.no_reviews_msg)
    String noReviewsMsg;
    @BindString(R.string.app_name)
    String appName;

    private String movieDetails; // to share by intent
    private MovieEntry movie;
    private boolean isFavouredStateChanged;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        BackgroundColorUtils backgroundColorUtils = new BackgroundColorUtils(this,null);
        backgroundColorUtils.getSharedPreferenceColor(PreferenceManager.getDefaultSharedPreferences(this));

        getComingIntentsAndSetViews();
        getMovieDetails();
    }

    private void getComingIntentsAndSetViews() {
        Intent comingIntent = getIntent();
        movie = comingIntent.getParcelableExtra(MOVIE_KEY);
    }

    private void getMovieDetails (){
        String title = movie.getTitle();
        String originalTitle = movie.getOriginalTitle();
        String originalLanguage = movie.getOriginalLanguage();
        String popularity = String.valueOf(movie.getPopularity());
        double voteAverage = movie.getVoteAverage();
        String voteCount = String.valueOf(movie.getVoteCount());
        String releaseDate = movie.getReleaseDate();
        String overView = movie.getOverview();
        String posterPath = movie.getPosterPath();
        String posterUrl = POSTERS_BESTV_URL + posterPath;
        boolean isAdult = movie.isAdult();
        int isFavoured = movie.getFavoured();

        setMovieDetails(title, originalTitle, originalLanguage, String.valueOf(voteAverage),
                voteCount, releaseDate, popularity, overView);

        Picasso.get().load(posterUrl).placeholder(R.drawable.progress_animation)
                .error(android.R.drawable.stat_notify_error)
                .into(posterIV);

        titleTV.setText(title);
        originalTitleTV.setText(originalTitle);
        ratingValueTV.setText(String.valueOf(voteAverage));
        originalLanguageTv.setText(originalLanguage);
        voteCountTV.setText(voteCount);
        releaseDateTV.setText(releaseDate);
        popularityTV.setText(popularity);
        overviewTV.setText(overView);
        if (isAdult) iconIsAdult.setVisibility(View.VISIBLE);
        else iconIsAdult.setVisibility(View.GONE);

        if (ZERO == isFavoured) {
            iconAddToFavouriteList.setTag(ZERO);
            iconAddToFavouriteList.setImageResource(R.drawable.ic_heart_empty_24);
        }else {
            iconAddToFavouriteList.setTag(ONE);
            iconAddToFavouriteList.setImageResource(R.drawable.ic_heart_red_24);
        }
    }

    @OnClick(R.id.details_icons_share)
    void onClickIconShare() {
        startActivity(shareMovieDetails());
    }

    @OnClick(R.id.details_icons_add_to_favourite_list)
    void onClickIconAddToFavoriteList(){
        int isFavoured = (int) iconAddToFavouriteList.getTag();
        changeFavouredState(isFavoured);
    }

    @OnClick(R.id.details_icons_play_btn)
    void onClickIconPlay(){
        final int MOVIE_ID = movie.getMovieId();
            ApiInterface apiService = ApiClient.getClientForMovie(MOVIE_ID).create(ApiInterface.class);
            Call<MovieTrailerKeyResponse> call ;
            call = apiService.getMovieTrailers(API_KEY_VALUE);
            call.enqueue(new Callback<MovieTrailerKeyResponse>(){
                @Override
                public void onResponse(Call<MovieTrailerKeyResponse> call, Response<MovieTrailerKeyResponse> response) {
                    try{
                        List<MovieTrailerKey> results = response.body().getResultsContainTrailers();
                        MovieTrailerKey trailer = results.get(ZERO);
                        String youtubeId = trailer.getKey();
                        openVideoTrailer(youtubeId);
                    }catch (Exception e){
                        Toast.makeText(getBaseContext(), noTrailerMsg, Toast.LENGTH_LONG).show();
                        Log.e(TAG, MOVIE_ID + COMMA + e.toString());
                    }
                }
                @Override
                public void onFailure(Call<MovieTrailerKeyResponse> call, Throwable t) {
                    Log.e(TAG, MOVIE_ID + COMMA + t.toString());
                }
            });
    }

    @OnClick(R.id.details_icons_reviews)
    void onClickIconReviews (){
        final int MOVIE_ID = movie.getMovieId();
        ApiInterface apiService = ApiClient.getClientForMovie(MOVIE_ID).create(ApiInterface.class);
        Call<MovieReviewsKeyResponse> call ;
        call = apiService.getMovieReviews(API_KEY_VALUE);
        call.enqueue(new Callback<MovieReviewsKeyResponse>(){
            @Override
            public void onResponse(Call<MovieReviewsKeyResponse> call, Response<MovieReviewsKeyResponse> response) {
                try{
                    List<MovieReviewsKey> results = response.body().getResultsContainReviews();
                    openReviewsActivity(results);
                }catch (Exception e){
                    Toast.makeText(getBaseContext(), noReviewsMsg, Toast.LENGTH_LONG).show();
                    Log.e(TAG, MOVIE_ID + COMMA + e.toString());
                }
            }

            @Override
            public void onFailure(Call<MovieReviewsKeyResponse> call, Throwable t) {
                Log.e(TAG, MOVIE_ID + COMMA + t.toString());
            }
        });
    }

    private void openReviewsActivity (List<MovieReviewsKey> results){
        Intent openReviewsActivity = new Intent(this,ReviewsActivity.class);
        openReviewsActivity.putParcelableArrayListExtra(REVIEWS , (ArrayList<? extends Parcelable>) results);
        startActivity(openReviewsActivity);
    }

    private void changeFavouredState (int isFavoured){
        if (ZERO == isFavoured){
            iconAddToFavouriteList.setTag(ONE);
            iconAddToFavouriteList.setImageResource(R.drawable.ic_heart_red_24);
        } else {
            iconAddToFavouriteList.setTag(ZERO);
            iconAddToFavouriteList.setImageResource(R.drawable.ic_heart_empty_24);
        }
        isFavouredStateChanged = true;
    }

    private Intent shareMovieDetails() {
        return ShareCompat.IntentBuilder.from(this).
                setType(TEXT_TYPE).
                setText(movieDetails).
                setChooserTitle(appName).
                getIntent();
    }

    private void openVideoTrailer (String youtubeId){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_VND + youtubeId));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_URL + youtubeId));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    public void setMovieDetails(String... movieDetailsValues) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] movieDetailsKeys = getResources().getStringArray(R.array.movie_details);
        for (int i = ZERO; i < movieDetailsValues.length; i++) {
            stringBuilder.append(movieDetailsKeys[i]).append(movieDetailsValues[i]).append(NEW_LINE_CHAR);
        }
        this.movieDetails = stringBuilder.toString();
    }

    @Override
    public void onBackPressed() {
        if (isFavouredStateChanged) startIntentServiceForUpdateFavoured ();
        super.onBackPressed();
    }

    private void startIntentServiceForUpdateFavoured (){
        Intent startIntentServiceForUpdateFavoured = new Intent(this, MoviesIntentService.class);
        startIntentServiceForUpdateFavoured.setAction(MovieReminderTasks.UPDATE_FAVOURED);
        startIntentServiceForUpdateFavoured.putExtra(ID_KEYWORD , movie.getRowId());
        startIntentServiceForUpdateFavoured.putExtra(VALUE,(int)iconAddToFavouriteList.getTag());
        startService(startIntentServiceForUpdateFavoured);
    }


}

