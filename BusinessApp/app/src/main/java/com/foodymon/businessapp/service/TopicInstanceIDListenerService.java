package com.foodymon.businessapp.service;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by alexdai on 4/30/16.
 */
public class TopicInstanceIDListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, TopicRegistrationService.class);
        startService(intent);
    }
}
