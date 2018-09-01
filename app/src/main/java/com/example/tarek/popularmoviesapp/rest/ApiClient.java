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
package com.example.tarek.popularmoviesapp.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    //   https://api.themoviedb.org/3/movie/popular?api_key=####
    //  scheme: authority        path1/path2/path3?query parameter, value
    //  http://api.themoviedb.org/3/movie/157336?api_key=###&append_to_response=videos
    //  http://api.themoviedb.org/3/movie/353081/videos?api_key=####   trailers
    //  https://api.themoviedb.org/3/movie/83542/reviews?api_key=#### reviews

    private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static Retrofit retrofitForListMovies = null;
    private static final String SLASH = "/";

    public static Retrofit getClientForListMovies() {
        if (retrofitForListMovies == null) {
            retrofitForListMovies = new Retrofit.Builder().
                    baseUrl(BASE_URL).
                    addConverterFactory(GsonConverterFactory.create()).
                    build();
        }
        return retrofitForListMovies;
    }

    public static Retrofit getClientForMovie(int movieId) {
        final String url = BASE_URL + String.valueOf(movieId) + SLASH;
        return new Retrofit.Builder().
                baseUrl(url).
                addConverterFactory(GsonConverterFactory.create()).
                build();
    }
}