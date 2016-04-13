package com.foodymon.businessapp.service;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;

import com.foodymon.businessapp.datastructure.StoreStaff;
import com.foodymon.businessapp.utils.HttpUtils;

import java.util.HashMap;

/**
 * Service provides authenticate function which is running in NonUI thread.
 */
public class LoginService {

    public LoginService() {

    }

    public void authenticate(String storeId, String userName, String password,
                             final LoginCallBack callBack) {
        String[] params = new String[]{storeId, userName, password};
        new TaskRunner<String, String>(new Task<String, String>() {
            @Override
            public void onPreExecute() {
                callBack.onPreExecute();
            }

            @Override
            @Nullable
            public String doInBackground(String[] params) {
                return tryLogin(params[0], params[1], params[2]);
            }

            @Override
            public void onPostExecute(@Nullable String o) {
                callBack.onPostExecute(TextUtils.isEmpty(o) ? null : new StoreStaff());

            }
        }).execute(params);
    }

    @Nullable
    private String tryLogin(String storeId, String userName, String password) {
        HashMap<String, String> property = new HashMap<String, String>();

        String auth = userName + ":" + password;
        String auth64 = Base64.encodeToString(auth.getBytes(), Base64.DEFAULT);
        property.put("Authorization", "Basic " + auth64);
        String user = HttpUtils.post("userprofile/up/storestaff/login/basic/" + storeId, "", property, String.class);
        return user != null ? user.toString(): null;
    }

    public interface LoginCallBack {

        void onPreExecute();

        void onPostExecute(StoreStaff storeStaff);

    }
}
