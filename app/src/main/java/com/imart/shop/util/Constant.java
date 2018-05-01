package com.imart.shop.util;

import android.graphics.Color;

import com.imart.shop.model.Cart;

import java.util.ArrayList;

public class Constant {
    // INI BASE URL API DI ADMIN PANEL JANGN LUPA GANTI KEY UNTUK KEAMANAN
    // BERSAMA. ^,^

    public static String URLADMIN = "http://www.guritadigital.com/imart/";
    public static String URLAPI = "http://www.guritadigital.com/imart/api/api.php?";
    //public static String URLADMIN = "http://192.168.56.1/adminrestoran/";
    //public static String URLAPI = "http://192.168.56.1/adminrestoran/api/api.php?";
    public static String KEY = "12345";// USAHKAN PAKAI CAMPURAN KARAKTER
    public static int COLOR = Color.parseColor("#b1f01d");
    public static int COLORVALUE = Color.parseColor("#b1f01d");

    public static String TAG_SUB = "sub";
    public static String TAG_PROMO = "promo";

    public static ArrayList<Cart> cartList = new ArrayList<>();
    public static int poin = 0;
    public static int jumlah = 0;
    public static boolean init = true;

    //BAGIAN LOGIN
    public static final String TAG_LOGIN = "login";
    public static final String USER_LOGIN_ARRAY = "Shopingo";
    public static final String USER_LOGIN_MSG = "msg";
    public static final String USER_LOGIN_SUCESS = "Success";
    public static final String USER_LOGIN_ID = "user_id";
    public static final String USER_LOGIN_NAMA = "nama";
    public static final String USER_LOGIN_TLP = "Telp";
    public static final String USER_LOGIN_ALAMAT = "alamat";
    public static final String USER_LOGIN_MAIL = "email";
    public static final String USER_LOGIN_POIN = "poin";
    public static final String USER_LOGIN_AKSES = "akses";
    public static final String USER_LOGIN_PASS = "password";
    public static final String USER_GCM_ID = "gcm_id";
    public static int GET_SUCCESS_MSG;
    public static int GET_AKSES;
    public static boolean IS_ADMIN = false;

    //BAGIAN LATITUDE
    public static String google_key="AIzaSyAKJSEmNQ9OtgKIzifjdNQBq50xb4cj_H0";

    //BAGIAN REGISTER
    public static final String USER_REG_ARRAY = "Shopingo";
    public static final String USER_REG_MSG = "msg";
    public static final String USER_REG_SUCESS = "Success";
    public static int GET_SUCCESS_REGIS;

    //bagian gcm
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static String strMessage;
    public static int status;
}
