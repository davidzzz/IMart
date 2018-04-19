package com.imart.shop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;

import com.imart.shop.app.myapp;
import com.imart.shop.util.Constant;
import com.imart.shop.util.GoogleSign;
import com.imart.shop.util.SessionManager;
import com.imart.shop.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    Button ToLogin;
    EditText nama, telp, email, pass;
    //ini bagian button
    private Button btnRegis;
    String URL_REG;
    String nm, tl,em,ps;
    String strEmail, strAlamat, strPass, strMessage, strName, strPoin, strTelp, strPassengerId,strAkses,strSex,latitude,long_latitude,fcmid;
    SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        session = new SessionManager(getApplicationContext());

        //editext disini
        nama = (EditText) findViewById(R.id.Nama);
        telp = (EditText) findViewById(R.id.Telp);
        email = (EditText) findViewById(R.id.UserName);
        pass = (EditText) findViewById(R.id.pass);

        //button
        btnRegis = (Button) findViewById(R.id.btnRegis);

        btnRegis.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btnRegis:
                // ini button singin biasa
                validateEditext();
                break;
        }
    }

    private void validateEditext() {
        nm = nama.getText().toString();
        em = email.getText().toString();
        ps = pass.getText().toString();
        tl = telp.getText().toString();

        if(nm.length()==0){
            nama.setError("Tidak boleh kosong");
            nama.requestFocus();
        }else if(em.length()==0){
            email.setError("Tidak boleh kosong");
            email.requestFocus();
        }else if(!Patterns.EMAIL_ADDRESS.matcher(em).matches()){
            email.setError("Email tidak Valid");
            email.requestFocus();
        }else if(ps.length()==0){
            pass.setError("Tidak boleh kosong");
            pass.requestFocus();
        }else if(ps.length()<6){
            pass.setError("minimal 6 karakter");
            pass.requestFocus();
        }else if(tl.length()==0){
            telp.setError("Tidak boleh kosong");
            telp.requestFocus();
        }else if(tl.length()<6){
            telp.setError("No Telp minimal 6 angka");
            telp.requestFocus();
        }else {
            URL_REG = Constant.URLAPI+"key=" + Constant.KEY + "&tag=register&email="+em+"&pass="+ps+"&nama="+nm+"&tlp="+tl;
            final ProgressDialog loading = ProgressDialog.show(this, "Loading..", "Sedang register...", false, false);

            JsonObjectRequest jsonLogin = new JsonObjectRequest(Request.Method.GET, URL_REG, null,
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
        }
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
            Toast.makeText(this, strMessage, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            overridePendingTransition(R.anim.open_next, R.anim.close_next);
            finish();
        } else {
            session.createLoginSession(strName, strTelp, strAlamat, strEmail, strPoin, strSex, strAkses, strPassengerId, "-",latitude,long_latitude,fcmid);
            Intent Menu = new Intent(getApplicationContext(), MainActivity.class);
            Menu.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(Menu);
            finish();
        }
    }
}
