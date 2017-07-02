package com.amarullah87.popularmovies.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by apandhis on 02/07/17.
 */

public class Trailers {
    @SerializedName("results")
    private List<Trailer> results;

    public List<Trailer> getResults() {
        return results;
    }

    public void setResults(List<Trailer> results) {
        this.results = results;
    }
}
