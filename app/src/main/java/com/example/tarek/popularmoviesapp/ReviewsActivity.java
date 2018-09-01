package com.example.tarek.popularmoviesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.example.tarek.popularmoviesapp.adapter.ReviewsAdapter;
import com.example.tarek.popularmoviesapp.model.MovieReviewsKey;
import com.example.tarek.popularmoviesapp.utils.BackgroundColorUtils;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.REVIEWS;

public class ReviewsActivity extends AppCompatActivity {

    @BindView(R.id.reviews_recycler_view)
    RecyclerView recyclerView;

    private ReviewsAdapter reviewsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        ButterKnife.bind(this);

        BackgroundColorUtils backgroundColorUtils = new BackgroundColorUtils(this,null);
        backgroundColorUtils.getSharedPreferenceColor(PreferenceManager.getDefaultSharedPreferences(this));

        reviewsAdapter = new ReviewsAdapter();
        setRecyclerView();
        getComingIntent();
    }

    private void getComingIntent (){
        Intent comingIntent = getIntent();
        List<MovieReviewsKey> results = comingIntent.getParcelableArrayListExtra(REVIEWS);
        reviewsAdapter.setMovieReviews(results);
    }

    private void setRecyclerView (){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(reviewsAdapter);
    }


}
