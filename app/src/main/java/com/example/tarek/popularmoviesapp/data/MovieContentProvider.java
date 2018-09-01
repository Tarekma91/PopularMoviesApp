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


import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.tarek.popularmoviesapp.R;
import com.example.tarek.popularmoviesapp.data.MovieContract.MovieEntry;
import com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils;

public class MovieContentProvider extends ContentProvider implements MoviesConstantsUtils {

    private final static String TAG = MovieContentProvider.class.getName();
    private static final int MOVIE = 100; // code for table path uri
    private static final int MOVIE_ID = 101; // code for table's rows uri
    private static final String READABLE = "READABLE";
    private static final String WRITABLE = "WRITABLE";
    // to create 2 uris of table and rows and add them to MATCHER object
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        MATCHER.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.MOVIES_PATH, MOVIE);

        MATCHER.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.MOVIES_PATH + "/#", MOVIE_ID);
    }

    private MovieDbHelper dbHelper;
    private ContentResolver contentResolver;
    private Context context;

    @Override
    public boolean onCreate() {
        context = getContext();
        dbHelper = new MovieDbHelper(context);
        contentResolver = context.getContentResolver();
        return TRUE_VALUE;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = getDb(READABLE);
        if (null == db) return null;

        int match = MATCHER.match(uri); // to know which uri the MATCHER is carrying ?
        Cursor cursor; // cursor which will contains the database / row as the MATCHER gets table or row uri
        switch (match) {
            case MOVIE:
                cursor = db.query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null, sortOrder);
                break;
            case MOVIE_ID:
                cursor = db.query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                Log.e(TAG, context.getString(R.string.error_displaying_data) + uri);
                return null;
        }
        cursor.setNotificationUri(contentResolver, uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = MATCHER.match(uri);

        if (match == MOVIE) {
            return MovieEntry.MOVIES_LIST_TYPE;
        } else { //  match == MOVIE_ID
            return MovieEntry.MOVIES_ITEM_TYPE;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = MATCHER.match(uri);

        if (match == MOVIE_ID) {
            Log.e(TAG, context.getString(R.string.error_inserting_data) + uri);
            return null;
        }
        notifyChangesToContentResolver(uri);
        return insertData(uri, values);
    }

    /**
     * to insert the data to the table
     *
     * @param uri    of the table
     * @param values which well be inserted
     * @return newRowUri
     */
    private Uri insertData(Uri uri, ContentValues values) {
        SQLiteDatabase db = getDb(WRITABLE);
        if (null == db) return null;

        int newRowId = (int) db.insert(MovieEntry.TABLE_NAME, null, values);
        if (newRowId == INVALID) { // if not inserted throw this error
            Log.e(TAG, context.getString(R.string.error_inserting_data) + uri);
            return null;
            //throw new IllegalArgumentException (TAG + context.getString(R.string.error_inserting_data) + uri);
        }
        // else return new uri with the id of inserted data
        notifyChangesToContentResolver(uri);
        return Uri.withAppendedPath(uri, String.valueOf(newRowId));
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = getDb(WRITABLE);
        if (null == db) return INVALID;

        int match = MATCHER.match(uri);
        int deletedRows;
        switch (match) {
            case MOVIE:
                deletedRows = db.delete(MovieEntry.TABLE_NAME, null, null); // to delete all rows but still increment according to last id
                break;
            case MOVIE_ID:
                deletedRows = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                Log.e(TAG, context.getString(R.string.error_deleting_data) + uri);
                deletedRows = INVALID;
        }
        notifyChangesToContentResolver(uri);
        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = MATCHER.match(uri);

        if (match == MOVIE) {
            // because update only working if take row id not table names
            Log.e(TAG, context.getString(R.string.error_updating_data) + uri);
            return INVALID;
        }

        SQLiteDatabase db = getDb(WRITABLE);
        if (null == db) return INVALID;

        int affectRows = db.update(MovieEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        notifyChangesToContentResolver(uri);
        return affectRows;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        int match = MATCHER.match(uri);
        SQLiteDatabase db = getDb(WRITABLE);
        if (null == db) return INVALID;

        switch (match) {
            case MOVIE:
                int insertedRow = ZERO;
                db.beginTransaction();
                try {
                    for (ContentValues newValues : values) {
                        db.insert(MovieEntry.TABLE_NAME, null, newValues);
                        insertedRow++;
                    }
                    db.setTransactionSuccessful();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } finally {
                    db.endTransaction();
                }

                if (insertedRow > ZERO) notifyChangesToContentResolver(uri);
                return insertedRow;

            default:
                return super.bulkInsert(uri, values);
        }

    }


    private void notifyChangesToContentResolver(Uri uri) {
        contentResolver.notifyChange(uri, null);
    }

    private SQLiteDatabase getDb(String readableOrWritable) {
        SQLiteDatabase db = null;
        try {
            switch (readableOrWritable) {
                case READABLE:
                    db = dbHelper.getReadableDatabase(); // get readable database to retrieve data to display it
                    break;
                case WRITABLE:
                    db = dbHelper.getWritableDatabase(); // get writable database
                    break;
            }
        } catch (SQLiteCantOpenDatabaseException e) {
            e.printStackTrace();
            return null;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return null;
        }
        return db;
    }
}
