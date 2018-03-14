package com.crowderia.mytoz.service.model.request;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Crowderia on 11/4/2016.
 */

public class UserLoginRequest {

    @SerializedName("username")
    public String username;

    @SerializedName("password")
    public String password;

    public UserLoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
