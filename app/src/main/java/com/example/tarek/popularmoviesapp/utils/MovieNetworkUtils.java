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

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.tarek.popularmoviesapp.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

class MovieNetworkUtils implements MoviesConstantsUtils {

    private static final String TAG = MovieNetworkUtils.class.getSimpleName();

    private static String getUrlString(Context context, String key, int idMovie, final String LIST_TYPE) {
        Uri.Builder builder = new Uri.Builder();
        final String PATH3; // for path 3 in url
        switch (key) {
            case MOVIE_KEY_FOR_URL:
                //like : http://api.themoviedb.org/3/movie/353081/videos?api_key=#####
                PATH3 = String.valueOf(idMovie);
                builder.scheme(SCHEME).
                        authority(AUTHORITY).
                        appendPath(PATH1).
                        appendPath(PATH2).
                        appendPath(PATH3).
                        appendPath(PATH4);
                break;
            case LIST_KEY_FOR_URL:
                //like : https://api.themoviedb.org/3/movie/popular?api_key=#####
            default:
                PATH3 = LIST_TYPE;
                builder.scheme(SCHEME).
                        authority(AUTHORITY).
                        appendPath(PATH1).
                        appendPath(PATH2).
                        appendPath(PATH3);
        }
        builder.appendQueryParameter(API_KEY_KEYWORD, API_KEY_VALUE).build();
        return builder.toString();
    }

    public static String getJsonResponseForListMovies(Context context, String listType) {
        String jsonResponse = EMPTY_STRING;
        URL url = buildUrl(getUrlString(context, LIST_KEY_FOR_URL, ZERO, listType));
        if (null != url) {
            Log.d(TAG, context.getString(R.string.downloading_url_msg) + url);
            jsonResponse = makeHttpRequest(context, url);
            Log.d(TAG, context.getString(R.string.response_msg) + jsonResponse);
        }
        return jsonResponse;
    }

    public static String getJsonResponseForMovie(Context context, int idMovie) {
        String jsonResponse = null;
        URL url = buildUrl(getUrlString(context, MOVIE_KEY_FOR_URL, idMovie, null));
        if (null != url) {
            Log.d(TAG, context.getString(R.string.downloading_url_msg) + url);
            jsonResponse = makeHttpRequest(context, url);
        }
        return jsonResponse;
    }

    private static URL buildUrl(String baseUrl) {
        URL url;
        try {
            url = new URL(baseUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        return url;
    }

    private static String makeHttpRequest(Context context, URL url) {
        String jsonResponse = EMPTY_STRING;
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(REQUESTED_METHOD);
            connection.setReadTimeout(READ_TIME_OUT);
            connection.setConnectTimeout(CONNECT_TIME_OUT);
            connection.connect();

            // The app checks whether the device is connected to the internet and responds appropriately.
            // The result of the request is validated to account for a bad server response or lack of server response.
            int response = connection.getResponseCode();
            if (response == THE_REQUEST_IS_OK) {
                InputStream stream = connection.getInputStream();
                jsonResponse = readInputStream(stream);
            } else if (response == BAD_GATEWAY || response == SERVICE_UNAVAILABLE) {
                Log.d(TAG, context.getString(R.string.error_no_service_bad_gatway_msg) + response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonResponse;
    }

    private static String readInputStream(InputStream inputStream) {
        // if used StringBuilder it throws an exception
        // E/art: Throwing OutOfMemoryError "Failed to allocate a 272854860 byte allocation with 4194304 free bytes and 207MB until OOM"
        String response = EMPTY_STRING;
        try {
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter(SCANNER_REGEX); // Now the scanner set as delimiter the [Regexp for \A][1], \A stands for :start of a string!
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return response;
        }
    }
}
