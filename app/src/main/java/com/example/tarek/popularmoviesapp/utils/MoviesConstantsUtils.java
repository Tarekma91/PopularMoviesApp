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
package com.example.tarek.popularmoviesapp.utils;

import android.net.Uri;

public interface MoviesConstantsUtils {

    //   https://api.themoviedb.org/3/movie/popular?api_key=#####
    //  scheme: authority        path1/path2/path3?query parameter, value
    //http://api.themoviedb.org/3/movie/157336?api_key=###&append_to_response=videos
    //http://api.themoviedb.org/3/movie/353081/videos?api_key=#####
    String SCHEME = "https";
    String AUTHORITY = "api.themoviedb.org";
    //please add the API key in the gradle.properties like this:
    //API_KEY="your key"
    String API_KEY_VALUE = BuildConfig.API_KEY;
    String API_KEY_KEYWORD = "api_key";
    String PATH1 = "3";
    String PATH2 = "movie";
    String PATH3 = "path3";
    String PATH4 = "videos";
    String POPULAR = "popular"; // to set as PATH3 in url (popular - unchangeable)
    String UPCOMING = "upcoming";// to set as PATH3 in url (upcoming - unchangeable)
    String NOW_PLAYING = "now_playing";// to set as PATH3 in url (now_playing - unchangeable)
    String TOP_RATED = "top_rated";// to set as PATH3 in url (top_rated - unchangeable)
    String FAVOURITE = "favourite"; // to set as PATH3 in url (favourite - unchangeable)

    //"https://image.tmdb.org/t/p/w600_and_h900_bestv2";
    // https://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
    String POSTERS_URL = new Uri.Builder().scheme(SCHEME).authority("image.tmdb.org").appendPath("t")
            .appendPath("p").appendPath("w600_and_h900_bestv2").build().toString();
    //https://www.youtube.com/watch?v=Div0iP65aZo
    String YOUTUBE_URL = "https://www.youtube.com/watch?v=";
    String YOUTUBE_VND = "vnd.youtube:";

    String MOVIE_KEY = "MOVIE";
    String EMPTY_STRING = "";
    String COMMA = " , ";
    String REVIEWS = "REVIEWS";

    String _PREFERENCES = "_preferences";
    String TEXT_TYPE = "text/plain";
    String NEW_LINE_CHAR = "\n";

    String VALUE = "value";
    String ID_KEYWORD = "id";
    String IS_CONNECTED = "isConnected ";

    int ZERO = 0;
    int ONE = 1;
    int THREE = 3;
    int REFRESH_TIME_MS= 1000;
    int INVALID = -1;

    boolean TRUE_VALUE = true;
    boolean FALSE_VALUE = false;

}
