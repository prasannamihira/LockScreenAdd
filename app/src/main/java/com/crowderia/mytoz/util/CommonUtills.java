package com.crowderia.mytoz.util;

/**
 * Created by Crowderia on 11/8/2016.
 */

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crowderia.mytoz.R;
import com.crowderia.mytoz.activity.HomeScreen;

import java.util.List;

public class CommonUtills {

    public static void showAlertWarnMessage(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.setTitle(title);
        builder.setMessage("\n" + message);
        builder.setIcon(R.drawable.warning);

        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

        wmlp.gravity = Gravity.CENTER;
        wmlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialog.show();
    }

    public static void showAlertSuccessMessage(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.setTitle(title);
        builder.setMessage("\n" + message);
        builder.setIcon(R.drawable.success);

        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

        wmlp.gravity = Gravity.CENTER;
        wmlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialog.show();
    }

    public static void showToastMessage(final Context context, final String message) {
        ((Activity)context).runOnUiThread(new Runnable()
        {
            public void run()
            {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void showSnackBarMessage(String message, CoordinatorLayout coordinatorLayout) {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    public static void killAppBypackage(Context context){


        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String myPackage = context.getPackageName();

        mActivityManager.killBackgroundProcesses(myPackage);
    }

    public static void imageAnimationScaleUp(ImageView imageView) {
        AnimatorTracker tracker = new AnimatorTracker(); // or make it a global reference
        AnimatorSet set = new AnimatorSet();

        ValueAnimator v1 = ObjectAnimator.ofFloat(imageView, "scaleX", 1.5f);
        ValueAnimator v2 = ObjectAnimator.ofFloat(imageView, "scaleY", 1.5f);

        set.addListener(tracker);
        set.setDuration(500);
        set.play(v1).with(v2);
        set.start();

    }

    public static void imageAnimationScaleDown(ImageView imageView) {
        AnimatorTracker tracker = new AnimatorTracker(); // or make it a global reference

        AnimatorSet reverseSet = new AnimatorSet();

        ValueAnimator reverseV1 = ObjectAnimator.ofFloat(imageView, "scaleX", 1.0f);
        ValueAnimator reverseV2 = ObjectAnimator.ofFloat(imageView, "scaleY", 1.0f);

        reverseSet.addListener(tracker);
        reverseSet.setDuration(500);
        reverseSet.play(reverseV1).with(reverseV2);
        reverseSet.start();
    }

}
