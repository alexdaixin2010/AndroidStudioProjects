package com.foodymon.businessapp.main.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.foodymon.businessapp.R;
import com.foodymon.businessapp.constant.Constants;
import com.foodymon.businessapp.datastructure.LiteOrderList;
import com.foodymon.businessapp.datastructure.StoreStaff;
import com.foodymon.businessapp.main.BusinessApplication;
import com.foodymon.businessapp.service.LoginService;
import com.foodymon.businessapp.service.OrderService;
import com.foodymon.businessapp.service.UICallBack;
import com.foodymon.businessapp.utils.Utils;

public class LogoActivity extends Activity {
    private BusinessApplication app;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private boolean subscriptionDone = false;
    private boolean orderLoadingDone = false;
    private LiteOrderList liteOrderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        app = (BusinessApplication) getApplication();

        if (!tryLogin()) {
            startLoginActivity();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
            new IntentFilter(Constants.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onDestroy() {
        try {
            if (mRegistrationBroadcastReceiver != null)
                LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        } catch (Exception e) {

        }
        super.onDestroy();
    }

    private boolean tryLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_FOODYMON, MODE_PRIVATE);
        final String userName = sharedPreferences.getString(Constants.SHARED_PREFERENCE_USER_ID, "");
        final String storeId = sharedPreferences.getString(Constants.SHARED_PREFERENCE_STORE_ID, "");
        final String password = sharedPreferences.getString(Constants.SHARED_PREFERENCE_PASSWROD, "");
        final String token = sharedPreferences.getString(Constants.SHARED_PREFERENCE_TOKEN, "");
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(storeId)
            && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(token)) {

            LoginService.getUser(LogoActivity.this, storeId, userName, token, new UICallBack<StoreStaff>() {
                @Override
                public void onPreExecute() {
                    // Do nothing.
                }

                @Override
                public void onPostExecute(StoreStaff user) {
                    if (Utils.isValidUser(user)) {
                        app.setStoreId(storeId);
                        app.setUser(user);
                        app.setToken(token);
                        LoginService.registerGCM(LogoActivity.this, app.getOrderTopicHeader());
                        loadOrderList(storeId, userName);
                    } else {
                        authenticate(storeId, userName, password);
                    }
                }
            }, app);

            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    // checking for type intent filter
                    if (intent.getAction().equals(Constants.REGISTRATION_COMPLETE)) {
                        subscriptionDone = true;
                        maybeStartMainActivity();
                    }
                }
            };
            return true;
        }
        return false;
    }

    private void authenticate(final String storeId, final String userName, final String password) {
        LoginService.authenticate(LogoActivity.this, storeId, userName, password, new UICallBack<StoreStaff>() {
            @Override
            public void onPreExecute() {
                // Do nothing.
            }

            @Override
            public void onPostExecute(StoreStaff user) {
                if (Utils.isValidUser(user)) {
                    app.setStoreId(storeId);
                    app.setUser(user);
                    LoginService.registerGCM(LogoActivity.this, app.getOrderTopicHeader());
                    loadOrderList(storeId, userName);
                } else {
                    startLoginActivity();
                }
            }
        }, app);
    }

    private void startLoginActivity() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LogoActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);

    }

    private void loadOrderList(String storeId, String userId) {
        OrderService.getSubmittedLiteOrderList(storeId, new UICallBack<LiteOrderList>() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(LiteOrderList liteOrderList) {
                orderLoadingDone = true;
                LogoActivity.this.liteOrderList = liteOrderList;
                maybeStartMainActivity();
            }
        }, app);
    }

    void maybeStartMainActivity() {
        if (subscriptionDone && orderLoadingDone) {
            Intent intent = new Intent(LogoActivity.this, MainActivity.class);
            intent.putExtra(LiteOrderList.BUNDLE_NAME, liteOrderList);
            startActivity(intent);
            finish();
        }
    }

}
