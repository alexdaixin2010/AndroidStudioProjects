package com.foodymon.businessapp.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.foodymon.businessapp.constant.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;


import java.io.IOException;

/**
 * Created by alexdai on 4/30/16.
 */
public class TopicRegistrationService extends IntentService {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "RegistrationService";


    public TopicRegistrationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String key = intent.getStringExtra(Constants.KEY);
        String topic = intent.getStringExtra(Constants.TOPIC);
        switch (key) {
            case Constants.SUBSCRIBE:
                // subscribe to a topic
                subscribeTopics(topic);
                break;
            case Constants.UNSUBSCRIBE:
                unsubscribeFromTopic(topic);
                break;
            default:
        }
    }

    private boolean checkPlayServices(Context context) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }


    public void subscribeTopics(String topic) {
        InstanceID instanceID = InstanceID.getInstance(this);
        String token = null;
        try {
            token = instanceID.getToken(Constants.SENDER_ID,
                GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            GcmPubSub pubSub = GcmPubSub.getInstance(this);
            pubSub.subscribe(token, "/topics/" + topic, null);

            Intent registrationComplete = new Intent(Constants.REGISTRATION_COMPLETE);
            registrationComplete.putExtra("token", token);
            LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
        } catch (IOException e) {
            Log.e(TAG, "Topic gcm registration error : "+  e.getMessage());
            Toast.makeText(this.getApplicationContext(), "subscribe error, need manually refresh",
                Toast.LENGTH_SHORT).show();
        }
    }


    public void unsubscribeFromTopic(String topic) {
        GcmPubSub pubSub = GcmPubSub.getInstance(getApplicationContext());
        InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
        String token = null;
        try {
            token = instanceID.getToken(Constants.SENDER_ID,
                GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            if (token != null) {
                pubSub.unsubscribe(token, "/topics/" + topic);
                Log.e(TAG, "Unsubscribed from topic: " + topic);
            } else {
                Log.e(TAG, "error: gcm registration id is null");
            }
        } catch (IOException e) {
            Log.e(TAG, "Topic unsubscribe error. Topic: " + topic + ", error: " + e.getMessage());
        }
    }
}
