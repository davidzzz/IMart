package com.imart.shop.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.imart.shop.LoginActivity;

import java.util.HashMap;

public class SessionManager {
    String value,key;
    SharedPreferences pref;
    Editor editor;
    Context _context;

    public static int PRIVATE_MODE = 0;
    public static final String PREF_NAME = "shopingo";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_NAME = "nama";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSENGER_ID = "user_id";
    public static final String KEY_ALAMAT = "alamat";
    public static final String KEY_TELP = "Telp";
    public static final String KEY_PHOTO ="photo";
    public static final String KEY_AKSES ="akses";
    public static final String KEY_SEX ="sex";
    public static final String KEY_LAT ="lat";
    public static final String KEY_LONGLAT ="longlat";
    public static final String KEY_POIN ="poin";
    public static final String KEY_FCM ="fcmid";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String nama, String telp, String alamat, String email, String poin,
                                   String sex ,String akses, String passengerId, String photo,String lat,String longlat,String fcmid) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        // Storing name in pref
        editor.putString(KEY_NAME, nama);
        // Storing telp in pref
        editor.putString(KEY_TELP, telp);
        // Storing alamat in pref
        editor.putString(KEY_ALAMAT, alamat);
        // Storing email in pref
        editor.putString(KEY_EMAIL, email);
        // Storing sex in pref
        editor.putString(KEY_SEX, sex);
        // Storing akses in pref
        editor.putString(KEY_AKSES, akses);
        editor.putString(KEY_PASSENGER_ID, passengerId);
        editor.putString(KEY_PHOTO,photo);
        editor.putString(KEY_POIN, poin);

        editor.putString(KEY_LAT,lat);
        editor.putString(KEY_LONGLAT,longlat);
        editor.putString(KEY_FCM,fcmid);

        // commit changes
        editor.commit();
    }

    public void updateValue(String key,String value){
        this.value = value;
        this.key =key;
        editor.putString(key,value);
        editor.commit();
    }

    public void updateProfile(String nama ,String email,String telp, String sex, String alamat,String lat,String long_lat,String fcmid, String poin){
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_NAME, nama);
        // Storing email in pref
        editor.putString(KEY_EMAIL, email);
        // Storing telp in pref
        editor.putString(KEY_TELP, telp);
        // Storing sex in pref
        editor.putString(KEY_SEX, sex);
        // Storing alamat in pref
        editor.putString(KEY_ALAMAT, alamat);
        // Storing lat in pref
        editor.putString(KEY_LAT, lat);
        // Storing long lat in pref
        editor.putString(KEY_LONGLAT, long_lat);

        editor.putString(KEY_FCM,fcmid);
        editor.putString(KEY_POIN, poin);
        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status If false it will redirect
     * user to login page Else won't do anything
     */
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);

        }
    }

    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        //TELP
        user.put(KEY_TELP, pref.getString(KEY_TELP, null));

        //ALAMAT
        user.put(KEY_ALAMAT, pref.getString(KEY_ALAMAT, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        user.put(KEY_SEX, pref.getString(KEY_SEX, null));

        user.put(KEY_AKSES, pref.getString(KEY_AKSES, null));

        user.put(KEY_PASSENGER_ID, pref.getString(KEY_PASSENGER_ID, null));

        user.put(KEY_PHOTO,pref.getString(KEY_PHOTO,null));
        user.put(KEY_POIN,pref.getString(KEY_POIN,null));

        user.put(KEY_LAT,pref.getString(KEY_LAT,null));
        user.put(KEY_LONGLAT,pref.getString(KEY_LONGLAT,null));

        user.put(KEY_LONGLAT,pref.getString(KEY_FCM,null));
        // return user
        return user;
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);

    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
