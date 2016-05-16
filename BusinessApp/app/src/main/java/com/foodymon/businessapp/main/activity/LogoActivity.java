package com.foodymon.businessapp.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.foodymon.businessapp.R;
import com.foodymon.businessapp.datastructure.StoreStaff;
import com.foodymon.businessapp.main.BusinessApplication;
import com.foodymon.businessapp.service.LoginService;
import com.foodymon.businessapp.service.UICallBack;
import com.foodymon.businessapp.utils.Utils;

public class LogoActivity extends Activity {
    private BusinessApplication app;

    private static final String FOODYMON = "FOODYMON";
    private static final String USER_NAME = "FOODYMON_USER";
    private static final String STORE_ID = "FOODYMON_STORE_ID";
    private static final String PASSWROD = "FOODYMON_PASSWORD";

    LoginService mLoginService;


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
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean tryLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences(FOODYMON, MODE_PRIVATE);
        if (sharedPreferences != null) {
            String userName = sharedPreferences.getString(USER_NAME, "");
            String storeId = sharedPreferences.getString(STORE_ID, "");
            String password = sharedPreferences.getString(PASSWROD, "");
            if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(storeId)
                && !TextUtils.isEmpty(password)) {
                    mLoginService.authenticate(userName, storeId, password, new UICallBack<StoreStaff>() {
                        @Override
                        public void onPreExecute() {
                            // Do nothing.
                        }
                        @Override
                        public void onPostExecute(StoreStaff user)  {
                            Intent intent;
                            if (Utils.isValidUser(null)) {
                                startMainActivity();
                            } else {
                                startLoginActivity();
                            }
                        }
                    });
                return true;
            }
        }
        return false;
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

    private void startMainActivity() {
        Intent intent = new Intent(LogoActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}
