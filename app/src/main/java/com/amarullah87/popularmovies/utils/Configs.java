package com.amarullah87.popularmovies.utils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by apandhis on 29/06/17.
 */

public class Configs {

    public final static String POPULAR = "popular";
    public final static String TOP_RATED = "top_rated";
    public final static String UPCOMING = "upcoming";
    private static final String BASE_URL = "https://api.themoviedb.org/";
    public static Retrofit getDataAPI(){

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
