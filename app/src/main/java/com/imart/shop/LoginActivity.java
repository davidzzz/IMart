package com.imart.shop;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.FacebookException;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;

import com.google.android.gms.common.SignInButton;
import com.imart.shop.app.myapp;
import com.imart.shop.util.Constant;
import com.imart.shop.util.FacebookSign;
import com.imart.shop.util.GoogleSign;
import com.imart.shop.util.SessionManager;
import com.imart.shop.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class LoginActivity extends AppCompatActivity implements GoogleSign.InfoLoginGoogleCallback, FacebookSign.InfoLoginFaceCallback, OnClickListener {
    Button txtRegis, txtForgot;
    private Button btnSign;
    SignInButton btnGl;
    LoginButton btnFace;
    EditText username, pass;
    GoogleSign googleSign; // Google sign-in
    FacebookSign facebookSign;
    String strEmail, strAlamat, strPass, strMessage, strName, strPoin, strTelp, strPassengerId,strAkses,strSex,latitude,long_latitude;
    SessionManager session;
    String email,fcmid;
    String password,ProfileImg;
    Uri uri;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        googleSign = new GoogleSign(this, this);
        facebookSign = new FacebookSign(this, this);
        setContentView(R.layout.login);
        session = new SessionManager(getApplicationContext());
        //button
        txtRegis = (Button) findViewById(R.id.txtRegis);
        txtForgot = (Button) findViewById(R.id.txtForgot);

        //editext
        username = (EditText) findViewById(R.id.UserName);
        pass = (EditText) findViewById(R.id.pass);

        txtForgot.setOnClickListener(this);
        txtRegis.setOnClickListener(this);

        btnGl = (SignInButton) findViewById(R.id.btnGoogle);
        btnFace = (LoginButton) findViewById(R.id.btnFace);
        btnSign = (Button) findViewById(R.id.btnSign);

        // bikin button di klik
        btnGl.setOnClickListener(this);
        btnFace.setOnClickListener(this);
        btnSign.setOnClickListener(this);
        for (int i = 0; i < btnGl.getChildCount(); i++) {
            View v = btnGl.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText("Sign In With Google");
                //tv.setTextColor(Color.WHITE);
                //tv.setBackgroundColor(Color.RED);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        googleSign.resultGoogleLogin(requestCode, resultCode, data); // result
        facebookSign.resultFaceLogin(requestCode, resultCode, data); // result
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btnGoogle:
                // button di google di clik eksekusi ketika ada internet
                if (Utils.isConnectedToInternet(this)) {
                    // ketika konek internet ekseskusi
                    googleSign.signIn();
                } else {
                    // ini ketika tidak ada koneksi
                    Toast.makeText(this, "tidak ada koneksi", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btnFace:
                // button di google di clik eksekusi ketika ada internet
                if (Utils.isConnectedToInternet(this)) {
                    // ketika konek internet ekseskusi
                    facebookSign.signInWithFaceButton(btnFace);
                } else {
                    // ini ketika tidak ada koneksi
                    Toast.makeText(this, "tidak ada koneksi", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btnSign:
                validateEditext();

                break;

            case R.id.txtRegis:
                // ini button singin biasa
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                finish();
                break;

            case R.id.txtForgot:
                // ini button singin biasa
                if (Utils.isConnectedToInternet(this)) {
                    // ketika konek internet ekseskusi
                    startActivity(new Intent(getApplicationContext(), LupaPasswordActivity.class));
                    overridePendingTransition(R.anim.open_next, R.anim.close_next);
                    finish();
                } else {
                    // ini ketika tidak ada koneksi
                    Toast.makeText(this, "tidak ada koneksi", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void validateEditext() {
        email = username.getText().toString();
        password = pass.getText().toString();

        if(email.length()==0){
            username.setError("Tidak boleh kosong");
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            username.setError("Email tidak valid");
        }else if(password.length()==0){
            pass.setError("Password masih kosong");
        }else if(password.length()<6){
            pass.setError("Password minimal 6");
        }else{
            LoginExecute();
        }
    }

    private void LoginExecute() {
        final ProgressDialog loading = ProgressDialog.show(this, "Loading..", "Sedang login ...", false,
                false);
        String URL_LOGIN = Constant.URLAPI + "key=" + Constant.KEY + "&tag=" + Constant.TAG_LOGIN
                + "&email=" + email + "&pass=" + password;
        JsonObjectRequest jsonLogin = new JsonObjectRequest(Request.Method.GET, URL_LOGIN, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Dismissing progress dialog
                        parseJsonLogin(response);
                        loading.dismiss();
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();

            }
        });
        jsonLogin.setRetryPolicy(new DefaultRetryPolicy(5000, 20,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myapp.getInstance().addToRequestQueue(jsonLogin);
    }

    private void parseJsonLogin(JSONObject response) {
        try {
            JSONArray jsonArray = response.getJSONArray(Constant.USER_LOGIN_ARRAY);
            JSONObject objJson = null;
            for (int i = 0; i < jsonArray.length(); i++) {
                objJson = jsonArray.getJSONObject(i);
                if (objJson.has(Constant.USER_LOGIN_MSG)) {
                    strMessage = objJson.getString(Constant.USER_LOGIN_MSG);
                    Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.USER_LOGIN_SUCESS);
                } else {
                    Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.USER_LOGIN_SUCESS);
                    strPassengerId = objJson.getString(Constant.USER_LOGIN_ID);
                    strName = objJson.getString(Constant.USER_LOGIN_NAMA);
                    strEmail = objJson.getString(Constant.USER_LOGIN_MAIL);
                    strPoin = objJson.getString(Constant.USER_LOGIN_POIN);
                    strPass = objJson.getString(Constant.USER_LOGIN_PASS);
                    strAlamat = objJson.getString(Constant.USER_LOGIN_ALAMAT);
                    strTelp = objJson.getString(Constant.USER_LOGIN_TLP);
                    strAkses = objJson.getString("akses");
                    strSex = objJson.getString("sex");
                    latitude = objJson.getString("lat");
                    long_latitude = objJson.getString("longlat");
                    if (latitude.equals("0")) {
                        latitude ="0.000000";
                        long_latitude ="0.000000";
                    }
                    fcmid ="";
                    Constant.GET_AKSES = objJson.getInt(Constant.USER_LOGIN_AKSES);
                    if (objJson.getString("username").equals("admin")) {
                        Constant.IS_ADMIN = true;
                    }
                }
            }
        } catch (JSONException e) {
        }
        setResult();
    }

    private void setResult() {
        if (Constant.GET_SUCCESS_MSG == 0) {
            Toast.makeText(LoginActivity.this, strMessage, Toast.LENGTH_SHORT).show();
        } else {
            session.createLoginSession(strName, strTelp, strAlamat, strEmail, strPoin, strSex, strAkses, strPassengerId, ProfileImg,latitude,long_latitude,fcmid);
            Intent Menu = new Intent(getApplicationContext(), MainActivity.class);
            Menu.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(Menu);
            finish();
        }
    }

    // LISTNER Google SIGN-IN
    @Override
    public void getInfoLoginGoogle(GoogleSignInAccount account) {
        System.setProperty("http.keepAlive", "false");
        if (account.getEmail() != null &&  account.getDisplayName() != null){
            strName = account.getDisplayName();
            strEmail = account.getEmail();
            uri = account.getPhotoUrl();
            if (uri != null) {
                ProfileImg = uri.toString();
            } else {
                ProfileImg = "-";
            }
            final ProgressDialog loading = ProgressDialog.show(this, "Loading..", "Tunggu ya..", false, false);
            String URL_LOGIN = Constant.URLAPI + "key=" + Constant.KEY + "&tag=Login_ggl" + "&email=" + strEmail + "&nama=" + strName.replace(" ", "%20");
            JsonObjectRequest jsonLogin = new JsonObjectRequest(Request.Method.GET, URL_LOGIN, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Dismissing progress dialog
                            parseJsonLogin(response);
                            loading.dismiss();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loading.dismiss();
                }
            });
            jsonLogin.setRetryPolicy(new DefaultRetryPolicy(5000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            myapp.getInstance().addToRequestQueue(jsonLogin);
        } else {
            loginFailed();
        }
    }

    @Override
    public void connectionFailedApiClient(ConnectionResult connectionResult) {
        Log.e("LOG", "Connection Failed API " + connectionResult.getErrorMessage());
        Toast.makeText(this, "Koneksi terputus..", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loginFailed() {
        Toast.makeText(this, "Gagal Login..", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getInfoFace(String id, String name, String email, String foto) {
        System.setProperty("http.keepAlive", "false");
        if (email != null &&  name != null){
            strName = name;
            strEmail = email;
            ProfileImg = foto;
            final ProgressDialog loading = ProgressDialog.show(this, "Loading..", "Tunggu ya..", false, false);
            String URL_LOGIN = Constant.URLAPI + "key=" + Constant.KEY + "&tag=Login_face" + "&email=" + strEmail + "&nama=" + strName.replace(" ", "%20");
            JsonObjectRequest jsonLogin = new JsonObjectRequest(Request.Method.GET, URL_LOGIN, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Dismissing progress dialog
                            parseJsonLogin(response);
                            loading.dismiss();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loading.dismiss();
                }
            });
            jsonLogin.setRetryPolicy(new DefaultRetryPolicy(5000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            myapp.getInstance().addToRequestQueue(jsonLogin);
        } else {
            cancelLoginFace();
        }
    }

    @Override
    public void cancelLoginFace() {
        Toast.makeText(this, "Batal Login", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void errorLoginFace(FacebookException e) {
        Log.e("LOG", "Connection Failed API " + e.getMessage());
        Toast.makeText(this, "Gagal Login", Toast.LENGTH_SHORT).show();
    }
}
