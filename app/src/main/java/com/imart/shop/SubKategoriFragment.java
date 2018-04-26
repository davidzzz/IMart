package com.imart.shop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
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

public class SubKategoriFragment extends Fragment {
    View view;
    GridView gridView;
    ArrayList<ItemSub> itemList;
    SubAdapter adapter;
    String URLKATE, akses, id;
    int colorValue;
    TextView countCart;
    SessionManager session;
    ProgressBar loading;
    HashMap<String, String> user;

    public SubKategoriFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_kategori, container, false);
        session = new SessionManager(getActivity().getApplicationContext());
        user = session.getUserDetails();
        akses = user.get(SessionManager.KEY_AKSES);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            colorValue = bundle.getInt("color", 0);
            id = bundle.getString("id");
        }
        gridView = (GridView) view.findViewById(R.id.gridView);
        loading = (ProgressBar) view.findViewById(R.id.prgLoading);
        itemList = new ArrayList<>();
        adapter = new SubAdapter(getActivity(), itemList, colorValue);
        gridView.setAdapter(adapter);
        URLKATE = Constant.URLAPI + "key=" + Constant.KEY + "&tag=subkat&id=" + id;
        daftarKategori();
        HashMap<String, String> user = session.getUserDetails();
        String akses = user.get(SessionManager.KEY_AKSES);
        if (akses.equals("1")) {
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                    ItemSub item = (ItemSub) parent.getItemAtPosition(position);
                    Intent intentlist = new Intent(getActivity(), ProdukActivity.class);
                    intentlist.putExtra("id", item.getId());
                    startActivity(intentlist);
                }
            });
        }
        return view;
    }

    private void daftarKategori() {
        JsonObjectRequest jsonKate = new JsonObjectRequest(Request.Method.GET, URLKATE, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                itemList.clear();
                parseJsonKategory(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.setVisibility(View.GONE);
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
                item.setId(feedObj.getString("id"));
                item.setName(feedObj.getString("nama"));
                item.setImage(feedObj.getString("gambar"));
                itemList.add(item);
            }
        } catch (JSONException e) {
        }
        loading.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }
}
