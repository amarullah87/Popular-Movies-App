package com.amarullah87.popularmovies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amarullah87.popularmovies.adapters.ReviewAdapter;
import com.amarullah87.popularmovies.adapters.TrailerAdapter;
import com.amarullah87.popularmovies.models.Review;
import com.amarullah87.popularmovies.models.Reviews;
import com.amarullah87.popularmovies.models.Trailer;
import com.amarullah87.popularmovies.models.Trailers;
import com.amarullah87.popularmovies.utils.InternetConnection;
import com.amarullah87.popularmovies.utils.RestAPI;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.amarullah87.popularmovies.utils.Configs.getDataAPI;

/**
 * Created by apandhis on 01/07/17.
 */

public class DetailsMovieActivity extends AppCompatActivity{

    @BindView(R.id.moviePoster) ImageView moviePoster;
    @BindView(R.id.movieTitle) TextView movieTitle;
    @BindView(R.id.btnWatch) Button btnWatch;
    @BindView(R.id.movieRating) TextView movieRating;
    @BindView(R.id.movieReleaseDate) TextView movieReleaseDate;
    @BindView(R.id.movieOverview) TextView movieOverview;
    @BindView(R.id.rvTrailer) RecyclerView rvTrailer;
    @BindView(R.id.rvReview) RecyclerView rvReview;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.toolbar_layout) CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.app_bar) AppBarLayout appBarLayout;
    @BindView(R.id.imgHeader) ImageView imgHeader;

    private List<Trailer> trailers;
    private List<Review> reviews;
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;

    private String idMovie, average, title, poster, backdrop, overview, releaseDate;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail_layout);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.movie_detail);
        }
        initViews();
        initCollapsingToolbar();
        loadTrailersAndReviews();

        rvTrailer.addOnItemTouchListener(new TrailerAdapter.RecyclerTouchListener(getApplicationContext(),
                rvTrailer, new TrailerAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Trailer item = trailers.get(position);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(item.getTrailerUrl())));
            }
        }));

        btnWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailers.get(0).getTrailerUrl())));
            }
        });
    }

    private void initCollapsingToolbar() {
        collapsingToolbar.setTitle(title);
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(title);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(title);
                    isShow = false;
                }
            }
        });
    }

    private void initViews() {

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null) {
            idMovie = extras.getString("idMovie");
            average = extras.getString("average");
            title = extras.getString("title");
            poster = extras.getString("poster");
            backdrop = extras.getString("backdrop");
            overview = extras.getString("overview");
            releaseDate = extras.getString("releaseDate");
        }

        movieTitle.setText(title);
        Picasso.with(this)
                .load("http://image.tmdb.org/t/p/w185" + backdrop)
                .placeholder(R.drawable.ic_loading)
                .error(R.mipmap.ic_launcher)
                .into(imgHeader);

        Picasso.with(this)
                .load("http://image.tmdb.org/t/p/w185" + poster)
                .config(Bitmap.Config.RGB_565)
                .placeholder(R.drawable.ic_loading)
                .error(R.mipmap.ic_launcher)
                .into(moviePoster);
        String rating = getResources().getString(R.string.movie_rating, average);
        movieRating.setText(rating);
        movieReleaseDate.setText(releaseDate);
        String inputPattern = "yyyy-MM-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.US);
        try {
            Date date = inputFormat.parse(releaseDate);
            movieReleaseDate.setText(DateFormat.getDateInstance().format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        movieOverview.setText(overview);
    }

    private void loadTrailersAndReviews() {
        if(InternetConnection.checkConnection(getApplicationContext())){

            progressBar.setVisibility(View.VISIBLE);
            RestAPI restAPI = getDataAPI().create(RestAPI.class);

            Call<Trailers> trailersCall = restAPI.getTrailers(Long.parseLong(idMovie),
                    BuildConfig.MOVIEDB_API_KEY);
            trailersCall.enqueue(new Callback<Trailers>() {
                @Override
                public void onResponse(Call<Trailers> call, Response<Trailers> response) {
                    if(response.isSuccessful() && response.code() != 400) {
                        if (response.body() != null) {
                            trailers = response.body().getResults();
                            trailerAdapter = new TrailerAdapter(getApplicationContext(), trailers);

                            LinearLayoutManager horizontalLayout = new LinearLayoutManager(
                                    getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                            rvTrailer.setLayoutManager(horizontalLayout);
                            rvTrailer.setNestedScrollingEnabled(false);
                            rvTrailer.setItemAnimator(new DefaultItemAnimator());
                            rvTrailer.setAdapter(trailerAdapter);

                        } else {
                            Toast.makeText(getApplicationContext(), R.string.no_data, Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(DetailsMovieActivity.this, R.string.oops_something_wrong, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Trailers> call, Throwable t) {

                }
            });

            Call<Reviews> reviewsCall = restAPI.getReviews(Long.parseLong(idMovie), BuildConfig.MOVIEDB_API_KEY);
            reviewsCall.enqueue(new Callback<Reviews>() {
                @Override
                public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                    Log.e("Respon Viewer: ", String.valueOf(response.code()));
                    if(response.isSuccessful() && response.code() != 400) {
                        if (response.body() != null) {
                            reviews = response.body().getResults();
                            reviewAdapter = new ReviewAdapter(getApplicationContext(), reviews);

                            LinearLayoutManager verticalLayout = new LinearLayoutManager(
                                    getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                            rvReview.setLayoutManager(verticalLayout);
                            rvReview.setItemAnimator(new DefaultItemAnimator());
                            rvReview.setAdapter(reviewAdapter);
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.no_data, Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(DetailsMovieActivity.this, R.string.oops_something_wrong, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Reviews> call, Throwable t) {

                }
            });
            progressBar.setVisibility(View.GONE);
        }else{
            Toast.makeText(this, R.string.oops_network, Toast.LENGTH_LONG).show();
        }
    }
}
