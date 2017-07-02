package com.amarullah87.popularmovies.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by apandhis on 02/07/17.
 */

public class Reviews {
    @SerializedName("results")
    private List<Review> results;

    public List<Review> getResults() {
        return results;
    }

    public void setResults(List<Review> results) {
        this.results = results;
    }
}
