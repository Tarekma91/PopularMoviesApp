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
package com.example.tarek.popularmoviesapp.model;


import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.tarek.popularmoviesapp.data.MovieContract.MovieEntry;
import com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils;
import com.google.gson.annotations.SerializedName;

public class Movie implements Parcelable, MoviesConstantsUtils {

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private final int rowId;
    private final boolean favoured;

    @SerializedName("poster_path")
    private final String posterPath;
    @SerializedName("adult")
    private final boolean adult;
    @SerializedName("overview")
    private final String overview;
    @SerializedName("release_date")
    private final String releaseDate;
    @SerializedName("id")
    private final int movieId;
    @SerializedName("original_title")
    private final String originalTitle;
    @SerializedName("original_language")
    private final String originalLanguage;
    @SerializedName("title")
    private final String title;
    @SerializedName("backdrop_path")
    private final String backdropPath;
    @SerializedName("popularity")
    private final Double popularity;
    @SerializedName("vote_count")
    private final Integer voteCount;
    @SerializedName("video")
    private final String video;
    @SerializedName("vote_average")
    private final Double voteAverage;

    public Movie(Cursor cursor) {
        this.rowId = cursor.getInt(cursor.getColumnIndex(MovieEntry._ID));
        this.posterPath = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH));
        this.adult = cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_ADULT)) == ONE ? TRUE_VALUE : FALSE_VALUE;
        this.overview = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_OVERVIEW));
        this.releaseDate = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE));
        this.originalTitle = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_ORIGINAL_TITLE));
        this.originalLanguage = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_ORIGINAL_LANGUAGE));
        this.title = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_TITLE));
        this.backdropPath = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_BACKDROP_PATH));
        this.popularity = cursor.getDouble(cursor.getColumnIndex(MovieEntry.COLUMN_POPULARITY));
        this.voteCount = cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_VOTE_COUNT));
        this.video = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_VIDEO));
        this.voteAverage = cursor.getDouble(cursor.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE));
        this.favoured = cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_FAVOURITE)) == ONE ? TRUE_VALUE : FALSE_VALUE;
        this.movieId = cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID));
    }

    private Movie(Parcel in) {
        movieId = in.readInt();
        rowId = in.readInt();
        posterPath = in.readString();
        adult = in.readByte() != 0;
        favoured = in.readByte() != 0;
        overview = in.readString();
        releaseDate = in.readString();
        originalTitle = in.readString();
        originalLanguage = in.readString();
        title = in.readString();
        backdropPath = in.readString();
        if (in.readByte() == 0) {
            popularity = null;
        } else {
            popularity = in.readDouble();
        }
        if (in.readByte() == 0) {
            voteCount = null;
        } else {
            voteCount = in.readInt();
        }
        video = in.readString();
        if (in.readByte() == 0) {
            voteAverage = null;
        } else {
            voteAverage = in.readDouble();
        }
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getTitle() {
        return title;
    }

    public Double getPopularity() {
        return popularity;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public int getMovieId() {
        return movieId;
    }

    public boolean isAdult() {
        return adult;
    }

    public boolean isFavoured() {
        return favoured;
    }

    public int getRowId() {
        return rowId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movieId);
        dest.writeInt(rowId);
        dest.writeString(posterPath);
        dest.writeByte((byte) (adult ? 1 : 0));
        dest.writeByte((byte) (favoured ? 1 : 0));
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeString(originalTitle);
        dest.writeString(originalLanguage);
        dest.writeString(title);
        dest.writeString(backdropPath);
        if (popularity == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(popularity);
        }
        if (voteCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(voteCount);
        }
        dest.writeString(video);
        if (voteAverage == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(voteAverage);
        }
    }
}
