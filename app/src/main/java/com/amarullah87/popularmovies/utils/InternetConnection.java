package com.amarullah87.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;

/**
 * Created by amarullah87 on 08/03/17.
 */

public class InternetConnection {

    public static boolean checkConnection(@NonNull Context context) {
        return ((ConnectivityManager) context.getSystemService
                (Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }
}
