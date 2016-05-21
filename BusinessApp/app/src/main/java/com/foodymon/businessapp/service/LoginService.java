package com.foodymon.businessapp.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.widget.Toast;

import com.foodymon.businessapp.constant.Constants;
import com.foodymon.businessapp.datastructure.StoreStaff;
import com.foodymon.businessapp.main.BusinessApplication;
import com.foodymon.businessapp.utils.HttpUtils;
import com.foodymon.businessapp.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service provides authenticate function which is running in NonUI thread.
 */
public class LoginService {

    public static void authenticate(final Context context, final String storeId, final String userName, final String password,
                                    final UICallBack<StoreStaff> callBack, final BusinessApplication app) {
        new TaskRunner<String, StoreStaff>(new Task<String, StoreStaff>() {
            @Override
            public void onPreExecute() {
                callBack.onPreExecute();
            }

            @Override
            @Nullable
            public StoreStaff doInBackground(String[] params) {
                return tryLogin(context, storeId, userName, password, app);
            }

            @Override
            public void onPostExecute(@Nullable StoreStaff o) {
                callBack.onPostExecute(o);

            }
        }).execute(null);
    }

    @Nullable
    private static StoreStaff tryLogin(Context context, String storeId, String userName, String password, final BusinessApplication app) {
        String auth = userName + ":" + password;
        String auth64 = Base64.encodeToString(auth.getBytes(), Base64.DEFAULT);
        Map<String,String> property = new HashMap<>();
        property.put("Authorization", "Basic " + auth64);
        Map<String, List<String>> responseHeader = new HashMap<>();
        StoreStaff user = HttpUtils.post("/storestaff/login/" + storeId, null, property, responseHeader,
            new byte[0], StoreStaff.class, app);

        if(user != null) {
            // set token
            String token = responseHeader.get("Authorization").get(0);
            app.setToken(token);
            SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHARED_PREFERENCE_FOODYMON, context.MODE_PRIVATE).edit();
            editor.putString(Constants.SHARED_PREFERENCE_USER_ID, userName);
            editor.putString(Constants.SHARED_PREFERENCE_STORE_ID, storeId);
            editor.putString(Constants.SHARED_PREFERENCE_PASSWROD, password);
            editor.putString(Constants.SHARED_PREFERENCE_TOKEN, token);
            editor.apply();
        }


        return user != null ? user : null;
    }

    public static void logOut(final UICallBack<Boolean> callBack, final BusinessApplication app){
        new TaskRunner<String, Boolean>(new Task<String, Boolean>() {
            @Override
            public void onPreExecute() {
                callBack.onPreExecute();
            }

            @Override
            @Nullable
            public Boolean doInBackground(String[] params) {
                return HttpUtils.post("/storestaff/logout/", null, null, null, new byte[0], Boolean.class, app);
            }

            @Override
            public void onPostExecute(Boolean response) {
                callBack.onPostExecute(response);

            }
        }).execute(null);
    }

    public static void registerGCM (Context context, String topic) {
        if (Utils.checkPlayServices(context)) {
            Intent intent = new Intent(context, TopicRegistrationService.class);
            intent.putExtra(Constants.KEY, Constants.SUBSCRIBE);
            intent.putExtra(Constants.TOPIC, topic);
            context.startService(intent);
        } else {
            Toast.makeText(context.getApplicationContext(), "subscribe error, need google play service",
                Toast.LENGTH_SHORT).show();
        }
    }

    public static void getUser(final Context context, final String storeId, final String userId, final String token,
                                    final UICallBack<StoreStaff> callBack, final BusinessApplication app) {
        new TaskRunner<String, StoreStaff>(new Task<String, StoreStaff>() {
            @Override
            public void onPreExecute() {
                callBack.onPreExecute();
            }

            @Override
            @Nullable
            public StoreStaff doInBackground(String[] params) {
                Map<String,String> property = new HashMap<>();
                property.put("Authorization", token);
                StoreStaff user = HttpUtils.get("/storestaff/" + storeId + "/" + userId,null, property, StoreStaff.class, app);
                return user;
            }

            @Override
            public void onPostExecute(@Nullable StoreStaff o) {
                callBack.onPostExecute(o);

            }
        }).execute(null);
    }




    public static void cleanPreference(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHARED_PREFERENCE_FOODYMON, context.MODE_PRIVATE).edit();
        editor.putString(Constants.SHARED_PREFERENCE_USER_ID, "");
        editor.putString(Constants.SHARED_PREFERENCE_STORE_ID, "");
        editor.putString(Constants.SHARED_PREFERENCE_PASSWROD, "");
        editor.putString(Constants.SHARED_PREFERENCE_TOKEN, "");
        editor.apply();

        // Do I need to call server API?

    }

}
