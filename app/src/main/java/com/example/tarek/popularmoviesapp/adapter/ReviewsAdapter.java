package com.example.tarek.popularmoviesapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.tarek.popularmoviesapp.R;
import com.example.tarek.popularmoviesapp.model.MovieReviewsKey;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils.ZERO;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder> {

    private final String TAG = ReviewsAdapter.class.getSimpleName();
    private List<MovieReviewsKey> movieReviews;
    private MovieReviewsKey review;
    private Context context;

    @NonNull
    @Override
    public ReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View root = LayoutInflater.from(context).inflate(R.layout.reviews_list_item,parent,false);
        return new ReviewsViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsViewHolder holder, int position) {
        review = movieReviews.get(position);
        String content = review.getContent();
        String author = review.getAuthor();
        holder.authorTV.setText(author);
        holder.contentTV.setText(content);
    }

    @Override
    public int getItemCount() {
        if (null == movieReviews)return ZERO;

        return movieReviews.size();
    }

    public void setMovieReviews(List<MovieReviewsKey> movieReviews){
        this.movieReviews = movieReviews;
    }


    class ReviewsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.author)
        TextView authorTV;
        @BindView(R.id.content)
        TextView contentTV;
        @BindView(R.id.open_web_page)
        ImageView openWebPageIcon;

        @BindString(R.string.no_broswer_found_msg)
        String noBrowserFoundMsg;
        @BindString(R.string.value_color_blue_bright)
        String blueColor;

        ReviewsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        @OnClick(R.id.open_web_page)
        void onClickOpenWebPageIcon(){
            String urlReview = review.getUrlReview();
            Intent openWebPageForReview = new Intent(Intent.ACTION_VIEW, Uri.parse(urlReview));
            PackageManager packageManager = context.getPackageManager();
            if (openWebPageForReview.resolveActivity(packageManager) != null) {
                context.startActivity(openWebPageForReview);
            } else {
                Toast.makeText(context,noBrowserFoundMsg , Toast.LENGTH_LONG).show();
            }
        }
    }
}
