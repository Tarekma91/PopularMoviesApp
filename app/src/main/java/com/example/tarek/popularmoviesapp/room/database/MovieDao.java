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

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movies WHERE favoured <> 1 ")
    List<MovieEntry> getAllMoviesExceptFavoured();

    @Query("SELECT * FROM movies WHERE listType LIKE :type ")
    LiveData<List<MovieEntry>> getMovies (String type);

    @Query("SELECT * FROM movies WHERE favoured = 1 ")
    LiveData<List<MovieEntry>> getFavouredMovies ();

    @Query("SELECT movieId FROM movies WHERE favoured = 1")
    List<Integer> getFavouredMoviesId();

    @Query("DELETE FROM movies WHERE favoured <> 1") // NOT IN ( 1 ) == <> 1
    int deleteAllExceptFavoured ();

    @Query("SELECT * FROM movies WHERE `rowId` = :id")
    MovieEntry getMovieByRowId(int id);

    @Insert
    void insertMovie (MovieEntry movieEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(MovieEntry movieEntry);
}
