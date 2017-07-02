package com.amarullah87.popularmovies.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amarullah87.popularmovies.R;
import com.amarullah87.popularmovies.models.Trailer;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by apandhis on 02/07/17.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.MyViewHolder> {

    private Context mContext;
    private List<Trailer> items;

    public TrailerAdapter(Context mContext, List<Trailer> items) {
        this.mContext = mContext;
        this.items = items;
    }

    @Override
    public TrailerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.trailer_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.MyViewHolder holder, int position) {
        Trailer item = items.get(position);

        String thumbnailUrl = "http://img.youtube.com/vi/" + item.getKey() + "/0.jpg";
        Picasso.with(mContext)
                .load(thumbnailUrl)
                .config(Bitmap.Config.RGB_565)
                .placeholder(R.drawable.ic_loading)
                .error(R.mipmap.ic_launcher)
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface ClickListener {
        void onClick(View view, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.trailerThumbnail) ImageView thumbnail;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private TrailerAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final TrailerAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {

                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
