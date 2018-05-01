package com.imart.shop;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.imart.shop.adapter.SubAdapter;
import com.imart.shop.app.myapp;
import com.imart.shop.model.ItemSub;
import com.imart.shop.util.Constant;
import com.imart.shop.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class KategoriActivity extends BaseActivity implements SearchView.OnQueryTextListener {
    Toolbar mToolbar;
    SubAdapter adapter;
    String akses;
    int colorValue;
    TextView countCart;
    SessionManager session;
    ProgressBar loading;
    HashMap<String, String> user;
    Timer timer = new Timer();
    boolean reset = true;
    Long time = 0L;
    KategoriFragment kategoriFragment = new KategoriFragment();
    SearchFragment searchFragment = new SearchFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kategori);
        getSupportActionBar().setTitle("KATEGORI");
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
        akses = user.get(SessionManager.KEY_AKSES);
        colorValue = getIntent().getIntExtra("color", 0);
        LinearLayout layout = (LinearLayout)findViewById(R.id.activity_kategori);
        layout.setBackgroundColor(colorValue);
        Bundle bundle = new Bundle();
        bundle.putInt("color", colorValue);
        kategoriFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.pager, kategoriFragment);
        transaction.commit();
    }

    public TextView getCountCart(){
        return countCart;
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
        if (akses.equals("1")) {
            MenuItemCompat.setActionView(shop, R.layout.badge);
            RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(shop);
            RelativeLayout layout = (RelativeLayout) notifCount.findViewById(R.id.badge_layout);
            countCart = (TextView) notifCount.findViewById(R.id.badge);
            countCart.setText(Constant.jumlah + "");
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Constant.cartList.size() > 0) {
                        Intent i = new Intent(KategoriActivity.this, CartActivity.class);
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
            transaction.replace(R.id.pager, kategoriFragment);
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
        if (id == android.R.id.home) {
            // app icon in action bar clicked; go home
            cekData();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        cekData();
    }

    private void cekData() {
        if (searchFragment.cartList.size() > 0) {
            Constant.cartList = searchFragment.cartList;
            Constant.poin = searchFragment.poin;
            Constant.jumlah = searchFragment.total_item;
        }
        finish();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }

    @Override
    public void onResume() {
        if (countCart != null) {
            countCart.setText(Constant.jumlah + "");
        }
        super.onResume();
    }
}
