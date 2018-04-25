package com.imart.shop;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.imart.shop.app.myapp;
import com.imart.shop.util.Constant;


import java.util.Random;

public class LupaPasswordActivity extends AppCompatActivity {
    EditText teksEmail, teksKode, teksPass;
    String status = "email", kode = "", email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupa_password);
        Button login = (Button) findViewById(R.id.txtLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(LupaPasswordActivity.this);
                alert.setTitle("LUPA PASSWORD");
                alert.setMessage("Anda yakin akan meninggalkan halaman ini?");
                alert.setPositiveButton("YA", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }
                });

                alert.setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
                alert.show();
            }
        });
        teksEmail = (EditText) findViewById(R.id.email);
        teksKode = (EditText) findViewById(R.id.kode);
        teksPass = (EditText) findViewById(R.id.pass);
        teksKode.setVisibility(View.GONE);
        teksPass.setVisibility(View.GONE);
        Button send = (Button) findViewById(R.id.btnSend);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status.equals("email")) {
                    sendEmail();
                } else if (status.equals("kode")) {
                    cekKode();
                } else {
                    gantiPassword();
                }
            }
        });
    }

    public void sendEmail() {
        email = teksEmail.getText().toString();
        if (email.equals("")) {
            Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email tidak valid", Toast.LENGTH_SHORT).show();
        }else {
            teksEmail.setVisibility(View.GONE);
            teksKode.setVisibility(View.VISIBLE);
            status = "kode";
            Random r = new Random();
            int angka = r.nextInt(10000);
            if (angka < 10) {
                kode = "000" + angka;
            } else if (angka < 100) {
                kode = "00" + angka;
            } else if (angka < 1000) {
                kode = "0" + angka;
            } else {
                kode = "" + angka;
            }
        }
        final ProgressDialog loading = ProgressDialog.show(this, "Loading..", "Mendapatkan kode ...", false, false);
        String URL_LOGIN = Constant.URLAPI + "key=" + Constant.KEY + "&tag=lupaPassword&email=" + email + "&kode=" + kode;
        StringRequest jsonLogin = new StringRequest(URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(LupaPasswordActivity.this, response, Toast.LENGTH_SHORT).show();
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

    public void cekKode() {
        String inputKode = teksKode.getText().toString();
        if (inputKode.equals(kode)) {
            teksKode.setVisibility(View.GONE);
            teksPass.setVisibility(View.VISIBLE);
            status = "pass";
        } else {
            Toast.makeText(this, "Kode salah", Toast.LENGTH_SHORT).show();
        }
    }

    public void gantiPassword() {
        String pass = teksPass.getText().toString();
        if (pass.equals("")) {
            Toast.makeText(this, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
        } else if (pass.length() < 6) {
            Toast.makeText(this, "Panjang Password minimal 6 karakter", Toast.LENGTH_SHORT).show();
        } else {
            final ProgressDialog loading = ProgressDialog.show(this, "Loading..", "Sedang mengganti password ...", false, false);
            String URL_LOGIN = Constant.URLAPI + "key=" + Constant.KEY + "&tag=gantiPassword&email=" + email + "&pass=" + pass;
            StringRequest jsonLogin = new StringRequest(URL_LOGIN,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(LupaPasswordActivity.this, response, Toast.LENGTH_SHORT).show();
                            loading.dismiss();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
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

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(LupaPasswordActivity.this);
        alert.setTitle("LUPA PASSWORD");
        alert.setMessage("Anda yakin akan meninggalkan halaman ini?");
        alert.setPositiveButton("YA", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        alert.setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        alert.show();
    }
}
