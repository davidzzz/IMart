package com.imart.shop.app;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.imart.shop.util.SessionManager;

import java.util.HashMap;

/**
 * Created by liau on 1/8/2017.
 */

public class MyfirebaseService extends FirebaseInstanceIdService {
    static final String TAG = "MyfirebaseService";
    String idfcm ="firebaseid";
    SessionManager session;
    HashMap<String, String> user;
    String id,URL_TOKEN,msg;
    @Override
    public void onTokenRefresh() {
        //Get hold of the registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendToServer(refreshedToken);
    }

    private void sendToServer(String token) {
        Log.d("Firebase", "simpan ke pref");
        SharedPreferences pref = getApplicationContext().getSharedPreferences(idfcm, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();

    }

}