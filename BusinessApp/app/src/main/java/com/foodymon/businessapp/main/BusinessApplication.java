package com.foodymon.businessapp.main;

import android.app.Application;
import android.os.RemoteException;

import com.foodymon.businessapp.service.ILoginService;
import com.foodymon.businessapp.service.LoginService;

/**
 * Created by alexdai on 4/7/16.
 */
public class BusinessApplication extends Application {
    private String mUserId;
    private LoginService mLoginService;

    @Override
    public void onCreate() {
        super.onCreate();
        //Init
        mLoginService = new LoginService();

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
