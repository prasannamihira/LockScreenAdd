package com.crowderia.mytoz.model;

import java.io.Serializable;

/**
 * Created by Crowderia on 11/16/2016.
 */

public class ProfileModel implements Serializable {
    String name;
    String phone;
    String email;
    String dob;
    String sex;

    public ProfileModel() {
    }

    public ProfileModel(String name, String phone, String email, String dob, String sex) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.dob = dob;
        this.sex = sex;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
