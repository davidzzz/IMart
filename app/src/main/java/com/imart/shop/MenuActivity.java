package com.imart.shop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.imart.shop.app.myapp;
import com.imart.shop.util.Constant;
import com.imart.shop.util.SessionManager;
import com.imart.shop.view.RoundImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MenuActivity.class.getSimpleName();
    private Toolbar toolbar;
    private LinearLayout lytProfile, lytMenu, lytOrder, lytPoin;
    //ambil data dari session
    SessionManager session;
    HashMap<String, String> user;
    String akses, photo, nama_user,URL,id_user;
    ImageView imgProfile;
    TextView txtnama,txtcount,txtkode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_user);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.setting_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Constant.COLOR));
        imgProfile = (ImageView) findViewById(R.id.imgProfile);
        txtnama = (TextView) findViewById(R.id.txtnama);
        txtkode = (TextView) findViewById(R.id.userid);
        txtcount =(TextView) findViewById(R.id.txtcount);
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
        photo = user.get(SessionManager.KEY_PHOTO);
        akses = user.get(SessionManager.KEY_AKSES);
        id_user = user.get(SessionManager.KEY_PASSENGER_ID);
        nama_user = user.get(SessionManager.KEY_NAME).toUpperCase();
        URL = Constant.URLADMIN + "api/order_list.php?key=" + Constant.KEY + "&tag=count";
        String formatter = String.format(Locale.US, "%04d", Integer.parseInt(id_user));
        txtnama.setText(nama_user);

        Glide.with(this)
                .load(photo != null ? photo : R.drawable.user)
                .transform(new RoundImage(MenuActivity.this))
                .into(imgProfile);
        lytProfile = (LinearLayout) findViewById(R.id.lytprofile);
        lytMenu = (LinearLayout) findViewById(R.id.lytMenu);
        lytOrder = (LinearLayout) findViewById(R.id.lytOrder);
        lytPoin = (LinearLayout) findViewById(R.id.lytPoin);
        if (akses.equals("2")) {
            lytMenu.setVisibility(View.VISIBLE);
            lytOrder.setVisibility(View.VISIBLE);
            lytPoin.setVisibility(View.VISIBLE);
            ambilData();
        } else {
            lytOrder.setVisibility(View.GONE);
            lytMenu.setVisibility(View.GONE);
            lytPoin.setVisibility(View.GONE);
            txtkode.setText("USER ID : " + String.valueOf(nama_user.charAt(0)) + String.valueOf(nama_user.charAt(2)) + formatter);
            try {
                ImageView qrCode = (ImageView) findViewById(R.id.qr_code);
                Bitmap bm = encodeAsBitmap(String.valueOf(nama_user.charAt(0)) + String.valueOf(nama_user.charAt(2)) + formatter);
                if(bm != null) {
                    qrCode.setImageBitmap(bm);
                }
            } catch (WriterException e) {
                Toast.makeText(this, "Gagal menampilkan QR Code", Toast.LENGTH_SHORT).show();
            }
        }
        lytOrder.setOnClickListener(this);
        lytMenu.setOnClickListener(this);
        lytProfile.setOnClickListener(this);
        lytPoin.setOnClickListener(this);
    }

    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 150, 150, null);
        } catch (IllegalArgumentException iae) {
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 150, 0, 0, w, h);
        return bitmap;
    }

    private void ambilData() {
        JsonObjectRequest jsonKate = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int count = response.getInt("data");
                    txtcount.setText(count + "");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lytprofile:
                Intent profile = new Intent(this, ProfileActivity.class);
                startActivity(profile);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                break;
            case R.id.lytMenu:
                Intent menu = new Intent(this, ListSeller.class);
                startActivity(menu);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                break;
            case R.id.lytOrder:
                Intent order = new Intent(this, OrderList.class);
                startActivity(order);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                break;
            case R.id.lytPoin:
                Intent poin = new Intent(this, SentPoinActivity.class);
                startActivity(poin);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                break;
            default:
                break;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Glide.get(this).clearMemory();
        finish();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }
}
