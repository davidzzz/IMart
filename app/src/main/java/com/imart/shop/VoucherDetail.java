package com.imart.shop;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.imart.shop.app.myapp;
import com.imart.shop.util.Constant;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class VoucherDetail extends AppCompatActivity {
    Toolbar toolbar;
    int id;
    String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Voucher");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Constant.COLOR));
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Constant.COLOR);
        } else {
            window.setTitleColor(Constant.COLOR);
        }
        id = getIntent().getIntExtra("id", 0);
        URL = Constant.URLADMIN + "api/voucher.php?key=" + Constant.KEY + "&tag=listdetail&id=" + id;
        viewVoucher();
    }

    public void viewVoucher() {
        JsonObjectRequest jsonKate = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                parseJsonKategory(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        jsonKate.setRetryPolicy(new DefaultRetryPolicy(5000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myapp.getInstance().addToRequestQueue(jsonKate);
    }

    private void parseJsonKategory(JSONObject response) {
        TextView teks_nama = (TextView) findViewById(R.id.nama);
        TextView teks_poin = (TextView) findViewById(R.id.poin);
        TextView teks_tanggal = (TextView) findViewById(R.id.tanggal);
        TextView teks_deskripsi = (TextView) findViewById(R.id.deskripsi);
        ImageView gambar = (ImageView) findViewById(R.id.gambar);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            JSONObject feedObj = response.getJSONObject("data");
            Date d = sdf.parse(feedObj.getString("tanggal_expired"));
            sdf = new SimpleDateFormat("dd MMMM yyyy");
            teks_nama.setText(feedObj.getString("nama"));
            teks_poin.setText(feedObj.getInt("poin") + " POINTS");
            teks_tanggal.setText("Batas Waktu : " + sdf.format(d));
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                teks_deskripsi.setText(Html.fromHtml(feedObj.getString("deskripsi"), Html.FROM_HTML_MODE_COMPACT), TextView.BufferType.SPANNABLE);
            } else {
                teks_deskripsi.setText(Html.fromHtml(feedObj.getString("deskripsi")), TextView.BufferType.SPANNABLE);
            }
            Glide.with(this).load(Constant.URLADMIN + feedObj.getString("gambar"))
                    .placeholder(R.drawable.loading)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(gambar);
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
