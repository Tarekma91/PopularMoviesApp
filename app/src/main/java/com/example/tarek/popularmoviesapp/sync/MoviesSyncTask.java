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
package com.example.tarek.popularmoviesapp.sync;

import android.content.Context;
import android.util.Log;
import com.example.tarek.popularmoviesapp.R;
import com.example.tarek.popularmoviesapp.model.Movie;
import com.example.tarek.popularmoviesapp.model.MoviesResponse;
import com.example.tarek.popularmoviesapp.rest.ApiClient;
import com.example.tarek.popularmoviesapp.rest.ApiInterface;
import com.example.tarek.popularmoviesapp.room.AppExecutors;
import com.example.tarek.popularmoviesapp.room.database.AppDatabase;
import com.example.tarek.popularmoviesapp.room.database.MovieEntry;
import com.example.tarek.popularmoviesapp.utils.MovieNotificationUtils;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.API_KEY_VALUE;
import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.FALSE_VALUE;
import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.FAVOURITE;
import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.NOW_PLAYING;
import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.ONE;
import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.TOP_RATED;
import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.TRUE_VALUE;
import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.UPCOMING;
import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.ZERO;

public class MoviesSyncTask {

    private static final String TAG = MoviesSyncTask.class.getSimpleName();
    private static boolean isMoviesDeleted = FALSE_VALUE;

    static public void syncMovies(final Context context) {
        //will load all 4 types of movies's list popular, top rated, upcoming, now playing
        // this approach will load the data scheduled every 6-12 hours
        // and when the user opens the app
        // and if the user changed the listType in sharedPreferences
        String[] listTypes = context.getResources().getStringArray(R.array.values_list_types);
        final ApiInterface apiService = ApiClient.getClientForListMovies().create(ApiInterface.class);
        Call<MoviesResponse> call ;
        for (final String listType : listTypes) {
            if (!FAVOURITE.equals(listType)){
                switch (listType) {
                    case NOW_PLAYING:
                        call = apiService.getNowPlayingMovies(API_KEY_VALUE);
                        break;
                    case TOP_RATED:
                        call = apiService.getTopRatedMovies(API_KEY_VALUE);
                        break;
                    case UPCOMING:
                        call = apiService.getUpcomingMovies(API_KEY_VALUE);
                        break;
                    default:
                        call = apiService.getPopularMovies(API_KEY_VALUE); // as default case
                        break;
                }
                call.enqueue(new Callback<MoviesResponse>() {
                    List<Movie> movies;
                    AppDatabase appDatabase = AppDatabase.getInstance(context);
                    @Override
                    public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                      try {
                          movies = response.body().getResults();
                          if (ZERO < movies.size()) {
                              AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                  @Override
                                  public void run() {
                                      deleteOldMoviesForOnceTime(appDatabase, context);
                                      for (int i = ZERO; i < movies.size(); i++) {
                                          Movie movie = movies.get(i);
                                          final MovieEntry movieEntry = getMovieEntry(movie,listType);
                                          int entryId = movieEntry.getMovieId();
                                          if (isFoundInDb(appDatabase,entryId)) { // update it
                                              movieEntry.setFavoured(ONE);// keep the movie favoured while updating it
                                              appDatabase.movieDao().updateMovie(movieEntry);
                                          } else {
                                              appDatabase.movieDao().insertMovie(movieEntry);
                                          }
                                      }
                                  }
                              });
                          }
                          MovieNotificationUtils.setNotification(context);
                      }catch (Exception e){
                          e.printStackTrace();
                      }
                    }
                    @Override
                    public void onFailure(Call<MoviesResponse> call, Throwable t) {
                        Log.e(TAG, t.toString());
                    }
                });
            }
        }
        isMoviesDeleted = FALSE_VALUE; // to let delete method work again once in the next refresh
    }

    private static void deleteOldMoviesForOnceTime(final AppDatabase appDatabase ,Context context) {
        if (!isMoviesDeleted) {
            int deleted = appDatabase.movieDao().deleteAllExceptFavoured();
            Log.d(TAG,context.getString(R.string.deleted_msg) + deleted);
            isMoviesDeleted = TRUE_VALUE;
        }
    }

    private static MovieEntry getMovieEntry(Movie movie, String listType) {
        String posterPath = movie.getPosterPath();
        boolean adult = movie.isAdult();
        String overview = movie.getOverview();
        String releaseDate = movie.getReleaseDate();
        String originalTitle = movie.getOriginalTitle();
        String originalLanguage = movie.getOriginalLanguage();
        String title = movie.getTitle();
        Double popularity = movie.getPopularity();
        int voteCount = movie.getVoteCount();
        Double voteAverage = movie.getVoteAverage();
        int favoured = movie.isFavoured() ? ONE : ZERO;
        int movieId = movie.getMovieId();

        return new MovieEntry(title, originalTitle, originalLanguage, posterPath, overview, releaseDate
                , null, movieId, voteCount, popularity, voteAverage, adult, listType, favoured);
    }

    private static boolean isFoundInDb (AppDatabase appDatabase , int entryId){
        List<Integer> favouredIds = appDatabase.movieDao().getFavouredMoviesId();
        return favouredIds.contains(entryId) ;
    }
}

