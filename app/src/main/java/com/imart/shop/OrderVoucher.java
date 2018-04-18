package com.imart.shop;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.imart.shop.app.myapp;
import com.imart.shop.model.Voucher;
import com.imart.shop.util.Constant;
import com.imart.shop.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderVoucher extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    ListView listVoucher;
    SwipeRefreshLayout swipeRefreshLayout;
    Adapter adapter;
    ArrayList<Voucher> list = new ArrayList<>();
    String URL;
    ProgressDialog loading;
    SessionManager session;
    HashMap<String, String> user;
    String userid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voucher_list, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        listVoucher = (ListView) view.findViewById(R.id.list_voucher);
        adapter = new Adapter(list);
        session = new SessionManager(getActivity());
        user = session.getUserDetails();
        userid = user.get(session.KEY_PASSENGER_ID);
        URL = Constant.URLADMIN + "api/voucher.php?key=" + Constant.KEY + "&tag=ordervoucher";

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                ambilData();
            }
        });

        return view;
    }

    private void ambilData() {
        JsonObjectRequest jsonKate = new JsonObjectRequest(URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                list.clear();
                parseJsonKategory(response);
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

    private void parseJsonKategory(JSONObject response) {
        try {
            JSONArray feedArray = response.getJSONArray("data");
            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);
                Voucher item = new Voucher();
                item.setId(feedObj.getInt("id"));
                item.setNama(feedObj.getString("nama"));
                item.setUser(feedObj.getString("user"));
                item.setKode(feedObj.getString("kode"));
                item.setPoin(feedObj.getInt("poin"));
                item.setTanggal(feedObj.getString("tanggal"));
                item.setTerpakai(feedObj.getInt("terpakai"));
                list.add(item);
            }
            listVoucher.setAdapter(adapter);
        } catch (JSONException e) {
        }
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        ambilData();
    }

    public class Adapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<Voucher> list;

        public Adapter(List<Voucher> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int location) {
            return list.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (inflater == null)
                inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null)
                convertView = inflater.inflate(R.layout.list_order_voucher, null);
            TextView nama = (TextView) convertView.findViewById(R.id.nama);
            TextView poin = (TextView) convertView.findViewById(R.id.poin);
            TextView status = (TextView) convertView.findViewById(R.id.status);
            TextView user = (TextView) convertView.findViewById(R.id.user);
            TextView kode = (TextView) convertView.findViewById(R.id.kode);
            TextView tanggal = (TextView) convertView.findViewById(R.id.tanggal);
            final Voucher item = list.get(position);
            nama.setText(item.getNama());
            poin.setText(item.getPoin() + " POINTS");
            if (item.getTerpakai() == 0) {
                status.setText("BELUM DIGUNAKAN");
                status.setBackgroundResource(R.color.cpb_blue);
            } else if (item.getTerpakai() == 1) {
                status.setText("BATAL DIGUNAKAN");
                status.setBackgroundResource(R.color.cpb_red);
            } else if (item.getTerpakai() == 2) {
                status.setText("TELAH DIGUNAKAN");
                status.setBackgroundResource(R.color.cpb_green);
            }
            user.setText(item.getUser());
            kode.setText(item.getKode());
            tanggal.setText(item.getTanggal());
            return convertView;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }
    }
}
