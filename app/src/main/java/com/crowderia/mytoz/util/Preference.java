package com.crowderia.mytoz.util;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by Crowderia on 11/3/2016.
 */

public class Preference {

    public static void savePreference(String key, String value, Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(key, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String showPreference(String key, Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(key, Activity.MODE_PRIVATE);
        String savedPref = sharedPreferences.getString(key, "");
        return savedPref;
    }
}
