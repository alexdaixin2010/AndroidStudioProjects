package com.foodymon.businessapp.main.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.foodymon.businessapp.R;
import com.foodymon.businessapp.constant.Constants;
import com.foodymon.businessapp.datastructure.LiteOrderList;
import com.foodymon.businessapp.main.BusinessApplication;
import com.foodymon.businessapp.datastructure.StoreStaff;
import com.foodymon.businessapp.service.LoginService;
import com.foodymon.businessapp.service.OrderService;
import com.foodymon.businessapp.service.UICallBack;
import com.foodymon.businessapp.utils.Utils;

import android.support.v4.content.LocalBroadcastManager;



/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity{
    BusinessApplication app;
    private TextView user;
    private TextView store;
    private TextView password;
    private TextView invalid;
    private Button loginButton;
    private ProgressBar loading;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private boolean subscriptionDone = false;
    private boolean orderLoadingDone = false;
    private LiteOrderList liteOrderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        app = (BusinessApplication) getApplication();

        user = (TextView) findViewById(R.id.login_user_id);
        store = (TextView) findViewById(R.id.login_store_id);
        password = (TextView) findViewById(R.id.login_password);
        invalid = (TextView) findViewById(R.id.login_invalid);
        loading = (ProgressBar) findViewById(R.id.login_loading);

        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invalid.setVisibility(View.INVISIBLE);
                final String userName = String.valueOf(user.getText());
                final String storeId = String.valueOf(store.getText());
                String pw = String.valueOf(password.getText());
                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(storeId)
                    || TextUtils.isEmpty(pw)) {
                    return;
                }

                LoginService.authenticate(LoginActivity.this, storeId, userName, pw, new UICallBack<StoreStaff>() {
                    @Override
                    public void onPreExecute() {
                        loading.setVisibility(View.VISIBLE);
                        loginButton.setEnabled(false);
                    }

                    @Override
                    public void onPostExecute(@Nullable StoreStaff user) {
                        loginButton.setEnabled(true);
                        if (Utils.isValidUser(user)) {
                            app.setStoreId(storeId);
                            app.setUser(user);
                            LoginService.registerGCM(LoginActivity.this, app.getOrderTopicHeader());
                            loadOrderList(storeId, userName);
                        } else {
                            invalid.setVisibility(View.VISIBLE);
                            loading.setVisibility(View.INVISIBLE);
                        }
                    }

                }, app);
            }
        });


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
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
            new IntentFilter(Constants.REGISTRATION_COMPLETE));
    }

    private void loadOrderList(String storeId, String userId) {
        OrderService.getSubmittedLiteOrderList(storeId, new UICallBack<LiteOrderList>() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(LiteOrderList liteOrderList) {
                orderLoadingDone = true;
                LoginActivity.this.liteOrderList = liteOrderList;
                maybeStartMainActivity();
            }
        }, app);
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

    void maybeStartMainActivity () {
        if(subscriptionDone && orderLoadingDone) {
            loading.setVisibility(View.INVISIBLE);

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra(LiteOrderList.BUNDLE_NAME, liteOrderList);
            startActivity(intent);
            finish();
        }
    }

}

