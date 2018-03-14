package com.crowderia.mytoz.service;

import com.crowderia.mytoz.service.model.response.CampaignResponse;
import com.crowderia.mytoz.service.model.response.UserLoginResponse;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Crowderia on 11/3/2016.
 */

public interface MytozApi {

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "Accept-Charset: utf-8"
    })

    @POST("/authenticate")
    boolean loginUser(@Body JsonObject bean, retrofit2.Callback<JsonObject> objectCallback);

    @Headers("Content-Type: application/json")
    @POST("/authenticate")
    Observable<UserLoginResponse> userLogin(@Body JSONObject bean);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @GET("/randomcampaignforcategory")
    Observable<List<CampaignResponse>> getRandomCampaign();
}
