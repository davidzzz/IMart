package com.imart.shop;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.imart.shop.adapter.AdapterSeller;
import com.imart.shop.app.myapp;
import com.imart.shop.model.ItemMenu;
import com.imart.shop.util.Constant;
import com.imart.shop.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListSeller extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    private List<ItemMenu> itemList;
    private ItemMenu object;
    private ListView list;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AdapterSeller adapter;
    SessionManager session;
    HashMap<String, String> user;
    String URL, userid;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_seller);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        getSupportActionBar().setTitle("Your Product");
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
        userid = user.get(session.KEY_PASSENGER_ID);
        list = (ListView) findViewById(R.id.ListView1);
        itemList = new ArrayList<ItemMenu>();
        adapter = new AdapterSeller(this, itemList);
        URL = Constant.URLADMIN + "api/list.php?key=" + Constant.KEY + "&tag=list";
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                ambilData();
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                object = itemList.get(position);
                Intent edit = new Intent(ListSeller.this, AddProduk.class);
                edit.putExtra("id", object.getIdMenu());
                edit.putExtra("nama", object.getNamaMenu());
                edit.putExtra("harga", object.getHarga());
                startActivity(edit);
            }
        });
    }

    private void ambilData() {
        JsonObjectRequest jsonKate = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                itemList.clear();
                parseJson(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        jsonKate.setRetryPolicy(new DefaultRetryPolicy(5000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myapp.getInstance().addToRequestQueue(jsonKate);
    }

    private void parseJson(JSONObject response) {
        try {
            JSONArray feedArray = response.getJSONArray("data");
            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);
                ItemMenu item = new ItemMenu();
                item.setIdMenu(feedObj.getString("produk_id"));
                item.setNamaMenu(feedObj.getString("nama_produk"));
                item.setHarga(feedObj.getInt("harga"));
                itemList.add(item);
            }
            list.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        if (itemList != null) {
            itemList.clear();
            ambilData();
        } else {
            ambilData();
        }
    }

    @Override
    public void onResume() {
        ambilData();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_seller, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.addItem) {
            Intent edit = new Intent(ListSeller.this, AddProduk.class);
            startActivity(edit);
            return true;
        }

        if (id == android.R.id.home) {
            // app icon in action bar clicked; go home
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (list != null) {
            list.setAdapter(null);
        }
    }
}
