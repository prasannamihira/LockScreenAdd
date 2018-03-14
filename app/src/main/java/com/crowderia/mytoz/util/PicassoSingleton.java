package com.crowderia.mytoz.util;

import android.content.Context;

import com.squareup.picasso.Picasso;

/**
 * Created by Crowderia on 11/9/2016.
 */

public class PicassoSingleton {
    private static Picasso instance;

    public static Picasso getInstance(Context context) {
        if (instance == null) {
            instance = new Picasso.Builder(context.getApplicationContext()).build();
        }
        return instance;
    }

    private PicassoSingleton() {
        throw new AssertionError("No instances.");
    }
}
