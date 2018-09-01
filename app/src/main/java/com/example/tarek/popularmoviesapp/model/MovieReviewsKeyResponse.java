package com.example.tarek.popularmoviesapp.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MovieReviewsKeyResponse {
    @SerializedName("results")
    private List<MovieReviewsKey> reviews;

    public List<MovieReviewsKey> getResultsContainReviews() {
        return reviews;
    }
}
