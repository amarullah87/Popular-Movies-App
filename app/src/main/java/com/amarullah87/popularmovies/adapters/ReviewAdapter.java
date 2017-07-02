package com.amarullah87.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amarullah87.popularmovies.R;
import com.amarullah87.popularmovies.models.Review;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by apandhis on 02/07/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder>{

    private Context mContext;
    private List<Review> items;

    public ReviewAdapter(Context mContext, List<Review> items) {
        this.mContext = mContext;
        this.items = items;
    }

    @Override
    public ReviewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.review_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.MyViewHolder holder, int position) {
        Review item = items.get(position);
        holder.author.setText(item.getAuthor());
        holder.content.setText(item.getContent());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtAuthor) TextView author;
        @BindView(R.id.txtContent) TextView content;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
