package com.imart.shop;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import com.imart.shop.adapter.DrawerMenuItemAdapter;
import com.imart.shop.adapter.SubAdapter;
import com.imart.shop.app.myapp;
import com.imart.shop.model.DrawerMenuItem;
import com.imart.shop.model.FlashDeal;
import com.imart.shop.model.ItemSub;
import com.imart.shop.util.Constant;
import com.imart.shop.util.EasyPermission;
import com.imart.shop.util.SessionManager;
import com.imart.shop.util.Utils;
import com.imart.shop.view.ExpandableHeightGridView;
import com.imart.shop.view.RoundImage;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, EasyPermission.OnPermissionResult, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private EasyPermission easyPermission;
    private Toolbar mToolbar;
    //bagian toolbar
    private DrawerLayout mDrawerLayout;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ActionBarDrawerToggle mDrawerToggle;
    ListView mLvDrawerMenu;
    List<DrawerMenuItem> menuItems;
    DrawerMenuItemAdapter mDrawerMenuAdapter;
    //session manager disini
    SessionManager session;
    HashMap<String, String> user;
    String photo, akses,fcmid,id,URL_TOKEN,msg;
    private ExpandableHeightGridView gridView;
    private List<ItemSub> itemList;
    private ItemSub object;
    private SubAdapter adapter;
    private SliderLayout mDemoSlider;
    String URL, URLKATE, URLFD, URLSTAT, URLBANNER, URLDEL, nama_promo, gambar_promo, order_id;
    float r;
    TextView alert, countCart;
    Adapter flashDealAdapter;
    ArrayList<FlashDeal> listFlashDeal;
    RecyclerView recyclerView;
    int colorValue;
    LinearLayout layoutStatistik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.layout_kategori);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setTitle(getString(R.string.app_name));
        getSupportActionBar().setIcon(R.drawable.logo);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Constant.COLOR));

        //jika os android di atas lolipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            easyPermission = new EasyPermission();
            easyPermission.requestPermission(this, Manifest.permission.READ_PHONE_STATE);
        }
        konfigurasi();
        checkPlayServices();
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();

        akses = user.get(SessionManager.KEY_AKSES);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLvDrawerMenu = (ListView) findViewById(R.id.lv_drawer_menu);
        View headerView = getLayoutInflater().inflate(R.layout.header_drawer, mLvDrawerMenu, false);
        LinearLayout lytHd = (LinearLayout) headerView.findViewById(R.id.lytHeader);
        layoutStatistik = (LinearLayout) findViewById(R.id.layout_statistik);
        ImageView imghd = (ImageView) headerView.findViewById(R.id.image);
        TextView namaHeader = (TextView) headerView.findViewById(R.id.nama);
        TextView desHeader = (TextView) headerView.findViewById(R.id.des);
        //jika user login
        if (isLogin()) {
            lytHd.setVisibility(View.VISIBLE);
            namaHeader.setText(user.get(SessionManager.KEY_NAME));
            desHeader.setText(user.get(SessionManager.KEY_EMAIL));
            photo = user.get(SessionManager.KEY_PHOTO);
            fcmid = user.get(SessionManager.KEY_FCM);
            id =  user.get(SessionManager.KEY_PASSENGER_ID);

                Log.e(TAG,"fcmid null dan mulai ambil dari pref");
                SharedPreferences pref = getApplicationContext().getSharedPreferences("firebaseid", 0);
                String regId = pref.getString("regId", null);
                if(regId.length()!=0){//jika tidak kosong
                    UpdateFcmId(regId);
                }
                Log.e(TAG,"ini id firebase "+regId);

            Glide.with(this)
                    .load(photo != null && !photo.equals("-") ? photo : R.drawable.user)
                    .transform(new RoundImage(MainActivity.this))
                    .into(imghd);
        } else {
            lytHd.setVisibility(View.GONE);
        }
        mLvDrawerMenu.addHeaderView(headerView);
        menuItems = new ArrayList<DrawerMenuItem>();
        //buat list beda klo login dan tidak login
        DrawerMenuItem test1 = new DrawerMenuItem(1, "Profile", R.drawable.qr);
        DrawerMenuItem test2 = new DrawerMenuItem(2, "Voucher / Point", R.drawable.voucher);
        DrawerMenuItem test3 = new DrawerMenuItem(3, "My Flash Deal", R.drawable.flash_deal);
        DrawerMenuItem test4 = new DrawerMenuItem(4, "Kategori", R.drawable.category);
        DrawerMenuItem test5 = new DrawerMenuItem(5, "History", R.drawable.history);
        DrawerMenuItem test6 = new DrawerMenuItem(6, "About", R.drawable.about_black);
        DrawerMenuItem test7 = new DrawerMenuItem(7, isLogin() ? "Logout" : "Login", R.drawable.user);
        menuItems.add(test1);
        menuItems.add(test2);
        menuItems.add(test3);
        menuItems.add(test4);
        menuItems.add(test5);
        menuItems.add(test6);
        menuItems.add(test7);
        if (!isLogin()) {
            DrawerMenuItem test8 = new DrawerMenuItem(8, "Daftar", R.drawable.user);
            menuItems.add(test8);
        }
        mDrawerMenuAdapter = new DrawerMenuItemAdapter(MainActivity.this, R.layout.layout_drawer_menu_item, menuItems);
        mLvDrawerMenu.setAdapter(mDrawerMenuAdapter);
        mLvDrawerMenu.setOnItemClickListener(this);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mDemoSlider = (SliderLayout) mSwipeRefreshLayout.findViewById(R.id.slider);
        alert = (TextView) mSwipeRefreshLayout.findViewById(R.id.txtAlert);
        URL = Constant.URLAPI + "key=" + Constant.KEY + "&tag=" + Constant.TAG_PROMO;
        URLKATE = Constant.URLAPI + "key=" + Constant.KEY + "&tag=" + Constant.TAG_SUB;
        URLFD = Constant.URLADMIN + "api/flash_deal.php?key=" + Constant.KEY + "&tag=list";
        URLSTAT = Constant.URLAPI + "key=" + Constant.KEY + "&tag=statistik";
        URLBANNER = Constant.URLAPI + "key=" + Constant.KEY + "&tag=banner";
        URLDEL = Constant.URLADMIN + "api/delivery.php?key=" + Constant.KEY + "&tag=list";
        gridView = (ExpandableHeightGridView) mSwipeRefreshLayout.findViewById(R.id.gridView1);
        itemList = new ArrayList<ItemSub>();
        adapter = new SubAdapter(this, itemList, colorValue);
        layoutStatistik.setVisibility(View.GONE);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                if (Utils.isConnectedToInternet(MainActivity.this)) {
                    SliderGet();
                } else {
                    mSwipeRefreshLayout.setRefreshing(false);
                    alert.setVisibility(View.VISIBLE);
                    gridView.setVisibility(View.GONE);
                }
            }
        });
        if (akses != null && akses.equals("1")) {
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    object = itemList.get(position);
                    Intent intentlist = new Intent(MainActivity.this, SubKategoriActivity.class);
                    intentlist.putExtra("color", colorValue);
                    intentlist.putExtra("nama", object.getName());
                    intentlist.putExtra("id", object.getId());
                    startActivity(intentlist);
                }
            });
        }
        if (Constant.init) {
            Banner();
            Constant.init = false;
        }
        if (isLogin()) {
            URLDEL += "&id=" + id;
            Delivery();
        }

        listFlashDeal = new ArrayList<>();
        flashDealAdapter = new Adapter(listFlashDeal);
        recyclerView = (RecyclerView) findViewById(R.id.cardView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setAdapter(flashDealAdapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void Banner() {
        JsonObjectRequest jsonKate = new JsonObjectRequest(Request.Method.GET, URLBANNER, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject feedObj = response.getJSONObject("data");
                    String aktif = feedObj.getString("aktif");
                    String gambar = feedObj.getString("gambar");
                    if (aktif.equals("1")) {
                        final Dialog dialog = new Dialog(MainActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.banner);
                        ImageView gbr = (ImageView) dialog.findViewById(R.id.gambar);
                        Glide.with(MainActivity.this).load(Constant.URLADMIN + gambar)
                                .diskCacheStrategy(DiskCacheStrategy.ALL).into(gbr);
                        ImageView close = (ImageView) dialog.findViewById(R.id.close);
                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
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

    private void Delivery() {
        JsonObjectRequest jsonKate = new JsonObjectRequest(Request.Method.GET, URLDEL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray feedArray = response.getJSONArray("data");
                    for (int i = 0; i < feedArray.length(); i++) {
                        JSONObject feedObj = (JSONObject) feedArray.get(i);
                        order_id = feedObj.getString("order_id");
                        final Dialog dialog = new Dialog(MainActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.delivery);
                        RatingBar rating = (RatingBar) dialog.findViewById(R.id.rating);
                        r = rating.getRating();
                        Button ok = (Button) dialog.findViewById(R.id.ok);
                        ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    new Accept().execute();
                                    dialog.dismiss();
                                }
                        });
                        dialog.show();
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

    class Accept extends AsyncTask<Void,Void,Boolean> {
        String response;
        ProgressDialog loading;
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String URL = Constant.URLADMIN + "api/delivery.php";
                loading = ProgressDialog.show(MainActivity.this,"Send Data...","Please wait...",false,true);
                java.net.URL url = new URL(URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpURLConnection.connect();
                String parameter = "order_id=" + order_id + "&rating=" + r + "&key=" + Constant.KEY;

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
                Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Terjadi kesalahan saat mengisi rating", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void SliderGet() {
        JsonObjectRequest jsonKate = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray feedArray = response.getJSONArray("data");

                    for (int i = 0; i < feedArray.length(); i++) {
                        JSONObject feedObj = (JSONObject) feedArray.get(i);
                        nama_promo = feedObj.getString("nama_promo");
                        gambar_promo = feedObj.getString("gambar_promo");

                        TextSliderView textSliderView = new TextSliderView(MainActivity.this);
                        textSliderView
                                .description(nama_promo)
                                .image(Constant.URLADMIN + gambar_promo)
                                .setScaleType(BaseSliderView.ScaleType.Fit);

                        mDemoSlider.addSlider(textSliderView);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (akses == null || akses.equals("1")) {
                    KateData();
                }
                if (akses != null && akses.equals("1")) {
                    daftarFlashDeal();
                } else if (akses != null && akses.equals("2")) {
                    statistik();
                } else {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSwipeRefreshLayout.setRefreshing(false);
                alert.setVisibility(View.VISIBLE);
            }
        });
        jsonKate.setRetryPolicy(new DefaultRetryPolicy(5000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myapp.getInstance().addToRequestQueue(jsonKate);

        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(5000);
    }

    private void KateData() {
        Cache cache = myapp.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(URLKATE);
        if (entry != null) {
            // fetch the data from cache
            try {
                String data = new String(entry.data, "UTF-8");
                try {
                    parseJsonKategory(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            // Creating a json array request
            JsonObjectRequest jsonKate = new JsonObjectRequest(Request.Method.GET, URLKATE, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // Dismissing progress dialog\
                    itemList.clear();
                    parseJsonKategory(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    alert.setVisibility(View.VISIBLE);
                    gridView.setVisibility(View.GONE);
                }
            });
            jsonKate.setRetryPolicy(new DefaultRetryPolicy(5000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            myapp.getInstance().addToRequestQueue(jsonKate);
        }
    }

    private void statistik() {
        JsonObjectRequest jsonKate = new JsonObjectRequest(Request.Method.GET, URLSTAT, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        DecimalFormat formatduit = new DecimalFormat();
                        JSONObject feedObj = response.getJSONObject("data");
                        String totalKategori = feedObj.getString("totalKategori");
                        String totalProduk = feedObj.getString("totalProduk");
                        String totalOrder = feedObj.getString("totalOrder");
                        String totalValue = feedObj.getString("totalValue");
                        TextView teksKategori = (TextView) findViewById(R.id.total_kategori);
                        TextView teksProduk = (TextView) findViewById(R.id.total_produk);
                        TextView teksOrder = (TextView) findViewById(R.id.total_order);
                        TextView teksValue = (TextView) findViewById(R.id.total_value);
                        teksKategori.setText(totalKategori);
                        teksProduk.setText(totalProduk);
                        teksOrder.setText(totalOrder);
                        teksValue.setText("Rp. " + formatduit.format(Double.parseDouble(totalValue)));
                        layoutStatistik.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                    }
                }
        }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    alert.setVisibility(View.VISIBLE);
                }
        });
        jsonKate.setRetryPolicy(new DefaultRetryPolicy(5000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myapp.getInstance().addToRequestQueue(jsonKate);
    }

    private void parseJsonKategory(JSONObject response) {
        try {
            JSONArray feedArray = response.getJSONArray("data");

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);
                ItemSub item = new ItemSub();
                item.setId(feedObj.getString("sub_id"));
                item.setName(feedObj.getString("nama_sub"));
                item.setImage(feedObj.getString("gambar"));

                itemList.add(item);
            }
            // notify data changes to list adapater
            gridView.setAdapter(adapter);
            gridView.setExpanded(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //listView.setExpanded(true);
        adapter.notifyDataSetChanged();
        alert.setVisibility(View.GONE);
        gridView.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void daftarFlashDeal() {
        final LinearLayout layoutFlashDeal = (LinearLayout) findViewById(R.id.layout_flash_deal);
        JsonObjectRequest jsonKate = new JsonObjectRequest(Request.Method.GET, URLFD, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String tanggal = "";
                listFlashDeal.clear();
                try {
                    JSONArray feedArray = response.getJSONArray("data");
                    tanggal = response.getString("tanggal");
                    for (int i = 0; i < feedArray.length(); i++) {
                        JSONObject feedObj = (JSONObject) feedArray.get(i);
                        FlashDeal item = new FlashDeal();
                        item.setId(feedObj.getString("id"));
                        item.setNama(feedObj.getString("nama_produk"));
                        item.setHarga(feedObj.getString("harga"));
                        item.setHargaAsli(feedObj.getString("harga_asli"));
                        item.setGambar(feedObj.getString("gambar"));
                        item.setSatuan(feedObj.getString("satuan"));
                        listFlashDeal.add(item);
                    }
                    recyclerView.setAdapter(flashDealAdapter);
                } catch (JSONException e) {
                }
                flashDealAdapter.notifyDataSetChanged();
                alert.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                if (tanggal.equals("")) {
                    layoutFlashDeal.setVisibility(View.GONE);
                } else {
                    layoutFlashDeal.setVisibility(View.VISIBLE);
                    Calendar today = Calendar.getInstance();
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.YEAR, Integer.parseInt(tanggal.substring(0, 4)));
                    c.set(Calendar.MONTH, Integer.parseInt(tanggal.substring(5, 7)) - 1);
                    c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tanggal.substring(8, 10)));
                    c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tanggal.substring(11, 13)));
                    c.set(Calendar.MINUTE, Integer.parseInt(tanggal.substring(14, 16)));
                    c.set(Calendar.SECOND, Integer.parseInt(tanggal.substring(17)));
                    long milliSecond = c.getTimeInMillis() - today.getTimeInMillis();
                    new CountDownTimer(milliSecond, 1000) {
                        TextView waktu = (TextView) findViewById(R.id.waktu);

                        public void onTick(long millisUntilFinished) {
                            long seconds = millisUntilFinished / 1000;
                            String day = ((seconds / 86400 < 10) ? "0" : "") + seconds / 86400;
                            seconds %= 86400;
                            String hour = ((seconds / 3600 < 10) ? "0" : "") + seconds / 3600;
                            seconds %= 3600;
                            String minute = ((seconds / 60 < 10) ? "0" : "") + seconds / 60;
                            seconds %= 60;
                            String second = (seconds < 10 ? "0" : "") + seconds;
                            waktu.setText(day + "D " + hour + ":" + minute + ":" + second);
                        }

                        public void onFinish() {
                            layoutFlashDeal.setVisibility(View.GONE);
                        }
                    }.start();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSwipeRefreshLayout.setRefreshing(false);
                alert.setVisibility(View.GONE);
                layoutFlashDeal.setVisibility(View.GONE);
            }
        });
        jsonKate.setRetryPolicy(new DefaultRetryPolicy(5000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myapp.getInstance().addToRequestQueue(jsonKate);
    }

    private void konfigurasi() {
        String URL_CONF = Constant.URLAPI + "key=" + Constant.KEY + "&tag=konfigurasi&id=5";
        JsonObjectRequest jsonKate = new JsonObjectRequest(Request.Method.GET, URL_CONF, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject feedObj = response.getJSONObject("data");
                    int color = feedObj.getInt("value");
                    colorValue = Color.parseColor("#" + String.format("%06x", color));
                    RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout_main);
                    layout.setBackgroundColor(colorValue);
                    LinearLayout homeLayout = (LinearLayout) findViewById(R.id.layout_home);
                    homeLayout.setBackgroundColor(colorValue);
                    alert.setBackgroundColor(colorValue);
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

    // cek play services
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void UpdateFcmId(String token) {
        URL_TOKEN = Constant.URLAPI + "key=" + Constant.KEY + "&tag=gcm" + "&id=" + id + "&token=" + token;

        JsonObjectRequest jsonLogin = new JsonObjectRequest(Request.Method.GET, URL_TOKEN, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Dismissing progress dialog
                        try {
                            JSONArray jsonArray = response.getJSONArray(Constant.USER_LOGIN_ARRAY);
                            JSONObject objJson = null;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                objJson = jsonArray.getJSONObject(i);
                                msg = objJson.getString(Constant.USER_LOGIN_MSG);
                                Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.USER_LOGIN_SUCESS);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        jsonLogin.setRetryPolicy(new DefaultRetryPolicy(5000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myapp.getInstance().addToRequestQueue(jsonLogin);
    }

    //bagian ketika di klik back presed di halaman utama akan tampil dialog
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mLvDrawerMenu)) {
            mDrawerLayout.closeDrawer(mLvDrawerMenu);
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.app_name);
            alert.setIcon(R.drawable.ic_launcher);
            alert.setMessage("Tutup Aplikasi ini ??");
            alert.setPositiveButton("YA KELUAR ", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    finish();
                }
            });

            alert.setNegativeButton("RATE APP", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    final String appName = getPackageName();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                    }
                }
            });
            alert.show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                mDrawerLayout.closeDrawer(mLvDrawerMenu);
                break;
            case 1:
                if (isLogin()) {
                    Intent profile = new Intent(this, MenuActivity.class);
                    startActivity(profile);
                } else {
                    Intent i = new Intent(this, LoginActivity.class);
                    startActivity(i);
                }
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                break;
            case 2:
                if (isLogin()) {
                    Intent intentVoucher = new Intent(this, TabActivity.class);
                    intentVoucher.putExtra("tipe", "voucher");
                    startActivity(intentVoucher);
                } else {
                    Intent i = new Intent(this, LoginActivity.class);
                    startActivity(i);
                }
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                break;
            case 3:
                if (isLogin()) {
                    Intent intentHistory = new Intent(this, TabActivity.class);
                    intentHistory.putExtra("tipe", "history");
                    intentHistory.putExtra("isFlashDeal", true);
                    startActivity(intentHistory);
                } else {
                    Intent i = new Intent(this, LoginActivity.class);
                    startActivity(i);
                }
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                break;
            case 4:
                if (isLogin()) {
                    Intent intent = new Intent(this, KategoriActivity.class);
                    intent.putExtra("color", colorValue);
                    startActivity(intent);
                } else {
                    Intent i = new Intent(this, LoginActivity.class);
                    startActivity(i);
                }
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                break;
            case 5:
                if (isLogin()) {
                    Intent intentHistory = new Intent(this, TabActivity.class);
                    intentHistory.putExtra("tipe", "history");
                    startActivity(intentHistory);
                } else {
                    Intent i = new Intent(this, LoginActivity.class);
                    startActivity(i);
                }
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                break;
            case 6:
                Intent intent = new Intent(this, HelpDetailActivity.class);
                intent.putExtra("id", 1);
                startActivity(intent);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                break;
            case 7:
                if (isLogin()) {
                    Logout();
                } else {
                    Intent i = new Intent(this, LoginActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.open_next, R.anim.close_next);
                }
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                break;
            case 8:
                if (!isLogin()) {
                    Intent i = new Intent(this, RegisterActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.open_next, R.anim.close_next);
                }
                break;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView gambar;
        public TextView nama, harga, hargaAsli;
        public String id;

        public MyViewHolder(View v) {
            super(v);
            gambar = (ImageView) v.findViewById(R.id.gambar);
            nama = (TextView) v.findViewById(R.id.nama);
            harga = (TextView) v.findViewById(R.id.harga);
            hargaAsli = (TextView) v.findViewById(R.id.harga_asli);
        }
    }

    public class Adapter extends RecyclerView.Adapter<MyViewHolder> {
        private ArrayList<FlashDeal> list;
        public Adapter(ArrayList<FlashDeal> Data) {
            list = Data;
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_flash_deals, parent, false);
            final MyViewHolder holder = new MyViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(MainActivity.this, FlashDealActivity.class);
                    i.putExtra("id", holder.id);
                    i.putExtra("nama", holder.nama.getText().toString());
                    startActivity(i);
                }
            });
            return holder;
        }
        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            FlashDeal fd = list.get(position);
            holder.id = fd.getId();
            holder.nama.setText(fd.getNama());
            holder.harga.setText("Rp. " + fd.getHarga() + " / " + fd.getSatuan());
            holder.hargaAsli.setText("Rp. " + fd.getHargaAsli() + " / " + fd.getSatuan(), TextView.BufferType.SPANNABLE);
            Spannable spannable = (Spannable)holder.hargaAsli.getText();
            spannable.setSpan(new StrikethroughSpan(), 0, holder.hargaAsli.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            Glide.with(MainActivity.this).load(Constant.URLADMIN + fd.getGambar())
                    .placeholder(R.drawable.loading)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.gambar);
        }
        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public void Logout() {
        SharedPreferences pref = getSharedPreferences(SessionManager.PREF_NAME, SessionManager.PRIVATE_MODE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        startActivity(i);

        finish();
    }

    //bagian ketika result permision
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        easyPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionResult(String permission, boolean isGranted) {
        switch (permission) {
            case Manifest.permission.READ_PHONE_STATE:
                if (isGranted) {
                    easyPermission.requestPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
                } else {
                    easyPermission.requestPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
                }
                break;
            case Manifest.permission.ACCESS_COARSE_LOCATION:
                if (isGranted) {
                    easyPermission.requestPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                } else {
                    easyPermission.requestPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                break;
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                if (isGranted) {
                    easyPermission.requestPermission(MainActivity.this, Manifest.permission.ACCESS_NETWORK_STATE);
                } else {
                    easyPermission.requestPermission(MainActivity.this, Manifest.permission.ACCESS_NETWORK_STATE);
                }
                break;
            case Manifest.permission.ACCESS_NETWORK_STATE:
                if (isGranted) {
                    easyPermission.requestPermission(MainActivity.this, Manifest.permission.GET_ACCOUNTS);
                } else {
                    easyPermission.requestPermission(MainActivity.this, Manifest.permission.GET_ACCOUNTS);
                }
                break;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.shop);
        if (akses != null && akses.equals("1")) {
            MenuItemCompat.setActionView(item, R.layout.badge);
            RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(item);
            RelativeLayout layout = (RelativeLayout) notifCount.findViewById(R.id.badge_layout);
            countCart = (TextView) notifCount.findViewById(R.id.badge);
            countCart.setText(Constant.jumlah + "");
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this, CartActivity.class);
                    i.putExtra("poin", Constant.poin);
                    i.putParcelableArrayListExtra("cartList", Constant.cartList);
                    startActivity(i);
                }
            });
        } else {
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.search) {
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isLogin() {
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        return sessionManager.isLoggedIn();
    }

    @Override
    public void onResume() {
        mDemoSlider.startAutoCycle();
        if (countCart != null) {
            countCart.setText(Constant.jumlah + "");
        }
        super.onResume();
    }

    @Override
    public void onStop() {
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onRefresh() {
        nama_promo = null;
        gambar_promo = null;
        mDemoSlider.removeAllSliders();
        SliderGet();
    }

    @Override
    protected void onDestroy() {
        gridView.setAdapter(null);
        mDemoSlider.stopAutoCycle();
        mLvDrawerMenu.setAdapter(null);
        Glide.get(this).clearMemory();
        super.onDestroy();
    }
}