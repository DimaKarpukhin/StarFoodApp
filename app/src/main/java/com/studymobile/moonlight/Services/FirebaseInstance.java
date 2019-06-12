package com.studymobile.moonlight.Services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseInstance extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseInstance";

    @Override
    public void onTokenRefresh()
    {
        // Get updated InstanceID token.
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "New Token:  " + refreshToken);
    }
}