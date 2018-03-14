package com.crowderia.mytoz.activity;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.crowderia.mytoz.R;
import com.crowderia.mytoz.db.DBHelper;
import com.crowderia.mytoz.model.CampaignModel;
import com.crowderia.mytoz.service.MytozApi;
import com.crowderia.mytoz.slide.OnSwipeTouchListener;
import com.crowderia.mytoz.slide.SlideButtonWeb;
import com.crowderia.mytoz.slide.SlideButtonWebListener;
import com.crowderia.mytoz.slide.service.LockScreenService;
import com.crowderia.mytoz.slide.SlideButtonListener;
import com.crowderia.mytoz.slide.SlideButtonLock;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import com.crowderia.mytoz.util.CommonUtills;
import com.crowderia.mytoz.util.MytozConstant;
import com.crowderia.mytoz.util.NetworkStatus;
import com.crowderia.mytoz.util.PhoneUtil;
import com.crowderia.mytoz.util.PicassoSingleton;
import com.crowderia.mytoz.util.Preference;
import com.jacksonandroidnetworking.JacksonParserFactory;
import com.splunk.mint.Mint;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import rx.Subscription;

public class LockScreen extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {

    TextView tvDate, tvTime;

    // Lock screen loading image
    ImageView imageLock;

    // Lock screen icons
    ImageView imageBookmark, imageHome, imageUnlock, imageWeb;

    private CoordinatorLayout coordinatorLayout;

    // CampaignModel array list
    ArrayList<CampaignModel> campaignModelsList;

    private boolean isDefaultImage;

    public boolean isDefaultImage() {
        return isDefaultImage;
    }

    public void setDefaultImage(boolean defaultImage) {
        isDefaultImage = defaultImage;
    }

    CampaignModel campaignModel;

    DBHelper db;

    private MytozApi mytozApi;

    // Json response tags
    private static final String TAG_LOGIN_USER = "user";
    private static final String TAG_LOGIN_TOKEN = "token";

    private Subscription mSubscription;

    // Check the permissions has enabled
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int PERMISSION_ALL = 1;

        // Permissions what application need
        String[] PERMISSIONS = {Manifest.permission.MODIFY_PHONE_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.READ_PHONE_STATE};

        // Popup allow permissions when app open
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        Mint.initAndStartSession(this.getApplication(), "5d92ade4");
//        Mint.initAndStartSession(this.getApplication(), "19058d45");

        //use keygen manager to unlock the device and acquire the app
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        // new database instance
        db = new DBHelper(LockScreen.this);

        // new campaugn model
        campaignModel = new CampaignModel();

        // Device network status
        boolean isNetworkConnect = NetworkStatus.isInternetConnected(LockScreen.this);
        if (!isNetworkConnect) {
            // Popup message for enable internet
            NetworkStatus.popupDataWifiEnableMessage(LockScreen.this);
        } else {
            // Login request
            requestLoginToken();
        }

        // make the full screen view
        makeFullScreen();

        // start lock screen service
        startService(new Intent(this, LockScreenService.class));

        setContentView(R.layout.activity_lock_screen);

        // get images from image views
        imageLock = (ImageView)findViewById(R.id.imageLockScreen);
        imageBookmark = (ImageView)findViewById(R.id.ivBookmark);
        imageHome = (ImageView)findViewById(R.id.ivHome);
        imageUnlock = (ImageView) findViewById(R.id.unlockButton);
        imageWeb = (ImageView) findViewById(R.id.browserButton);

        imageBookmark.setOnClickListener(this);
        imageHome.setOnClickListener(this);
        imageUnlock.setOnClickListener(this);
        imageWeb.setOnClickListener(this);

        imageBookmark.setVisibility(View.GONE);
        imageHome.setVisibility(View.GONE);
        imageUnlock.setVisibility(View.GONE);
        imageWeb.setVisibility(View.GONE);

        campaignModel.setCampaignWebUrl("http://www.mytoz.com/");
        this.setDefaultImage(true);

        // set campaign view touch listener
        imageOnTouchListener(true);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = MotionEventCompat.getActionMasked(event);

        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                Log.d("MotionEvent","Action was DOWN");

                return true;
            case (MotionEvent.ACTION_MOVE) :
                Log.d("MotionEvent","Action was MOVE");
                if(!isDefaultImage)
                    imageBookmark.setVisibility(View.VISIBLE);
                else
                    imageBookmark.setVisibility(View.GONE);

