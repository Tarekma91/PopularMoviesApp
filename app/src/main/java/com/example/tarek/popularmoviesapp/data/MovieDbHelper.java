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
package com.example.tarek.popularmoviesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.tarek.popularmoviesapp.data.MovieContract.MovieEntry;

class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shelter.db";
    private static final int DATABASE_VERSION = 6;

    private static final String CREATE_TABLE = "CREATE TABLE ";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String AUTOINCREMENT = " AUTOINCREMENT";
    private static final String TEXT = " TEXT";
    private static final String INTEGER = " INTEGER";
    private static final String DEFAULT = " DEFAULT ";
    private static final String NOT_NULL = " NOT NULL";
    private static final String OPEN_BRACKET = "(";
    private static final String CLOSE_BRACKET = ")";
    private static final String COMA = ",";
    private static final String SEMI_COLUMN = ";";
    private static final int ZERO = 0;
    private static final String DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS ";

    MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE_QUERY = CREATE_TABLE + MovieEntry.TABLE_NAME + OPEN_BRACKET +
                MovieEntry.COLUMN_ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMA +
                MovieEntry.COLUMN_MOVIE_ID + TEXT + NOT_NULL + COMA + /* it won't change so let it text not integer*/
                MovieEntry.COLUMN_DATE + INTEGER + NOT_NULL + COMA +
                MovieEntry.COLUMN_TITLE + TEXT + NOT_NULL + COMA +
                MovieEntry.COLUMN_ORIGINAL_TITLE + TEXT + NOT_NULL + COMA +
                MovieEntry.COLUMN_ORIGINAL_LANGUAGE + TEXT + NOT_NULL + COMA +
                MovieEntry.COLUMN_VOTE_AVERAGE + TEXT + NOT_NULL + COMA + /* it won't change so let it text not double*/
                MovieEntry.COLUMN_VOTE_COUNT + TEXT + NOT_NULL + COMA +
                MovieEntry.COLUMN_POPULARITY + TEXT + NOT_NULL + COMA +
                MovieEntry.COLUMN_RELEASE_DATE + TEXT + NOT_NULL + COMA +
                MovieEntry.COLUMN_ADULT + INTEGER + NOT_NULL + COMA + /* boolean */
                MovieEntry.COLUMN_VIDEO + TEXT + NOT_NULL + COMA + /* boolean */
                MovieEntry.COLUMN_OVERVIEW + TEXT + NOT_NULL + COMA +
                MovieEntry.COLUMN_POSTER_PATH + TEXT + NOT_NULL + COMA +
                MovieEntry.COLUMN_BACKDROP_PATH + TEXT + NOT_NULL + COMA +
                MovieEntry.COLUMN_FAVOURITE + INTEGER + NOT_NULL + DEFAULT + ZERO + COMA +
                MovieEntry.COLUMN_MOVIES_POPULAR + INTEGER + NOT_NULL + DEFAULT + ZERO + COMA +
                MovieEntry.COLUMN_MOVIES_UPCOMING + INTEGER + NOT_NULL + DEFAULT + ZERO + COMA +
                MovieEntry.COLUMN_MOVIES_TOP_RATED + INTEGER + NOT_NULL + DEFAULT + ZERO + COMA +
                MovieEntry.COLUMN_MOVIES_NOW_PLAYING + INTEGER + NOT_NULL + DEFAULT + ZERO + CLOSE_BRACKET + SEMI_COLUMN;
        /* list types :default:"" , popular , top rated , upcoming , playing now */
        db.execSQL(CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop the current then create new one
        db.execSQL(DROP_TABLE_IF_EXISTS + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
