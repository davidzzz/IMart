package com.imart.shop;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.imart.shop.app.myapp;
import com.imart.shop.util.Constant;
import com.imart.shop.util.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SplashActivity extends Activity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private ImageView mLogo;
    private TextView welcomeText;
    private static final int SPLASH_DURATION = 5000;
    private SessionManager sessionManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);
        mLogo = (ImageView) findViewById(R.id.logo);
        welcomeText = (TextView) findViewById(R.id.welcome_text);

        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        final boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

        if (isFirstStart) {
            //  Launch app intro
            Intent i = new Intent(SplashActivity.this, Intro.class);
            startActivity(i);
            finish();

            //  Make a new preferences editor
            SharedPreferences.Editor e = getPrefs.edit();

            //  Edit preference to make it false because we don't want this to run again
            e.putBoolean("firstStart", false);

            //  Apply changes
            e.apply();
        } else {
            start();
        }
    }

    private void start() {
        if (isLogin()) {
            log();
        }
        konfigurasi();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        },SPLASH_DURATION );
    }

    public boolean isLogin() {
        sessionManager = new SessionManager(getApplicationContext());
        return sessionManager.isLoggedIn();
    }

    public void log(){
        HashMap<String, String> user = sessionManager.getUserDetails();
        String id = user.get(sessionManager.KEY_PASSENGER_ID);
        String URL = Constant.URLAPI + "key=" + Constant.KEY + "&tag=log&idUser=" + id + "&tipe=Login&keterangan=Login";
        JsonObjectRequest jsonKate = new JsonObjectRequest(URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        jsonKate.setRetryPolicy(new DefaultRetryPolicy(5000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myapp.getInstance().addToRequestQueue(jsonKate);
    }

    private void konfigurasi() {
        String URL_CONF = Constant.URLAPI + "key=" + Constant.KEY + "&tag=konfigurasi&id=6";
        JsonObjectRequest jsonKate = new JsonObjectRequest(URL_CONF, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject feedObj = response.getJSONObject("data");
                    int color = feedObj.getInt("value");
                    Constant.COLOR = Color.parseColor("#" + String.format("%06x", color));
                } catch (JSONException e) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        jsonKate.setRetryPolicy(new DefaultRetryPolicy(5000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myapp.getInstance().addToRequestQueue(jsonKate);
    }
}