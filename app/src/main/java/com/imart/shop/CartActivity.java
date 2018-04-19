package com.imart.shop;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.imart.shop.adapter.ConfirmAdapter;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.imart.shop.app.myapp;
import com.imart.shop.model.Antar;
import com.imart.shop.model.Cart;
import com.imart.shop.util.Constant;
import com.imart.shop.util.EasyPermission;
import com.imart.shop.util.GPSTracker;
import com.imart.shop.util.RouteDraw;
import com.imart.shop.util.SessionManager;
import com.imart.shop.util.Utils;
import com.imart.shop.view.ExpandableHeightListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CartActivity extends AppCompatActivity
        implements View.OnClickListener, OnMapReadyCallback, RouteDraw.onDrawRoute, EasyPermission.OnPermissionResult, LocationListener {
    private static final String TAG = CartActivity.class.getSimpleName();
    private Toolbar toolbar;
    private ExpandableHeightListView list;
    private ArrayList<Cart> cartList;
    private ArrayList<Antar> listAntar;
    private AdapterCart adapter = null;
    private ArrayAdapter adapterAntar;
    private GoogleMap mMap;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private EasyPermission easyPermission;
    GPSTracker gps;
    SessionManager session;
    HashMap<String, String> user;
    String alamat, notes, totalorder, userid,telp;
    String URL_SEND, URL_CONF, URL_LOKASI, URL_REK;
    Button btnSend;
    Antar antar;
    String idAntar;
    double latitude, longitude;
    EditText edtkoment, edtAlamat, edt_telp;
    int Total = 0; //total harga menulist
    int ongkir = 0; //ini total ongkir
    int subTotal = 0; //ini subtotal
    int meter = 0;
    int perkm = 0;
    int poin = 0;
    int totalBarang = 0, totalItem = 0;
    boolean onTheSpot = false;
    double latToko = 0.0, lngToko = 0.0;
    String idFlashDeal = "", norek = "";
    TextView textTotal, textPoin, txtOngkir, txtsubtotl,titleongkir;
    DecimalFormat formatduit = new DecimalFormat();
    ProgressDialog loading;
    LinearLayout lytAlert,lytOrder;
    CheckBox cek;
    Spinner jam;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_list);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.checkout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Constant.COLOR));
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Constant.COLOR);
        } else {
            window.setTitleColor(Constant.COLOR);
        }
        gps = new GPSTracker(this);

        lytOrder =(LinearLayout)findViewById(R.id.lytOrder);
        lytAlert =(LinearLayout)findViewById(R.id.lytAlert);
        list = (ExpandableHeightListView) findViewById(R.id.listview);
        edtkoment = (EditText) findViewById(R.id.edtkoment);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
        if(!Utils.isConnectedToInternet(this)){
            lytAlert.setVisibility(View.VISIBLE);
        }else{
            lytOrder.setVisibility(View.VISIBLE);
            lytAlert.setVisibility(View.GONE);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            easyPermission = new EasyPermission();
            easyPermission.requestPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (gps.canGetLocation()) {
            startLocationUpdates();
        }

        edtAlamat = (EditText) findViewById(R.id.edtAlamat);
        edt_telp = (EditText)findViewById(R.id.edt_telp);

        textPoin = (TextView) findViewById(R.id.textPoin);
        textTotal = (TextView) findViewById(R.id.textTotal);
        txtOngkir = (TextView) findViewById(R.id.txtOngkir);
        titleongkir =(TextView) findViewById(R.id.titleongkir);
        txtsubtotl = (TextView) findViewById(R.id.txtsubtotl);
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
        userid = user.get(session.KEY_PASSENGER_ID);
        poin = getIntent().getIntExtra("poin", 0);
        idFlashDeal = getIntent().getStringExtra("idFlashDeal");

        cek = (CheckBox) findViewById(R.id.cek);
        cek.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LinearLayout layoutAlamat = (LinearLayout) findViewById(R.id.layout_alamat);
                LinearLayout layoutPoin = (LinearLayout) findViewById(R.id.layout_poin);
                LinearLayout layoutOngkir = (LinearLayout) findViewById(R.id.layout_ongkir);
                LinearLayout layoutAntar = (LinearLayout) findViewById(R.id.layout_antar);
                onTheSpot = b;
                if (b) {
                    layoutAlamat.setVisibility(View.GONE);
                    layoutPoin.setVisibility(View.GONE);
                    layoutOngkir.setVisibility(View.GONE);
                    layoutAntar.setVisibility(View.GONE);
                    subTotal -= ongkir;
                    txtsubtotl.setText(formatduit.format(subTotal) + "");
                } else {
                    layoutAlamat.setVisibility(View.VISIBLE);
                    layoutPoin.setVisibility(View.VISIBLE);
                    layoutOngkir.setVisibility(View.VISIBLE);
                    layoutAntar.setVisibility(View.VISIBLE);
                    subTotal += ongkir;
                    txtsubtotl.setText(formatduit.format(subTotal) + "");
                }
            }
        });
        jam = (Spinner) findViewById(R.id.jam);
        setSpin();

        URL_SEND = Constant.URLADMIN + "api/send_order.php";
        URL_CONF = Constant.URLAPI + "key=" + Constant.KEY + "&tag=konfigurasi&id=4";
        URL_REK = Constant.URLAPI + "key=" + Constant.KEY + "&tag=konfigurasi&id=7";
        URL_LOKASI = Constant.URLAPI + "key=" + Constant.KEY + "&tag=lokasiToko";
        cartList = getIntent().getParcelableArrayListExtra("cartList");
        if (cartList.size() <= 0) {
            btnSend.setEnabled(false);
        }
        konfigurasi();
        lokasiToko();
        ambilData();
    }

    public void daftarKategori() {
        String URLKATE = Constant.URLAPI + "key=" + Constant.KEY + "&tag=" + Constant.TAG_SUB;
        listAntar = new ArrayList<>();
        JsonObjectRequest jsonKate = new JsonObjectRequest(Request.Method.GET, URLKATE, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray feedArray = response.getJSONArray("data");
                    for (int i = 0; i < feedArray.length(); i++) {
                        JSONObject feedObj = (JSONObject) feedArray.get(i);
                        Antar item = new Antar();
                        item.setId(feedObj.getString("id"));
                        item.setJam(feedObj.getString("jam"));
                        listAntar.add(item);
                    }
                } catch (JSONException e) {
                }
                adapterAntar.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        jsonKate.setRetryPolicy(new DefaultRetryPolicy(5000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myapp.getInstance().addToRequestQueue(jsonKate);
    }

    private void setSpin() {
        daftarKategori();
        adapterAntar = new ArrayAdapter<>(CartActivity.this, android.R.layout.simple_spinner_dropdown_item, listAntar);
        jam.setAdapter(adapter);
        jam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                antar = (Antar) parent.getItemAtPosition(position);
                idAntar = antar.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public Boolean Check() {
        notes = edtkoment.getText().toString();
        alamat = edtAlamat.getText().toString();
        telp = edt_telp.getText().toString();
        String ttl = textTotal.getText().toString();
        totalorder = ttl.replace(",","");

        if (!onTheSpot) {
            edtAlamat.setError(null);
            edt_telp.setError(null);

            if (alamat.length() == 0) {
                edtAlamat.setError("Alamat masih kosong.");
                return false;
            }
            if (telp.length() == 0) {
                edt_telp.setError("Telp masih kosong.");
                return false;
            }
            if (meter > 20) {
                Toast.makeText(CartActivity.this, "Maximum 20 km..", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    private void KirimOrder() {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("KONFIRMASI CHECKOUT");
        dialog.setContentView(R.layout.confirm_order);
        dialog.setCancelable(false);
        ListView listview = (ListView) dialog.findViewById(R.id.listView);
        ConfirmAdapter adapter = new ConfirmAdapter(this, cartList);
        listview.setAdapter(adapter);
        TextView teksTotal = (TextView) dialog.findViewById(R.id.total);
        TextView teksGrandTotal = (TextView) dialog.findViewById(R.id.grandtotal);
        TextView teksTotalItem = (TextView) dialog.findViewById(R.id.totalitem);
        TextView teksOngkir = (TextView) dialog.findViewById(R.id.teksOngkir);
        TextView valueOngkir = (TextView) dialog.findViewById(R.id.ongkir);
        TextView teksAlamat = (TextView) dialog.findViewById(R.id.alamat);
        TextView teksRekening = (TextView) dialog.findViewById(R.id.rekening);
        if (!onTheSpot) {
            teksOngkir.setVisibility(View.VISIBLE);
            valueOngkir.setVisibility(View.VISIBLE);
            valueOngkir.setText("Rp " + ongkir);
            teksAlamat.setVisibility(View.VISIBLE);
            teksAlamat.setText("Dikirim ke " + alamat);
        }
        teksRekening.setText("Silahkan melakukan pembayaran ke nomor rekening ini\n" + norek);
        teksTotal.setText("Rp " + totalorder);
        teksGrandTotal.setText("Rp " + txtsubtotl.getText().toString());
        teksTotalItem.setText(totalItem + "");
        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        Button ok = (Button) dialog.findViewById(R.id.ok);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading = ProgressDialog.show(CartActivity.this, "Kirim Order", "Please wait...", false, true);
                new SendOrder().execute();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    class SendOrder extends AsyncTask<Void,Void,Boolean> {
        String response;
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                URL url = new URL(URL_SEND);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpURLConnection.connect();

                StringBuilder sb = new StringBuilder();
                sb.append("[");
                for (Cart c : cartList)
                {
                    sb.append("{\"idMenu\":");
                    sb.append(c.getIdMenu());
                    sb.append(",\"quantity\":");
                    sb.append(c.getQuantity());
                    sb.append(",\"total\":");
                    sb.append(c.getTotal());
                    sb.append("},");
                }
                sb.deleteCharAt(sb.length() - 1);
                sb.append("]");
                String parameter = "idUser=" + userid + "&totalOrder=" + totalorder + "&notes=" + notes + "&onTheSpot=" + onTheSpot
                        + "&key=" + Constant.KEY + "&cartArray=" + sb.toString();

                if (!onTheSpot) {
                    parameter += "&ongkir=" + ongkir + "&alamat=" + alamat + "&telepon=" + telp + "&idAntar=" + idAntar + "&poin=" + poin;
                }
                if (idFlashDeal != null) {
                    parameter += "&idFlashDeal=" + idFlashDeal;
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
            cartList.clear();
            Constant.cartList.clear();
            Constant.poin = 0;
            Constant.jumlah = 0;
            if (result != null && result) {
                Toast.makeText(CartActivity.this, response, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(CartActivity.this, "Order gagal dikirim", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPermissionResult(String permission, boolean isGranted) {
        switch (permission) {
            case android.Manifest.permission.ACCESS_COARSE_LOCATION:
                if (!isGranted) {
                    easyPermission.requestPermission(CartActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
                }
                break;
        }
    }

    public void startLocationUpdates() {
        final ProgressDialog loading = ProgressDialog.show(CartActivity.this, "Cari Lokasi", "Sedang mencari lokasi pengguna", false, true);
        LocationServices.SettingsApi.checkLocationSettings(
                gps.mGoogleApiClient,
                gps.mLocationSettingsRequest
        ).setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        try {
                            LocationServices.FusedLocationApi.requestLocationUpdates(
                                    gps.mGoogleApiClient, gps.mLocationRequest, CartActivity.this);
                        } catch (SecurityException e) {
                            Toast.makeText(CartActivity.this, "Lokasi tidak terdeteksi", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade location settings");
                        try {
                            status.startResolutionForResult(CartActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        String errorMessage = "Location settings are inadequate, and cannot be fixed here. Fix in Settings.";
                        Toast.makeText(CartActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
                if (gps.mCurrentLocation != null) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    drawRoute();
                }
                loading.dismiss();
            }
        });
    }

    private void rekening() {
        JsonObjectRequest jsonKate = new JsonObjectRequest(Request.Method.GET, URL_REK, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject feedObj = response.getJSONObject("data");
                    norek = feedObj.getString("value");
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

    private void konfigurasi() {
        JsonObjectRequest jsonKate = new JsonObjectRequest(Request.Method.GET, URL_CONF, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject feedObj = response.getJSONObject("data");
                    perkm = feedObj.getInt("value");
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

    private void lokasiToko() {
        JsonObjectRequest jsonKate = new JsonObjectRequest(Request.Method.GET, URL_LOKASI, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject feedObj = response.getJSONObject("data");
                    latToko = feedObj.getDouble("latitude");
                    lngToko = feedObj.getDouble("long_latitude");
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

    private void ambilData() {
        adapter = new AdapterCart(cartList);
        list.setAdapter(adapter);
        list.setExpanded(true);
        TotalCheckOut();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSend:
                if (gps.mCurrentLocation == null && !cek.isChecked()) {
                    Toast.makeText(CartActivity.this, "Aktifkan fitur GPS untuk mengambil lokasi anda", Toast.LENGTH_SHORT).show();
                } else if (Check()){
                    KirimOrder();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        drawRoute();
    }

    public void drawRoute(){
        RouteDraw.getInstance(this,CartActivity.this).setFromLatLong(latitude, longitude)
                .setToLatLong(latToko, lngToko).setGmapAndKey("AIzaSyAKJSEmNQ9OtgKIzifjdNQBq50xb4cj_H0",mMap)
                .run();
        if (mMap != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("location"));
            mMap.addMarker(new MarkerOptions().position(new LatLng(latToko, lngToko)).title("Ke tujuan"));
        }
    }

    @Override
    public void afterDraw(String result) {
        try {
            //Convert string to jsona and parse
            JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);

            //parse total jarak
            JSONArray arr = routes.getJSONArray("legs");
            JSONObject a = arr.getJSONObject(0);
            JSONObject b = a.getJSONObject("distance");
            alamat = a.getString("start_address");
            meter = b.getInt("value");
            ongkir = ((int) Math.ceil(meter/ 1000.0)* perkm);
            edtAlamat.setText(alamat);
            txtOngkir.setText(formatduit.format(ongkir)+"");
            titleongkir.setText("Jarak ("+(int) Math.ceil(meter/ 1000.0)+" km x "+ formatduit.format(perkm) +")");
            subTotal = ongkir + totalBarang;
            txtsubtotl.setText(formatduit.format(subTotal)+"");
        } catch (JSONException e) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        gps.getLatitude();
                        gps.getLongitude();
                        break;
                }
                break;
        }
    }

    public class AdapterCart extends BaseAdapter {
        private LayoutInflater inflater;
        private List<Cart> list;
        DecimalFormat formatData = new DecimalFormat();

        public AdapterCart(List<Cart> list) {
            this.list = list;
        }

        @Override
        public int getCount() { return list.size(); }

        @Override
        public Object getItem(int location) { return list.get(location); }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Cart items = list.get(position);
            if (inflater == null)
                inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null)
                convertView = inflater.inflate(R.layout.produk_item, null);
            TextView nama = (TextView) convertView.findViewById(R.id.textproduk);
            nama.setText(items.getNamaMenu());

            TextView harga = (TextView) convertView.findViewById(R.id.textharga);
            String format = formatData.format(items.getHarga());
            harga.setText("Rp " + format);
            TextView satuan = (TextView) convertView.findViewById(R.id.satuan);
            satuan.setText(" / " + items.getSatuan());
            ImageView gambar = (ImageView) convertView.findViewById(R.id.gambar);
            if (!items.getGambar().equals("null")) {
                Glide.with(CartActivity.this).load(Constant.URLADMIN + items.getGambar())
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(gambar);
            } else {
                Glide.with(CartActivity.this).load(R.drawable.shop)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(gambar);
            }

            TextView qty = (TextView) convertView.findViewById(R.id.edtQty);
            // qty.setTag(item);
            qty.setText("" + items.getQuantity());

            final TextView txttotal = (TextView) convertView.findViewById(R.id.texTotal1);
            txttotal.setText(items.getTotal() + "");

            Button plus = (Button) convertView.findViewById(R.id.btnplus);
            Button min = (Button) convertView.findViewById(R.id.btnmin);
            // bagian button plus di tekan
            plus.setOnClickListener(new View.OnClickListener() {
                Cart qty = list.get(position);

                @Override
                public void onClick(View v) {
                    qty.setQuantity(qty.getQuantity() + 1);
                    Total = (qty.getQuantity() * qty.getHarga());
                    qty.setTotal(Total);
                    poin += qty.getPoin();
                    notifyDataSetChanged();
                    TotalCheckOut();
                }
            });

            // bagian button min di tekan
            min.setOnClickListener(new View.OnClickListener() {
                Cart qty = list.get(position);

                @Override
                public void onClick(View v) {
                    poin -= qty.getPoin();
                    if (qty.getQuantity() == 1) {
                        list.remove(position);
                        notifyDataSetChanged();
                        TotalCheckOut();
                    } else {
                        qty.setQuantity(qty.getQuantity() - 1);
                        Total = (qty.getQuantity() * qty.getHarga());
                        qty.setTotal(Total);
                        notifyDataSetChanged();
                        TotalCheckOut();
                    }
                    if (cartList.size() <= 0) {
                        btnSend.setEnabled(false);
                    }
                }
            });

            return convertView;
        }
    }

    public void TotalCheckOut() {
        int jumlahlist = list.getAdapter().getCount();
        totalBarang = 0;
        totalItem = 0;
        for (int i = 0; i < jumlahlist; i++) {
            View v = list.getAdapter().getView(i, null, null);
            TextView txt = (TextView) v.findViewById(R.id.texTotal1);
            TextView textproduk = (TextView) v.findViewById(R.id.textproduk);
            TextView quantity = (TextView) v.findViewById(R.id.edtQty);
            String stringItem = quantity.getText().toString();
            String namaproduk = textproduk.getText().toString();
            String stringTotal = txt.getText().toString();
            totalBarang += Integer.parseInt(stringTotal);
            totalItem += Integer.parseInt(stringItem);
        }
        textTotal.setText(formatduit.format(totalBarang)+"");
        subTotal = ongkir + totalBarang;
        txtsubtotl.setText(formatduit.format(subTotal)+"");
        textPoin.setText(poin + "");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // app icon in action bar clicked; go home
            gps.stopLocationUpdates();
            Constant.cartList = cartList;
            Constant.poin = poin;
            Constant.jumlah = totalItem;
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        if (gps.isConnected()) {
            startLocationUpdates();
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        gps.stopLocationUpdates();
        Constant.cartList = cartList;
        Constant.poin = poin;
        Constant.jumlah = totalItem;
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (list != null) {
            list.setAdapter(null);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        gps.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gps.isConnected()) {
            gps.stopLocationUpdates();
            Constant.cartList = cartList;
            Constant.poin = poin;
            Constant.jumlah = totalItem;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (gps.isConnected()) {
            gps.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        gps.onLocationChanged(location);
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();
        drawRoute();
    }
}
