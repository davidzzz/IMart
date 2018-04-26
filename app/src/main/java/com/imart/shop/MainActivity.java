package com.imart.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import com.imart.shop.adapter.DrawerMenuItemAdapter;
import com.imart.shop.app.myapp;
import com.imart.shop.model.DrawerMenuItem;
import com.imart.shop.util.Constant;
import com.imart.shop.util.EasyPermission;
import com.imart.shop.util.SessionManager;
import com.imart.shop.view.RoundImage;

import android.Manifest;
import android.support.v4.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        EasyPermission.OnPermissionResult, SearchView.OnQueryTextListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private EasyPermission easyPermission;
    private Toolbar mToolbar;
    //bagian toolbar
    private DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    ListView mLvDrawerMenu;
    List<DrawerMenuItem> menuItems;
    DrawerMenuItemAdapter mDrawerMenuAdapter;
    //session manager disini
    SessionManager session;
    HashMap<String, String> user;
    String photo, akses,fcmid,id,URL_TOKEN,msg;
    String URL;
    float r;
    TextView countCart;
    int colorValue;
    Timer timer = new Timer();
    boolean reset = true;
    Long time = 0L;
    MainFragment mainFragment = new MainFragment();
    SearchFragment searchFragment = new SearchFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Constant.COLOR));

        //jika os android di atas lolipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            easyPermission = new EasyPermission();
            easyPermission.requestPermission(this, Manifest.permission.READ_PHONE_STATE);
        }
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
        akses = user.get(SessionManager.KEY_AKSES);
        checkPlayServices();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLvDrawerMenu = (ListView) findViewById(R.id.lv_drawer_menu);
        View headerView = getLayoutInflater().inflate(R.layout.header_drawer, mLvDrawerMenu, false);
        LinearLayout lytHd = (LinearLayout) headerView.findViewById(R.id.lytHeader);
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
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.pager, mainFragment);
        transaction.commit();
    }

    public TextView getCountCart(){
        return countCart;
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
        MenuItem item = menu.findItem(R.id.search);
        MenuItemCompat.setActionView(item, R.layout.search);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        searchView.setIconified(false);
        searchView.setBackgroundResource(R.drawable.edittext_top_bg);
        searchView.setOnQueryTextListener(this);
        MenuItem shop = menu.findItem(R.id.shop);
        if (akses != null && akses.equals("1")) {
            MenuItemCompat.setActionView(shop, R.layout.badge);
            RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(shop);
            RelativeLayout layout = (RelativeLayout) notifCount.findViewById(R.id.badge_layout);
            countCart = (TextView) notifCount.findViewById(R.id.badge);
            countCart.setText(Constant.jumlah + "");
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Constant.cartList.size() > 0) {
                        Intent i = new Intent(MainActivity.this, CartActivity.class);
                        i.putExtra("poin", Constant.poin);
                        i.putParcelableArrayListExtra("cartList", Constant.cartList);
                        startActivity(i);
                    }
                }
            });
        } else {
            item.setVisible(false);
            shop.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchFragment.getData(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(final String text) {
        if (text.length() > 0) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.pager, searchFragment);
            transaction.commit();
        } else if (text.length() == 0) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.pager, mainFragment);
            transaction.commit();
        }
        Long tsLong = System.currentTimeMillis()/1000;
        if (time != 0L && tsLong - time < 1) {
            reset = false;
            time = tsLong;
        } else {
            reset = true;
        }
        timer.cancel();
        timer = new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (reset) {
                            searchFragment.getData(text);
                            time = 0L;
                            reset = true;
                        }
                    }
                },
                1000
        );
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
        //mDemoSlider.startAutoCycle();
        if (countCart != null) {
            countCart.setText(Constant.jumlah + "");
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mLvDrawerMenu.setAdapter(null);
        timer.cancel();
        super.onDestroy();
    }
}