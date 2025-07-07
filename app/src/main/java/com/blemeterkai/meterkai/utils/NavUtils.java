package com.blemeterkai.meterkai.utils;

import android.app.Activity;
import android.util.Log;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.blemeterkai.meterkai.R; // Import your R file

public class NavUtils {

    private static final String TAG = "NavigationUtils"; // Optional: for logging

    public static boolean handleOnSupportNavigateUp(Activity activity, AppBarConfiguration appBarConfiguration) {
        Log.i(TAG, "handleOnSupportNavigateUp() called");
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || activity.onNavigateUp(); // Call activity's super method
    }
}
