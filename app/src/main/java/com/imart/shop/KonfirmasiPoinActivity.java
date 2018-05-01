package com.imart.shop;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.imart.shop.app.myapp;
import com.imart.shop.util.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class KonfirmasiPoinActivity extends BaseActivity {
    TextView teksNomorResi, teksTotalOrder, teksPoin, teksNomorUser, teksNama, teksNoHP;
    Button konfirmasi;
    String URL, URLPOIN, nomorResi, totalOrder, poin, nomorUser, id, idOrder;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfirmasi_poin);
        getSupportActionBar().setTitle("KONFIRMASI");
        teksNomorResi = (TextView) findViewById(R.id.nomor_resi);
        teksTotalOrder = (TextView) findViewById(R.id.total_order);
        teksPoin = (TextView) findViewById(R.id.poin);
        teksNomorUser = (TextView) findViewById(R.id.nomor_user);
        teksNama = (TextView) findViewById(R.id.nama);
        teksNoHP = (TextView) findViewById(R.id.nomor_handphone);
        konfirmasi = (Button) findViewById(R.id.konfirmasi);
        konfirmasi.setEnabled(false);
        nomorResi = getIntent().getStringExtra("nomor");
        totalOrder = getIntent().getStringExtra("total");
        poin = getIntent().getStringExtra("poin");
        nomorUser = getIntent().getStringExtra("nomorUser");
        idOrder = getIntent().getStringExtra("idOrder");
        teksNomorResi.setText(nomorResi);
        teksTotalOrder.setText("Rp. " + totalOrder);
        teksPoin.setText(poin + " poin");
        teksNomorUser.setText(nomorUser);
        URL = Constant.URLAPI + "key=" + Constant.KEY + "&tag=user&nomorUser=" + nomorUser;
        URLPOIN = Constant.URLADMIN + "api/konfirmasi_poin.php";
        getDataUser();
        konfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(KonfirmasiPoinActivity.this);
                alert.setTitle("KONFIRMASI");
                alert.setMessage("Apakah data transaksi sudah sesuai?");
                alert.setPositiveButton("YA", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        loading = ProgressDialog.show(KonfirmasiPoinActivity.this, "Loading", "Mengupdate poin user", false, false);
                        new UpdatePoin().execute();
                    }
                });

                alert.setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });
    }

    class UpdatePoin extends AsyncTask<Void,Void,Boolean> {
        String response;
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                java.net.URL url = new URL(URLPOIN);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpURLConnection.connect();

                String parameter = "id=" + id + "&poin=" + poin + "&total=" + totalOrder + "&nomor=" + nomorResi + "&key=" + Constant.KEY;
                if (idOrder != null) {
                    parameter += "&idOrder=" + idOrder;
                }
                OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());
                writer.write(parameter);
                writer.flush();
                writer.close();

                InputStream responseStream = new BufferedInputStream(httpURLConnection.getInputStream());
                BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = responseStreamReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                response = stringBuilder.toString();
                responseStreamReader.close();
                responseStream.close();
                loading.dismiss();
                Intent Menu = new Intent(getApplicationContext(), MainActivity.class);
                Menu.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(Menu);
                finish();
                return true;
            } catch (Exception e) {
                loading.dismiss();
                return false;
            }
        }

        @Override
        public void onPostExecute(Boolean result) {
            if (result != null && result) {
                Toast.makeText(KonfirmasiPoinActivity.this, response, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(KonfirmasiPoinActivity.this, "Poin tidak berhasil ditambahkan", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void getDataUser(){
        final ProgressDialog loading = ProgressDialog.show(this, "Loading", "Sedang mengambil data user", false, false);
        JsonObjectRequest jsonKate = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loading.dismiss();
                parseJsonKategory(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
            }
        });
        jsonKate.setRetryPolicy(new DefaultRetryPolicy(5000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myapp.getInstance().addToRequestQueue(jsonKate);
    }

    private void parseJsonKategory(JSONObject response) {
        try {
            JSONObject feedObj = response.getJSONObject("data");
            teksNama.setText(feedObj.getString("nama"));
            teksNoHP.setText(feedObj.getString("telp"));
            id = feedObj.getString("user_id");
            konfirmasi.setEnabled(true);
        } catch (JSONException e) {
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // app icon in action bar clicked; go home
            this.finish();
            overridePendingTransition(R.anim.open_main, R.anim.close_next);
        }
        return super.onOptionsItemSelected(item);
    }
}
