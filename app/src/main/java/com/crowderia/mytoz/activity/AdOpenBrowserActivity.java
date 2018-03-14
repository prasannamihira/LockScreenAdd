package com.crowderia.mytoz.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.crowderia.mytoz.R;
import com.crowderia.mytoz.util.NetworkStatus;

public class AdOpenBrowserActivity extends BaseActivity {

    WebView adWebview;
    final Activity thisActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ad_open_browser);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String webUrl = getIntent().getStringExtra("url");
        Log.i("URL", webUrl);

        adWebview = (WebView) findViewById(R.id.ad_browser);
        adWebview.setWebViewClient(new MyBrowser());
        adWebview.getSettings().setLoadsImagesAutomatically(true);
        adWebview.getSettings().setJavaScriptEnabled(true);
        adWebview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        adWebview.loadUrl(webUrl);

        adWebview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                thisActivity.setProgress(progress * 1000);
            }
        });
        adWebview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(thisActivity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Don't hang around.
        finish();
    }

}
