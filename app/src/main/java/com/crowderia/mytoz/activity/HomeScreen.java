package com.crowderia.mytoz.activity;

import android.Manifest;
import android.app.ActionBar;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;

import com.crowderia.mytoz.R;
import com.crowderia.mytoz.adapter.TabAdapter;
import com.crowderia.mytoz.util.MytozConstant;
import com.crowderia.mytoz.util.NetworkStatus;

import java.util.List;

public class HomeScreen extends AppCompatActivity {

    Intent nextActivity;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.MODIFY_PHONE_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.READ_PHONE_STATE};

        if (!LockScreen.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }


        boolean isNetworkConnect = NetworkStatus.isInternetConnected(HomeScreen.this);
        if (!isNetworkConnect) {
            NetworkStatus.popupDataWifiEnableMessage(HomeScreen.this);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.fragment_home)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.fragment_interest)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.fragment_profile)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final TabAdapter adapter = new TabAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Intent it = new Intent(MytozConstant.ACTION_SERVICE_INTENT_START);
        it.setPackage(getPackageName());
        startService(it);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            /*case R.id.action_profile:
                nextActivity = new Intent(HomeScreen.this, ProfileScreen.class);
                nextActivity.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(nextActivity);
                return true;*/
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
