package com.example.tarek.popularmoviesapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MovieReviewsKey implements Parcelable {

    @SerializedName("author")
    private String author;
    @SerializedName("content")
    private String content;
    @SerializedName("url")
    private String urlReview;

    private MovieReviewsKey(Parcel in) {
        author = in.readString();
        content = in.readString();
        urlReview = in.readString();
    }

    public static final Creator<MovieReviewsKey> CREATOR = new Creator<MovieReviewsKey>() {
        @Override
        public MovieReviewsKey createFromParcel(Parcel in) {
            return new MovieReviewsKey(in);
        }

        @Override
        public MovieReviewsKey[] newArray(int size) {
            return new MovieReviewsKey[size];
        }
    };


    public String getAuthor() {
        return author;
    }

    public String getUrlReview() {
        return urlReview;
    }

    public String getContent() {
        return content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(urlReview);
    }
}
