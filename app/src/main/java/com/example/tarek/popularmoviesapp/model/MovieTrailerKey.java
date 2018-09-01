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

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MovieTrailerKey implements Parcelable {
    // id	"5794fffbc3a36829ab00056f"
    // iso_639_1	"en"
    // iso_3166_1	"US"
    // key	"2LqzF5WauAw"
    // name	"Interstellar Movie - Official Trailer"
    // site	"YouTube"
    // size	1080
    // type	"Trailer"

    public static final Creator<MovieTrailerKey> CREATOR = new Creator<MovieTrailerKey>() {
        @Override
        public MovieTrailerKey createFromParcel(Parcel in) {
            return new MovieTrailerKey(in);
        }

        @Override
        public MovieTrailerKey[] newArray(int size) {
            return new MovieTrailerKey[size];
        }
    };
    @SerializedName("id")
    private String id;
    @SerializedName("key")
    private String key;

    private MovieTrailerKey(Parcel in) {
        id = in.readString();
        key = in.readString();
    }

    public String getKey() {
        return key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(key);
    }
}
