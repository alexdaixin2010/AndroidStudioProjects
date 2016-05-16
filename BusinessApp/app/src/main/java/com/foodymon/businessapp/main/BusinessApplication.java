package com.foodymon.businessapp.main;

import android.app.Application;

import com.foodymon.businessapp.service.LoginService;

/**
 * Created by alexdai on 4/7/16.
 */
public class BusinessApplication extends Application {
    private String mUserId;
    private String storeId;
    private LoginService mLoginService;

    @Override
    public void onCreate() {
        super.onCreate();
        //Init
        mLoginService = new LoginService();

    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public LoginService getLoginService() {
        return mLoginService;
    }


    public void setUserId(String userId) {
        this.mUserId = userId;
    }

    public String getUserId() {
        return mUserId;
    }

}
