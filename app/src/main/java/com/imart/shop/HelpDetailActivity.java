package com.imart.shop;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.imart.shop.app.myapp;
import com.imart.shop.util.Constant;

import org.json.JSONException;
import org.json.JSONObject;

public class HelpDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    int id;
    double latitude, longitude;
    GoogleMap gMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_detail);
        id = getIntent().getIntExtra("id", 0);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setIcon(R.drawable.logo_imart);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Constant.COLOR));
        if (id == 1) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
        ambilData();
        lokasiToko();
    }

    private void ambilData() {
        String URL = Constant.URLAPI + "key=" + Constant.KEY + "&tag=helpdetail&id=" + id;
        JsonObjectRequest jsonKate = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject object = response.getJSONObject("data");
                    TextView judul = (TextView) findViewById(R.id.judul);
                    TextView isi = (TextView) findViewById(R.id.isi);
                    judul.setText(object.getString("judul"));
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        isi.setText(Html.fromHtml(object.getString("isi"), Html.FROM_HTML_MODE_COMPACT), TextView.BufferType.SPANNABLE);
                    } else {
                        isi.setText(Html.fromHtml(object.getString("isi")), TextView.BufferType.SPANNABLE);
                    }
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
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
    }

    private void lokasiToko() {
        String URL_LOKASI = Constant.URLAPI + "key=" + Constant.KEY + "&tag=lokasiToko";
        JsonObjectRequest jsonKate = new JsonObjectRequest(Request.Method.GET, URL_LOKASI, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject feedObj = response.getJSONObject("data");
                    latitude = feedObj.getDouble("latitude");
                    longitude = feedObj.getDouble("long_latitude");

                    LatLng lokasi = new LatLng(latitude, longitude);
                    gMap.addMarker(new MarkerOptions().position(lokasi).title("Lokasi Toko"));
                    gMap.moveCamera(CameraUpdateFactory.newLatLng(lokasi));
                    gMap.animateCamera(CameraUpdateFactory.zoomTo(17));
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // app icon in action bar clicked; go home
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