                imageHome.setVisibility(View.VISIBLE);
                imageUnlock.setVisibility(View.VISIBLE);
                imageWeb.setVisibility(View.VISIBLE);

                return true;
            case (MotionEvent.ACTION_UP) :
                Log.d("MotionEvent","Action was UP");

                Thread timer = new Thread() {
                    public void run(){
                        try {
                            sleep(4000);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageBookmark.setVisibility(View.GONE);
                                    imageHome.setVisibility(View.GONE);
                                    imageUnlock.setVisibility(View.GONE);
                                    imageWeb.setVisibility(View.GONE);
                               }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                timer.start();


                return true;
            case (MotionEvent.ACTION_CANCEL) :
                Log.d("MotionEvent","Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE) :
                Log.d("MotionEvent","Movement occurred outside bounds of current screen element");
                return true;
            default :
                return super.onTouchEvent(event);
        }


    }

    // request login token when user open app
    private void requestLoginToken(){
        try {
            JSONObject jsonObject = new JSONObject();
            try {
                String phoneImei = PhoneUtil.getPhoneImeiNumber(LockScreen.this);
                Log.i("phoneImei", phoneImei);

                jsonObject.put("clientsecret", MytozConstant.CLIENTSECRET);
                jsonObject.put("udid", phoneImei);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Interceptor interceptor = new Interceptor() {
                @Override
                public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
                    Request newRequest = chain.request().newBuilder().addHeader("Accept", "application/json").build();
                    return chain.proceed(newRequest);
                }
            };

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.interceptors().add(interceptor);
            OkHttpClient client = builder.build();

            AndroidNetworking.initialize(getApplicationContext(),client);

            // Then set the JacksonParserFactory like below
            AndroidNetworking.setParserFactory(new JacksonParserFactory());

            //authenticate | extratoken
            AndroidNetworking.post(MytozConstant.BASE_URL + "extratoken")
                    .addJSONObjectBody(jsonObject) // posting json
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // do anything with response
                            Log.i("onResponse", response.toString());
                            try {
                                Log.i("onResponse Token", response.getString(TAG_LOGIN_TOKEN).toString());
                                Preference.savePreference(getResources().getString(R.string.sp_request_token), response.getString(TAG_LOGIN_TOKEN).toString(), LockScreen.this);
                                getRandomCampaign();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onError(ANError error) {
                            // handle error
                            Log.i("onError", error.toString());

                            final int radius = 20;
                            final int margin = 20;

                            imageBookmark.setVisibility(View.GONE);
                            PicassoSingleton.getInstance(LockScreen.this).load(R.drawable.mytoz_banner)
                                    .transform(new RoundedCornersTransformation(radius, margin))
                                    .placeholder(R.drawable.mytoz_banner)
                                    .into(imageLock, new Callback() {
                                @Override
                                public void onSuccess() {
                                    Log.i("Picasso", "onSuccess");
                                }

                                @Override
                                public void onError() {
                                    Log.i("Picasso", "onError");
                                }
                            });
                            campaignModel.setCampaignWebUrl("http://www.mytoz.com/");
                            setDefaultImage(true);

                            imageOnTouchListener(true);
                        }
                    });

        } catch (Exception e) {
            Log.i("Ex", e.getMessage());
            e.printStackTrace();
        }
    }

