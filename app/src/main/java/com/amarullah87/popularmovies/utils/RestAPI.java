package com.amarullah87.popularmovies.utils;

import com.amarullah87.popularmovies.models.Movies;
import com.amarullah87.popularmovies.models.Reviews;
import com.amarullah87.popularmovies.models.Trailers;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by apandhis on 29/06/17.
 */

public interface RestAPI {

    @GET("3/movie/{sort_by}")
    Call<Movies> getMovies(@Path("sort_by") String sortBy, @Query("api_key") String apiKey );

    @GET("3/movie/{id}/videos")
    Call<Trailers> getTrailers(@Path("id") long movieId, @Query("api_key") String apiKey);

    @GET("3/movie/{id}/reviews")
    Call<Reviews> getReviews(@Path("id") long movieId, @Query("api_key") String apiKey);
}
