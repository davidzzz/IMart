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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;

import com.imart.shop.app.myapp;
import com.imart.shop.util.Constant;
import com.imart.shop.util.GoogleSign;
import com.imart.shop.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity implements GoogleSign.InfoLoginGoogleCallback, View.OnClickListener {

    Button ToLogin;
    EditText nama, telp, email, pass;
    //ini bagian button
    private Button btnGl, btnRegis;
    GoogleSign googleSign; // Google sign-in
    String URL_REG, MESSAGE;
    String nm, tl,em,ps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        //editext disini
        nama = (EditText) findViewById(R.id.Nama);
        telp = (EditText) findViewById(R.id.Telp);
        email = (EditText) findViewById(R.id.UserName);
        pass = (EditText) findViewById(R.id.pass);

        //button
        //btnGl = (Button) findViewById(R.id.btnGogle);
        //ToLogin = (Button) findViewById(R.id.txtToLogin);
        btnRegis = (Button) findViewById(R.id.btnRegis);


        // FragmentActivity and interface listener
        googleSign = new GoogleSign(this, this);

        //ToLogin.setOnClickListener(this);
        btnRegis.setOnClickListener(this);
        //btnGl.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        googleSign.resultGoogleLogin(requestCode, resultCode, data); // result
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            /*case R.id.btnGogle:
                // button di google di clik eksekusi ketika ada internet
                if (Utils.isConnectedToInternet(this)) {
                    // ketika konek internet ekseskusi
                    googleSign.signIn();
                } else {
                    // ini ketika tidak ada koneksi
                    Toast.makeText(this, "tidak ada koneksi pak", Toast.LENGTH_SHORT).show();
                }

                break;*/
            case R.id.btnRegis:
                // ini button singin biasa
                validateEditext();
                break;

            /*case R.id.txtToLogin:
                // ini button singin biasa
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                finish();

                break;*/
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

            JsonObjectRequest jsonLogin = new JsonObjectRequest(URL_REG,
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
            JSONArray jsonArray = response.getJSONArray(Constant.USER_REG_ARRAY);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objJson = jsonArray.getJSONObject(i);
                MESSAGE = objJson.getString(Constant.USER_REG_MSG);
                Constant.GET_SUCCESS_REGIS = objJson.getInt(Constant.USER_REG_SUCESS);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setResult();
    }

    private void setResult() {
        if (Constant.GET_SUCCESS_MSG == 0) {
            Toast.makeText(this, MESSAGE, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            overridePendingTransition(R.anim.open_next, R.anim.close_next);
            finish();
        } else {
            Toast.makeText(this,MESSAGE,Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            overridePendingTransition(R.anim.open_next, R.anim.close_next);
            finish();
        }
    }

    @Override
    public void getInfoLoginGoogle(GoogleSignInAccount account) {
        URL_REG = Constant.URLAPI + "key=" + Constant.KEY + "&tag=regsgoogle&email=" + account.getEmail() + "&nama=" + account.getDisplayName().replace(" ", "%20");
        final ProgressDialog loading = ProgressDialog.show(this, "Loading..", "Tunggu ya..", false, false);

        JsonObjectRequest jsonLogin = new JsonObjectRequest(URL_REG,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Dismissing progress dialog
                        parseJsonLogin(response);
                        loading.dismiss();
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {loading.dismiss();

            }
        });
        jsonLogin.setRetryPolicy(new DefaultRetryPolicy(5000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myapp.getInstance().addToRequestQueue(jsonLogin);
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
}
