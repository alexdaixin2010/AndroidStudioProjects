package com.foodymon.businessapp.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.foodymon.businessapp.R;
import com.foodymon.businessapp.main.BusinessApplication;
import com.foodymon.businessapp.datastructure.StoreStaff;
import com.foodymon.businessapp.service.LoginService;
import com.foodymon.businessapp.utils.Utils;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity{
    BusinessApplication app;
    LoginService mLoginService;
    private TextView user;
    private TextView store;
    private TextView password;
    private TextView invalid;
    private Button loginButton;
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        app = (BusinessApplication) getApplication();

        mLoginService = app.getLoginService();

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
                String userName = String.valueOf(user.getText());
                String storeId = String.valueOf(store.getText());
                String pw = String.valueOf(password.getText());
                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(storeId)
                    || TextUtils.isEmpty(pw)) {
                    return;
                }

                mLoginService.authenticate(storeId, userName, pw, new LoginService.LoginCallBack() {
                    @Override
                    public void onPreExecute() {
                        loading.setVisibility(View.VISIBLE);
                        loginButton.setEnabled(false);
                    }

                    @Override
                    public void onPostExecute(@Nullable StoreStaff user) {
                        loading.setVisibility(View.INVISIBLE);
                        loginButton.setEnabled(true);
                        if (Utils.isValidUser(user)) {
                            startMainActivity();
                        } else {
                            invalid.setVisibility(View.VISIBLE);
                        }
                    }

                });
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    void startMainActivity () {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}

