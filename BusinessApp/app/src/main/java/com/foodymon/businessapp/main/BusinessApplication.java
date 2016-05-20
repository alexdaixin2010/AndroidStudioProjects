package com.foodymon.businessapp.main;

import android.app.Application;

import com.foodymon.businessapp.datastructure.StoreStaff;
import com.foodymon.businessapp.service.LoginService;

/**
 * Created by alexdai on 4/7/16.
 */
public class BusinessApplication extends Application {
    private StoreStaff user;
    private String storeId;
    private String token;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public void setUser(StoreStaff user) {
        this.user = user;
    }

    public StoreStaff getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
