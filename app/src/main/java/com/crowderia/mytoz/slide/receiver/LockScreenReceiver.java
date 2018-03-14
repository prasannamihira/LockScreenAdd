package com.crowderia.mytoz.slide.receiver;

/**
 * Created by crowderia on 11/1/16.
 */

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import com.crowderia.mytoz.activity.LockScreen;
import com.crowderia.mytoz.util.CommonUtills;
import com.crowderia.mytoz.util.MytozConstant;

import static android.content.Context.ACTIVITY_SERVICE;

public class LockScreenReceiver extends BroadcastReceiver {

    private static final String TAG = "LockScreenReceiver";

    private Context cx;
    ActivityManager am;

    public LockScreenReceiver() {
        Log.d(TAG, "ScreenEventReceiver initialized");
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        String action = intent.getAction();

        // || action.equals(Intent.ACTION_SCREEN_ON) || action.equals(Intent.ACTION_SCREEN_OFF)

        //If the screen was just turned on or it just booted up, start your Lock Activity
        if(action.equals(Intent.ACTION_SCREEN_ON) || action.equals(Intent.ACTION_BOOT_COMPLETED))
        {
            Intent i = new Intent(context, LockScreen.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
        if (action.equals(Intent.ACTION_SCREEN_OFF)) {


            Log.e("Screen OFF ", "ACTION_SCREEN_OFF");

        }
    }
}
