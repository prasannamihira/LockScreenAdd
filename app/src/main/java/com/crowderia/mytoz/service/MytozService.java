package com.crowderia.mytoz.service;

import android.util.Log;

import com.crowderia.mytoz.service.model.response.CampaignResponse;
import com.crowderia.mytoz.service.model.response.UserLoginResponse;
import com.crowderia.mytoz.util.MytozConstant;

import org.json.JSONObject;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.strategy.Strategy;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by Crowderia on 11/3/2016.
 */

public class MytozService {

    private static MytozService mytozService;
    private MytozApi mytozApi;

    private MytozService() {

        Strategy strategy = new AnnotationStrategy();

        // Define the interceptor, add authentication headers
        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder().addHeader("Content-Type", "application/json").build();
                return chain.proceed(newRequest);
            }
        };

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(interceptor);
        OkHttpClient client = builder.build();

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);
        builder.build();

        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.create();
        //RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MytozConstant.BASE_URL)
                //.addConverterFactory(SimpleXmlConverterFactory.create(serializer))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .client(client)
                .build();

        try {
            this.mytozApi = retrofit.create(MytozApi.class);
        } catch (Exception ex) {
            Log.e("Exception", ex.toString());
        }
    }

    public static MytozService getInstance() {
        if (mytozService == null) {
            mytozService = new MytozService();
        }
        return mytozService;
    }

    public Observable<UserLoginResponse> userLogin(JSONObject bean) {
        return this.mytozApi.userLogin(bean);
    }

    public Observable<List<CampaignResponse>> getRandomCampaign(){
        return this.mytozApi.getRandomCampaign();
    }
}
