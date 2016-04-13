package com.foodymon.businessapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;

import com.foodymon.businessapp.datastructure.StoreStaff;
import com.foodymon.businessapp.utils.HttpUtils;

import java.util.HashMap;

public class LoginServiceDeprecated extends Service {
    LoginServiceBinder mLoginServiceBinder;
    ILoginService mLoginService;

    public LoginServiceDeprecated() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLoginServiceBinder = new LoginServiceBinder();

    }


    @Override
    public IBinder onBind(Intent intent) {
        return mLoginServiceBinder;
    }

    protected static class LoginServiceBinder extends ILoginService.Stub {

        @Override
        public void authenticate(String storeId, String userName, String password,
                                 final ILoginCallBack callBack) {
            String[] params = new String[]{storeId, userName, password};
            new TaskRunner<String, String>(new Task<String, String>() {
                @Override
                public void onPreExecute() {
                    try {
                        callBack.preCallBack();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                @Nullable
                public String doInBackground(String[] params) {
                    return tryLogin(params[0], params[1], params[2]);
                }

                @Override
                public void onPostExecute(@Nullable String o) {
                    try {
                        callBack.postCallBack(TextUtils.isEmpty(o) ? null : new StoreStaff());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }
            }).execute(params);
        }

        @Nullable
        String tryLogin(String storeId, String userName, String password) {
            HashMap<String, String> property = new HashMap<String, String>();

            String auth = userName + ":" + password;
            String auth64 = Base64.encodeToString(auth.getBytes(), Base64.DEFAULT);
            property.put("Authorization", "Basic " + auth64);

            String user = HttpUtils.post("userprofile/up/storestaff/login/basic/" + storeId, "", property, String.class);

            return user != null ? user.toString(): null;
        }
    }
}
