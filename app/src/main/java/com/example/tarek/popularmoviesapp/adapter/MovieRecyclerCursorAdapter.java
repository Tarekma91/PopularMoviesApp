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
package com.example.tarek.popularmoviesapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tarek.popularmoviesapp.R;
import com.example.tarek.popularmoviesapp.data.MovieContract.MovieEntry;
import com.example.tarek.popularmoviesapp.model.Movie;
import com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.ONE;
import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.ZERO;

public class MovieRecyclerCursorAdapter extends RecyclerView.Adapter<MovieRecyclerCursorAdapter.MovieViewHolder> {

    private final MovieAdapterOnClickHandler movieAdapterOnClickHandler;
    private Cursor cursor;
    private Movie currentMovie;

    public MovieRecyclerCursorAdapter(Cursor cursor, MovieAdapterOnClickHandler movieAdapterOnClickHandler) {
        this.movieAdapterOnClickHandler = movieAdapterOnClickHandler;
        this.cursor = cursor;
    }


    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View root = LayoutInflater.from(context).inflate(R.layout.movies_list_item, parent, false);
        return new MovieViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        if (null != cursor) {
            if (cursor.moveToPosition(position)) {
                holder.itemPosition.setText(String.valueOf(position + ONE));
                holder.ratingTv.setText(cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE)));
                String url = MoviesConstantsUtils.POSTERS_185_URL +
                        cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH));
                Picasso.get().load(url).placeholder(R.drawable.progress_animation)
                        .error(R.drawable.icon_app)
                        .into(holder.moviePosterIV);
                int adult = cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_ADULT));
                if (ZERO == adult) {
                    holder.adultIcon.setVisibility(View.GONE);
                } else {
                    holder.adultIcon.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (cursor == null) return MoviesConstantsUtils.ZERO;

        return cursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (null != cursor) cursor.close();
        cursor = newCursor;
        notifyDataSetChanged();
    }


    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_item_movie_poster)
        ImageView moviePosterIV;
        @BindView(R.id.iv_adult_icon)
        ImageView adultIcon;
        @BindView(R.id.tv_item_rating_value)
        TextView ratingTv;
        @BindView(R.id.tv_items_position_in_adapter)
        TextView itemPosition;

        private MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (cursor.moveToPosition(position)) currentMovie = new Movie(cursor);

            movieAdapterOnClickHandler.onClick(currentMovie);
        }
    }
}
