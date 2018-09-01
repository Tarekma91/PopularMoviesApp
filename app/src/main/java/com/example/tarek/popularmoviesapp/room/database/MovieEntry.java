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
package com.example.tarek.popularmoviesapp.room.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "movies")
public class MovieEntry implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int rowId;
    // columns names is the variables names as default
    // if wanted to change it use @ColumnInfo(name = "wanted named") before the variable declaration

    private String posterPath;
    private boolean adult;
    private String overview;
    private String releaseDate;
    private int movieId;
    private String originalTitle;
    private String originalLanguage;
    private String title;
    private Double popularity;
    private int voteCount;
    private String video;
    private Double voteAverage;
    private String listType;
    private int favoured;

    @Ignore
    public MovieEntry(String title ,String originalTitle, String originalLanguage, String posterPath
            , String overview, String releaseDate, String video, int movieId, int voteCount
            , Double popularity, Double voteAverage, boolean adult
            , String listType, int favoured){
        this.posterPath = posterPath;
        this.adult = adult;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.originalTitle = originalTitle;
        this.originalLanguage = originalLanguage;
        this.title = title;
        this.popularity = popularity;
        this.voteCount = voteCount;
        this.video = video;
        this.voteAverage = voteAverage;
        this.favoured = favoured;
        this.movieId = movieId;
        this.listType = listType;
    }


    public MovieEntry(int rowId,String title, String originalTitle, String originalLanguage
            , String posterPath, String overview, String releaseDate, String video, int movieId
            , int voteCount, Double popularity, Double voteAverage, boolean adult
            , String listType, int favoured){
        this.rowId = rowId;
        this.posterPath = posterPath;
        this.adult = adult;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.originalTitle = originalTitle;
        this.originalLanguage = originalLanguage;
        this.title = title;
        this.popularity = popularity;
        this.voteCount = voteCount;
        this.video = video;
        this.voteAverage = voteAverage;
        this.favoured = favoured;
        this.movieId = movieId;
        this.listType= listType;
    }

    protected MovieEntry(Parcel in) {
        rowId = in.readInt();
        favoured = in.readInt();
        posterPath = in.readString();
        adult = in.readByte() != 0;
        overview = in.readString();
        releaseDate = in.readString();
        movieId = in.readInt();
        originalTitle = in.readString();
        originalLanguage = in.readString();
        title = in.readString();
        if (in.readByte() == 0) {
            popularity = null;
        } else {
            popularity = in.readDouble();
        }
        voteCount = in.readInt();
        video = in.readString();
        if (in.readByte() == 0) {
            voteAverage = null;
        } else {
            voteAverage = in.readDouble();
        }
        listType = in.readString();
    }

    public static final Creator<MovieEntry> CREATOR = new Creator<MovieEntry>() {
        @Override
        public MovieEntry createFromParcel(Parcel in) {
            return new MovieEntry(in);
        }

        @Override
        public MovieEntry[] newArray(int size) {
            return new MovieEntry[size];
        }
    };

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

    public int getFavoured() {
        return favoured;
    }

    public int getRowId(){
        return rowId;
    }

    public String getVideo() {
        return video;
    }

    public String getListType() {
        return listType;
    }

    public void setFavoured (int binaryValue){
        this.favoured = binaryValue;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public void setListType(String listType) {
        this.listType = listType;
    }


    public void setVideo(String video) {
        this.video = video;
    }

    public void setPosterPath(String posterPath){
        this.posterPath = posterPath;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(rowId);
        dest.writeInt(favoured);
        dest.writeString(posterPath);
        dest.writeByte((byte) (adult ? 1 : 0));
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeInt(movieId);
        dest.writeString(originalTitle);
        dest.writeString(originalLanguage);
        dest.writeString(title);
        if (popularity == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(popularity);
        }
        dest.writeInt(voteCount);
        dest.writeString(video);
        if (voteAverage == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(voteAverage);
        }
        dest.writeString(listType);
    }
}
