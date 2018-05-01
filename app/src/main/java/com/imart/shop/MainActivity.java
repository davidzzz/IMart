package com.imart.shop;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import com.imart.shop.app.myapp;
import com.imart.shop.util.Constant;
import com.imart.shop.util.EasyPermission;
import com.imart.shop.util.SessionManager;

import android.Manifest;
import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        EasyPermission.OnPermissionResult, SearchView.OnQueryTextListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private EasyPermission easyPermission;
    SessionManager session;
    HashMap<String, String> user;
    String akses,fcmid,id,URL_TOKEN,msg;
    String URL;
    float r;
    TextView countCart;
    Timer timer = new Timer();
    boolean reset = true;
    Long time = 0L;
    MainFragment mainFragment = new MainFragment();
    SearchFragment searchFragment = new SearchFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //jika os android di atas lolipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            easyPermission = new EasyPermission();
            easyPermission.requestPermission(this, Manifest.permission.READ_PHONE_STATE);
        }
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
        akses = user.get(SessionManager.KEY_AKSES);
        checkPlayServices();
        //jika user login
        if (isLogin()) {
            fcmid = user.get(SessionManager.KEY_FCM);
            id =  user.get(SessionManager.KEY_PASSENGER_ID);

                Log.e(TAG,"fcmid null dan mulai ambil dari pref");
                SharedPreferences pref = getApplicationContext().getSharedPreferences("firebaseid", 0);
                String regId = pref.getString("regId", null);
                if(regId.length()!=0){//jika tidak kosong
                    UpdateFcmId(regId);
                }
                Log.e(TAG,"ini id firebase "+regId);
        }
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
        super.onBackPressed();
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.app_name);
        alert.setIcon(R.drawable.ic_launcher);
        alert.setMessage("Tutup Aplikasi ini ??");
        alert.setPositiveButton("YA KELUAR ", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {finish();
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
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) searchView.findViewById(id);
        textView.setTextColor(Color.BLACK);
        id = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View v = searchView.findViewById(id);
        v.setBackgroundColor(Color.WHITE);
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