    private void getRandomCampaign(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageBookmark.setVisibility(View.GONE);
                imageHome.setVisibility(View.GONE);
                imageUnlock.setVisibility(View.GONE);
                imageWeb.setVisibility(View.GONE);
            }
        });

        // create new array list for admodel
        campaignModelsList = new ArrayList<>();

        //JSON to be parse
        // {"categorys":[{"category":"AUIOS_VEHICLES"}, {"category":"BEAUTY_FITNESS"}]}
        try {
            JSONObject jsonObjectCategory = null;
            JSONArray catJsonArray = null;
            try {
                catJsonArray = new JSONArray();

                //get categories from database
                Cursor cursor = db.getAllCategories();

                if (cursor != null && cursor.getCount() > 0) {

                    Log.i("DATA",cursor.getCount()+"");

                    while (cursor.moveToNext()) {

                        String name = cursor.getString(cursor.getColumnIndex("name"));
                        String isSelected = cursor.getString(cursor.getColumnIndex("is_selected"));

                        Log.i("Categories from DB1 >>", name + " "
                                + isSelected);

                        if(isSelected.equalsIgnoreCase("1")){
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("category", name);
                            catJsonArray.put(jsonObject);
                        }
                    }
                    cursor.close();
                } else {
                    db.insertCategory("ARTS_ENTERTAINMENT", "1");
                    db.insertCategory("AUIOS_VEHICLES", "1");
                    db.insertCategory("BEAUTY_FITNESS", "1");
                    db.insertCategory("COMPUTERS_ELECTRONICS", "1");
                    db.insertCategory("FOOD_DRINKS", "1");
                    db.insertCategory("GAMES", "1");

                    Cursor cursor2 = db.getAllCategories();
                    while (cursor2.moveToNext()) {

                        String name = cursor2.getString(cursor2.getColumnIndex("name"));
                        String isSelected = cursor2.getString(cursor2.getColumnIndex("is_selected"));

                        Log.i("Categories from DB2 >>", name + " "
                                + isSelected);

                        if(isSelected.equalsIgnoreCase("1")){
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("category", name);
                            catJsonArray.put(jsonObject);
                        }
                    }
                    cursor2.close();
                }

                jsonObjectCategory = new JSONObject();
                jsonObjectCategory.put("categorys", catJsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.i("Json array created", catJsonArray.toString());

            //Service call
            Interceptor interceptor = new Interceptor() {
                @Override
                public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
                    String requestToken = Preference.showPreference(getResources().getString(R.string.sp_request_token), LockScreen.this);
                    Request newRequest = chain.request().newBuilder()
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .addHeader("authorization", "Bearer " + requestToken).build();
                    return chain.proceed(newRequest);
                }
            };

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.interceptors().add(interceptor);
            OkHttpClient client = builder.build();

            AndroidNetworking.initialize(LockScreen.this, client);

            // Then set the JacksonParserFactory like below
            AndroidNetworking.setParserFactory(new JacksonParserFactory());

            String arrayObject = catJsonArray.toString();

            AndroidNetworking.post(MytozConstant.BASE_URL + "randomcampaignforcategory")
                .addBodyParameter("categorys", arrayObject) // posting array object
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Random Campaign", response.toString());
                        try {

                            JSONObject explrObject = response;
                            JSONArray jsonArrayImages = new JSONArray(explrObject.getString("inAppImages"));
                            Log.i("jsonArrayImage", jsonArrayImages + "");
                            Log.i("jsonArrayImage.length()", jsonArrayImages.length() + "");

                            ArrayList<String> arraylist= new ArrayList<String>();
                            arraylist.addAll( Arrays.asList(jsonArrayImages.toString()));

                            for (int j = 0; j < jsonArrayImages.length(); j++) {

                                Log.i("List Item Image", arraylist.get(j));
                            }

                            //get lock screen image
                            JSONArray jsonArrayLockScreenImages = new JSONArray(explrObject.getString("loginScreenImages"));
                            Log.i("jsonArrayImage", jsonArrayLockScreenImages + "");
                            Log.i("jsonArrayImage.length()", jsonArrayLockScreenImages.length() + "");

                            ArrayList<String> arraylistLock= new ArrayList<String>();
                            arraylistLock.addAll( Arrays.asList(jsonArrayLockScreenImages.toString()));

                            for (int j = 0; j < jsonArrayLockScreenImages.length(); j++) {

                                Log.i("List Item Image", arraylistLock.get(j));
                            }

                            Log.i("List Item", arraylist.get(0).replace("\\", "") + " " + arraylistLock.get(0).replace("\\", "") + " " + explrObject.getString("name") + " " + explrObject.getString("category"));
                            campaignModelsList.add(new CampaignModel
                                    (explrObject.getString("_id")
                                    , arraylist.get(0).replace("\\", "")
                                    , arraylistLock.get(0).replace("\\", "")
                                    , explrObject.getString("name")
                                    , explrObject.getString("category")
                                    , (explrObject.getString("url")==null?"":explrObject.getString("url").replace("\\", ""))));

                            campaignModel.setCampaignId(explrObject.getString("_id"));
                            campaignModel.setCategory(explrObject.getString("category"));
                            campaignModel.setCampaignName(explrObject.getString("name"));
                            campaignModel.setImageUrl(arraylist.get(0).replace("\\", ""));
                            campaignModel.setImageUrlLockScreen(arraylistLock.get(0).replace("\\", ""));
                            campaignModel.setCampaignAddDate(explrObject.getString("createdAt"));
                            campaignModel.setCampaignWebUrl((explrObject.getString("url")==null?"":explrObject.getString("url").replace("\\", "")));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //set image which get from random campaign
                        final String imageUrl = campaignModel.getImageUrlLockScreen().replace("[", "").replace("]", "").replace("\"", "");
                        Log.i("Image Url", imageUrl);

                        //if(!isAirplaneModeOn(context.getApplicationContext())){
                        final int radius = 20;
                        final int margin = 20;
                        //imageBookmark.setVisibility(View.VISIBLE);
                        final Transformation transformation = new RoundedCornersTransformation(radius, margin);

                            PicassoSingleton.getInstance(LockScreen.this).load(imageUrl.toString())
                                    .transform(new RoundedCornersTransformation(radius, margin))
                                    .placeholder(R.drawable.mytoz_banner)
                                    .noFade()
                                    .into(imageLock, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.i("Picasso", "onSuccess");
                                        }

                                        @Override
                                        public void onError() {
                                            Log.i("Picasso", "onError");
                                        }
                                    });

                        setDefaultImage(false);

                        imageOnTouchListener(false);
                    }
                    @Override
                    public void onError(ANError anError) {
                        Log.i("Random Campaign onError", anError.toString());
                        // set default mytoz image campaign
                        final int radius = 20;
                        final int margin = 20;

                        imageBookmark.setVisibility(View.GONE);

                                PicassoSingleton.getInstance(LockScreen.this).load(R.drawable.mytoz_banner)
                                        .transform(new RoundedCornersTransformation(radius, margin))
                                        .placeholder(R.drawable.mytoz_banner)
                                        .noFade()
                                        .into(imageLock, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                Log.i("Picasso", "onSuccess");
                                            }

                                            @Override
                                            public void onError() {
                                                Log.i("Picasso", "onError");
                                            }
                                        });


                        campaignModel.setCampaignWebUrl("http://www.mytoz.com/");
                        setDefaultImage(true);
                        imageOnTouchListener(true);
                    }
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void imageOnTouchListener(final boolean isDefaultCampaign){

        imageLock.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(!isDefaultImage)
                    imageBookmark.setVisibility(View.VISIBLE);
                else
                    imageBookmark.setVisibility(View.GONE);

                imageHome.setVisibility(View.VISIBLE);
                imageUnlock.setVisibility(View.VISIBLE);
                imageWeb.setVisibility(View.VISIBLE);
                return true;
            }

        });

        imageLock.setOnTouchListener(new OnSwipeTouchListener(LockScreen.this) {

            public void onSwipeTop() {
                // start home screen
                boolean isNetworkConnect = NetworkStatus.isInternetConnected(LockScreen.this);
                if (!isNetworkConnect) {
                    NetworkStatus.popupDataWifiEnableMessage(LockScreen.this);
                } else {
                    openHomeScreen();
                }

            }
            public void onSwipeRight() {

                imageUnlock = (ImageView) findViewById(R.id.unlockButton);
                // Scale up the unlock icon
                CommonUtills.imageAnimationScaleUp(imageUnlock);

                Thread timer = new Thread() {
                    public void run(){
                        try {
                            sleep(500);
                            // Unlock screen
                            unlockScreen();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                timer.start();
            }
            public void onSwipeLeft() {
                // Open web screen
                openCampaignInBrowser();

            }

            public void onSwipeBottom() {

                if(!isDefaultCampaign) {
                    // campaign save in bookmarked list
                    bookmarkCampaign();
                }
            }

        });
    }

    public void bookmarkCampaign() {

            imageBookmark = (ImageView)findViewById(R.id.ivBookmark);

            CommonUtills.imageAnimationScaleUp(imageBookmark);
            coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

            Thread timer = new Thread() {
                public void run(){
                    try {
                        sleep(500);
                        int rows = getCampaignByCampaignId(campaignModel.getCampaignId());
                        Log.i("Campaign isInterested", rows + "");
                        if (rows == 0) {
                            // Add to interest
                            insertCampaignToInterestDb();
                            CommonUtills.showSnackBarMessage("Bookmarked successfully", coordinatorLayout);

                            sleep(1500);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CommonUtills.imageAnimationScaleDown(imageBookmark);
                                    // Unlock screen
                                    unlockScreen();
                                }
                            });
                            //getRandomCampaign();

                        } else {
                            CommonUtills.showSnackBarMessage("Already bookmarked", coordinatorLayout);
                            sleep(1500);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CommonUtills.imageAnimationScaleDown(imageBookmark);
                                    // Unlock screen
                                    unlockScreen();
                                }
                            });
                            //getRandomCampaign();
                        }

                    } catch (InterruptedException e) {
                        //CommonUtills.showToastMessage(LockScreen.this, "Bookmarked failed");
                        CommonUtills.showSnackBarMessage("Bookmarked failed", coordinatorLayout);
                        // Unlock screen
                        unlockScreen();
                        e.printStackTrace();
                    }
                }
            };
            timer.start();
    }

    public void openCampaignInBrowser() {
        // Open web screen
        // Scale up the web icon

        boolean isNetworkConnect = NetworkStatus.isInternetConnected(LockScreen.this);
        if (!isNetworkConnect) {
            NetworkStatus.popupDataWifiEnableMessage(LockScreen.this);
        } else {
            imageWeb = (ImageView)findViewById(R.id.browserButton);

            CommonUtills.imageAnimationScaleUp(imageWeb);

            Thread timer = new Thread() {
                public void run() {
                    try {
                        sleep(500);
                        if (campaignModel.getCampaignWebUrl() != null) {
                            webScreen(campaignModel.getCampaignWebUrl().toString());
                            Log.i("Web Url", campaignModel.getCampaignWebUrl().toString());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            timer.start();

        }

    }

    public void openHomeScreen() {
        imageHome = (ImageView)findViewById(R.id.ivHome);

        CommonUtills.imageAnimationScaleUp(imageHome);

        Thread timer = new Thread() {
            public void run(){
                try {
                    sleep(500);
                    Intent homeIntent = new Intent(LockScreen.this, HomeScreen.class);
                    startActivity(homeIntent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        timer.start();
    }

    private boolean insertCampaignToInterestDb(){

        String campaign_id = campaignModel.getCampaignId();
        String name = campaignModel.getCampaignName();
        String category = campaignModel.getCategory();
        String imageUrl = campaignModel.getImageUrl().replace("[", "").replace("]", "").replace("\"", "");
        String imageUrlLock = campaignModel.getImageUrlLockScreen().replace("[", "").replace("]", "").replace("\"", "");
        String insertDate = campaignModel.getCampaignAddDate();
        String webUrl = campaignModel.getCampaignWebUrl();

        Log.i("campaign_id", campaign_id);
        Log.i("name", name);
        Log.i("category", category);
        Log.i("app image url", imageUrl);
        Log.i("lock image url", imageUrlLock);
        Log.i("insertDate", insertDate);
        Log.i("webUrl", webUrl);

        db.insertInterestCampaign(campaign_id, name, category, imageUrl, imageUrlLock, insertDate,webUrl);

        return false;
    }

    private int getCampaignByCampaignId(String campaignId){
        return db.getCampaignById(campaignId);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        return false;
    }
    /**
     * Sets the screen to fullscreen.  It removes the Notifications bar,
     *   the Actionbar and the virtual keys (if they are on the phone)
     */
    public void makeFullScreen() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(Build.VERSION.SDK_INT < 19) { //View.SYSTEM_UI_FLAG_IMMERSIVE is only on API 19+
            this.getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        } else {
            this.getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Don't hang around.
        finish();
    }

    public void unlockScreen() {
        virbate();

        // this totally destroys the process
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public void webScreen(String webUrl) {
        Intent webIntent = new Intent(this, AdOpenBrowserActivity.class);
        webIntent.putExtra("url", webUrl);
        startActivity(webIntent);
        finish();
    }

    @Override
    public void onAttachedToWindow() {
        /*Window window = getWindow();

        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        super.onAttachedToWindow();
    }

    // check the back button events
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
                || (keyCode == KeyEvent.KEYCODE_POWER)
                || (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
                || (keyCode == KeyEvent.KEYCODE_CAMERA)) {
            return true;
        }
        if ((keyCode == KeyEvent.KEYCODE_HOME)) {

            return true;
        }
        return false;
    }

    // handle the key press events here itself
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP
                || (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)
                || (event.getKeyCode() == KeyEvent.KEYCODE_POWER)) {
            return false;
        }
        if ((event.getKeyCode() == KeyEvent.KEYCODE_HOME)) {

            return true;
        }
        return false;
    }

    private void virbate() {
        Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.unlockButton:
                unlockScreen();
                break;
            case R.id.browserButton:
                openCampaignInBrowser();
                break;
            case R.id.ivHome:
                openHomeScreen();
                break;
            case R.id.ivBookmark:
                bookmarkCampaign();
                break;
            default:
                break;
        }
    }
}
