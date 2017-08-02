package com.amarullah87.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amarullah87.popularmovies.adapters.FavouriteListAdapter;
import com.amarullah87.popularmovies.adapters.MovieAdapter;
import com.amarullah87.popularmovies.utilities.MovieContract;
import com.amarullah87.popularmovies.utilities.MovieDbHelper;
import com.amarullah87.popularmovies.models.Movie;
import com.amarullah87.popularmovies.models.Movies;
import com.amarullah87.popularmovies.utils.InternetConnection;
import com.amarullah87.popularmovies.utils.RestAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.amarullah87.popularmovies.utilities.MovieContract.MovieEntry.COL_TITLE;
import static com.amarullah87.popularmovies.utils.Configs.FAVOURITE;
import static com.amarullah87.popularmovies.utils.Configs.POPULAR;
import static com.amarullah87.popularmovies.utils.Configs.TOP_RATED;
import static com.amarullah87.popularmovies.utils.Configs.UPCOMING;
import static com.amarullah87.popularmovies.utils.Configs.getDataAPI;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<String>{

    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rvMovies) RecyclerView rvMovies;
    @BindView(R.id.rvFavourites) RecyclerView rvFavourites;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    private List<Movie> movies = new ArrayList<>();

    private MovieAdapter adapter;
    private static final String SEARCH_QUERY = "query";
    private static final int MOVIE_LOADER_ID = 22;

    boolean doubleBackToExitPressedOnce = false;
    private String mSortBy = POPULAR;
    private RecyclerView.LayoutManager manager;
    int colsPortrait = 2;
    int colsLandscape = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        loadDefaultMovies(POPULAR);
        rvMovies.addOnItemTouchListener(new MovieAdapter.RecyclerTouchListener(getApplicationContext(),
                rvMovies, new MovieAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Movie item = movies.get(position);
                Bundle extras = new Bundle();
                Intent intent = new Intent(getApplicationContext(), DetailsMovieActivity.class);
                extras.putString("idMovie", String.valueOf(item.getId()));
                extras.putString("average", String.valueOf(item.getVoteAverage()));
                extras.putString("title", item.getTitle());
                extras.putString("poster", item.getPosterPath());
                extras.putString("backdrop", item.getBackdropPath());
                extras.putString("overview", item.getOverview());
                extras.putString("releaseDate", item.getReleaseDate());
                intent.putExtras(extras);
                startActivity(intent);
            }
        }));
        if (savedInstanceState != null) {
            String sorting = savedInstanceState.getString(SEARCH_QUERY);
            Log.e(TAG + "-savedInstance", sorting);
        }

        //makeQuery();
        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
    }

    private void makeQuery(){
        Bundle queryBundle = new Bundle();
        queryBundle.putString(SEARCH_QUERY, mSortBy);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> sortingLoader = loaderManager.getLoader(MOVIE_LOADER_ID);
        if (sortingLoader == null) {
            loaderManager.initLoader(MOVIE_LOADER_ID, queryBundle, this);
            //Toast.makeText(this, "init: " + mSortBy, Toast.LENGTH_SHORT).show();
            Log.e(TAG + "-initLoader", mSortBy);
        } else {
            loaderManager.restartLoader(MOVIE_LOADER_ID, queryBundle, this);
            Log.e(TAG + "-restart", mSortBy);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG + "-onresume", mSortBy);
        makeQuery();
        Log.e(TAG + "-onresume2", mSortBy);
    }

    private void loadDefaultMovies(String sortType) {
        progressBar.setVisibility(View.VISIBLE);
        rvMovies.setVisibility(View.GONE);
        rvFavourites.setVisibility(View.GONE);

        //I created a Class to Check Network State Called "Internet Connection"
        //Before the retrofit Launch, it will check the network First (Wifi/ Data)
        if(InternetConnection.checkConnection(getApplicationContext())){

            if(!movies.isEmpty()){
                movies.clear();
            }

            RestAPI restAPI = getDataAPI().create(RestAPI.class);
            Call<Movies> call = restAPI.getMovies(sortType, BuildConfig.MOVIEDB_API_KEY);

            call.enqueue(new Callback<Movies>() {
                @Override
                public void onResponse(Call<Movies> call, Response<Movies> response) {
                    progressBar.setVisibility(View.GONE);
                    rvMovies.setVisibility(View.VISIBLE);

                    if(response.isSuccessful() && response.code() != 400) {
                        if (response.body() != null) {
                            movies = response.body().getResults();
                            adapter = new MovieAdapter(getApplicationContext(), movies);
                            adapter.notifyDataSetChanged();

                            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                manager = new GridLayoutManager(getApplicationContext(), colsPortrait);
                            } else {
                                manager = new GridLayoutManager(getApplicationContext(), colsLandscape);
                            }

                            rvMovies.setLayoutManager(manager);
                            rvMovies.setNestedScrollingEnabled(false);
                            rvMovies.setItemAnimator(new DefaultItemAnimator());
                            rvMovies.setAdapter(adapter);
                        } else {
                            Toast.makeText(MainActivity.this, R.string.no_data, Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(MainActivity.this, R.string.oops_something_wrong, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Movies> call, Throwable t) {

                }
            });

        }else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, R.string.oops_network, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        switch (mSortBy){
            case POPULAR:
                menu.findItem(R.id.sort_by_most_popular).setChecked(true);
                break;

            case TOP_RATED:
                menu.findItem(R.id.sort_by_top_rated).setChecked(true);
                break;

            case UPCOMING:
                menu.findItem(R.id.sort_by_upcoming).setChecked(true);
                break;

            case FAVOURITE:
                menu.findItem(R.id.sort_by_fav).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.sort_by_most_popular:
                loadDefaultMovies(POPULAR);

                mSortBy = POPULAR;
                item.setChecked(true);
                break;

            case R.id.sort_by_top_rated:
                loadDefaultMovies(TOP_RATED);

                mSortBy = TOP_RATED;
                item.setChecked(true);
                break;

            case R.id.sort_by_upcoming:
                loadDefaultMovies(UPCOMING);

                mSortBy = UPCOMING;
                item.setChecked(true);
                break;

            case R.id.sort_by_fav:
                loadFavouriteMovies();

                mSortBy = FAVOURITE;
                item.setChecked(true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFavouriteMovies() {
        if(!movies.isEmpty()){
            movies.clear();
        }
        rvMovies.setVisibility(View.GONE);
        rvFavourites.setVisibility(View.VISIBLE);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            manager = new GridLayoutManager(getApplicationContext(), colsPortrait);
        } else {
            manager = new GridLayoutManager(getApplicationContext(), colsLandscape);
        }

        Uri movie = Uri.parse(String.valueOf(MovieContract.MovieEntry.CONTENT_URI));
        Cursor cursor = getContentResolver().query(movie, null, null, null, COL_TITLE);
        FavouriteListAdapter favAdapter = new FavouriteListAdapter(cursor, getApplicationContext());
        rvFavourites.setLayoutManager(manager);
        rvFavourites.setNestedScrollingEnabled(false);
        rvFavourites.setItemAnimator(new DefaultItemAnimator());
        rvFavourites.setAdapter(favAdapter);
        //cursor.close();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            android.os.Process.killProcess(android.os.Process.myPid());
        }else {

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press again to EXIT.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            String mSorting;

            @Override
            protected void onStartLoading() {
                if(args == null){
                    return;
                }
                if(mSorting != null){
                    deliverResult(mSorting);
                }else{
                    forceLoad();
                }
            }

            @Override
            public String loadInBackground() {
                String searchQuery = args.getString(SEARCH_QUERY);
                if(searchQuery == null){
                    return null;
                }

                mSortBy = searchQuery;
                if(!movies.isEmpty()) {
                    movies.clear();
                }
                Log.e(TAG + "-Background", mSortBy);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*if(!mSortBy.equals(FAVOURITE)) {
                            loadDefaultMovies(mSortBy);
                        }*//*else{
                            loadFavouriteMovies();
                        }*/
                    }
                });

                return searchQuery;
            }

            @Override
            public void deliverResult(String data) {
                mSorting = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        //loadDefaultMovies(data);
        if(data != null) {
            if(!mSortBy.equals(FAVOURITE)) {
                loadDefaultMovies(mSortBy);
            }else{
                loadFavouriteMovies();
            }
            Log.e(TAG + "-finished", data);
        }

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(SEARCH_QUERY, mSortBy);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mSortBy = savedInstanceState.getString(SEARCH_QUERY);
        Log.e(TAG + "-restoreInstance", mSortBy);
    }
}
