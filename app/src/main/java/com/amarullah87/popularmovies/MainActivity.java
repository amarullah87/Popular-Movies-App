package com.amarullah87.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amarullah87.popularmovies.adapters.MovieAdapter;
import com.amarullah87.popularmovies.models.Movie;
import com.amarullah87.popularmovies.models.Movies;
import com.amarullah87.popularmovies.utils.InternetConnection;
import com.amarullah87.popularmovies.utils.RestAPI;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.amarullah87.popularmovies.utils.Configs.POPULAR;
import static com.amarullah87.popularmovies.utils.Configs.TOP_RATED;
import static com.amarullah87.popularmovies.utils.Configs.UPCOMING;
import static com.amarullah87.popularmovies.utils.Configs.getDataAPI;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rvMovies) RecyclerView rvMovies;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    private List<Movie> movies;
    private MovieAdapter adapter;

    private String mSortBy = POPULAR;
    int cols = 2;

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
    }

    private void loadDefaultMovies(String sortType) {
        progressBar.setVisibility(View.VISIBLE);
        rvMovies.setVisibility(View.GONE);

        //I created a Class to Check Network State Called "Internet Connection"
        //Before the retrofit Launch, it will check the network First (Wifi/ Data)
        if(InternetConnection.checkConnection(getApplicationContext())){

            RestAPI restAPI = getDataAPI().create(RestAPI.class);
            Call<Movies> call = restAPI.getMovies(sortType, BuildConfig.MOVIEDB_API_KEY);

            call.enqueue(new Callback<Movies>() {
                @Override
                public void onResponse(Call<Movies> call, Response<Movies> response) {
                    progressBar.setVisibility(View.GONE);
                    rvMovies.setVisibility(View.VISIBLE);
                    
                    if(response.body().getResults() != null){
                        movies = response.body().getResults();
                        adapter = new MovieAdapter(getApplicationContext(), movies);

                        RecyclerView.LayoutManager manager = new GridLayoutManager(getApplicationContext(), cols);
                        rvMovies.setLayoutManager(manager);
                        rvMovies.setNestedScrollingEnabled(false);
                        rvMovies.setItemAnimator(new DefaultItemAnimator());
                        rvMovies.setAdapter(adapter);
                    }else{
                        Toast.makeText(MainActivity.this, R.string.no_data, Toast.LENGTH_SHORT).show();
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
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.sort_by_most_popular:
                if(!movies.isEmpty()) {
                    movies.clear();
                }
                loadDefaultMovies(POPULAR);
                mSortBy = POPULAR;
                item.setChecked(true);
                break;

            case R.id.sort_by_top_rated:
                if(!movies.isEmpty()) {
                    movies.clear();
                }
                loadDefaultMovies(TOP_RATED);
                mSortBy = TOP_RATED;
                item.setChecked(true);
                break;

            case R.id.sort_by_upcoming:
                if(!movies.isEmpty()) {
                    movies.clear();
                }
                loadDefaultMovies(UPCOMING);
                mSortBy = UPCOMING;
                item.setChecked(true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
