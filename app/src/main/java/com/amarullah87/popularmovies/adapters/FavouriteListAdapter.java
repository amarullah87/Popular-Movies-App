package com.amarullah87.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amarullah87.popularmovies.DetailsMovieActivity;
import com.amarullah87.popularmovies.R;
import com.amarullah87.popularmovies.utilities.MovieContract;
import com.amarullah87.popularmovies.utilities.MovieDbHelper;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.amarullah87.popularmovies.utilities.MovieContract.MovieEntry.TABLE_NAME;

/**
 * Created by apandhis on 31/07/17.
 */

public class FavouriteListAdapter extends RecyclerView.Adapter<FavouriteListAdapter.MyViewHolder> {

    private Cursor mCursor;
    private Context mContext;
    private SQLiteDatabase mDb;

    public FavouriteListAdapter(Cursor mCursor, Context mContext) {
        this.mCursor = mCursor;
        this.mContext = mContext;
    }

    @Override
    public FavouriteListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavouriteListAdapter.MyViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position))
            return;

        String posterPath = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COL_POSTER));
        if(posterPath == null){
            holder.title.setVisibility(View.VISIBLE);
            holder.thumbnail.setVisibility(View.GONE);
        }

        Picasso.with(mContext)
                .load("http://image.tmdb.org/t/p/w185" + posterPath)
                .config(Bitmap.Config.RGB_565)
                .placeholder(R.drawable.ic_loading)
                .error(R.mipmap.ic_launcher)
                .into(holder.thumbnail);

        final long id = mCursor.getLong(mCursor.getColumnIndex(MovieContract.MovieEntry._ID));
        holder.itemView.setTag(id);
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle extras = new Bundle();
                Intent intent = new Intent(mContext, DetailsMovieActivity.class);

                Uri movie = Uri.parse(String.valueOf(MovieContract.MovieEntry.CONTENT_URI + "/" + String.valueOf(id)));
                Cursor c = mContext.getContentResolver().query(movie, null, null, null, null);
                if (c != null) {
                    c.moveToFirst();
                    extras.putString("idMovie", c.getString(c.getColumnIndex(MovieContract.MovieEntry._ID)));
                    extras.putString("average", c.getString(c.getColumnIndex(MovieContract.MovieEntry.COL_VOTEAVG)));
                    extras.putString("title", c.getString(c.getColumnIndex(MovieContract.MovieEntry.COL_TITLE)));
                    extras.putString("poster", c.getString(c.getColumnIndex(MovieContract.MovieEntry.COL_POSTER)));
                    extras.putString("backdrop", c.getString(c.getColumnIndex(MovieContract.MovieEntry.COL_BACKDROP)));
                    extras.putString("overview", c.getString(c.getColumnIndex(MovieContract.MovieEntry.COL_OVERVIEW)));
                    extras.putString("releaseDate", c.getString(c.getColumnIndex(MovieContract.MovieEntry.COL_RELEASEDATE)));
                    c.close();
                }
                intent.putExtras(extras);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    public interface ClickListener {
        void onClick(View view, int position);
    }

    @Override
    public int getItemCount() {
        return (null != mCursor ? mCursor.getCount() : 0);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.thumbnail) ImageView thumbnail;
        @BindView(R.id.title) TextView title;
        @BindView(R.id.viewParent) FrameLayout viewParent;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private FavouriteListAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final FavouriteListAdapter.ClickListener clickListener) {
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
