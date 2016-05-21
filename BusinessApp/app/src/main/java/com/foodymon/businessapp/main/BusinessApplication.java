package com.foodymon.businessapp.main;

import android.app.Application;
import android.content.Context;

import com.foodymon.businessapp.datastructure.StoreStaff;
//import com.squareup.leakcanary.LeakCanary;
//import com.squareup.leakcanary.RefWatcher;

/**
 * Created by alexdai on 4/7/16.
 */
public class BusinessApplication extends Application {
    private StoreStaff user;
    private String storeId;
    private String token;

//    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
  //      refWatcher = LeakCanary.install(this);
    }
/*
    public static RefWatcher getRefWatcher(Context context) {
        BusinessApplication application = (BusinessApplication) context.getApplicationContext();
        return application.refWatcher;
    }
*/
    public String getStoreId() {
        return storeId;
    }

    public String getOrderTopicHeader() {
        return storeId+"-order";
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
