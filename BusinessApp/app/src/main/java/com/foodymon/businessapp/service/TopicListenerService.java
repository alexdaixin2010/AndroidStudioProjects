package com.foodymon.businessapp.service;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.support.v4.content.LocalBroadcastManager;

import com.foodymon.businessapp.constant.Constant;
import com.foodymon.businessapp.main.activity.MainActivity;
import com.foodymon.businessapp.utils.Utils;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by alexdai on 4/30/16.
 */
public class TopicListenerService extends GcmListenerService {
    private final static String TAG = "TopicListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }


        // app is in foreground, broadcast the push message
        Intent pushNotification = new Intent(Constant.ORDER_UPDATE);
        pushNotification.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

    }


}
