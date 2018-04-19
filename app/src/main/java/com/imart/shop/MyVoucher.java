package com.imart.shop;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

public class MyVoucher extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    ListView listVoucher;
    SwipeRefreshLayout swipeRefreshLayout;
    Adapter adapter;
    ArrayList<Voucher> list = new ArrayList<>();
    String URL, URL_CANCEL, URL_CONFIRM;
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
        URL = Constant.URLADMIN + "api/voucher.php?key=" + Constant.KEY + "&tag=myvoucher&id=" + userid;
        URL_CANCEL = Constant.URLADMIN + "api/voucher.php?key=" + Constant.KEY + "&tag=cancel";
        URL_CONFIRM = Constant.URLADMIN + "api/voucher.php?key=" + Constant.KEY + "&tag=confirm";

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                ambilData();
            }
        });
        listVoucher.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, final int position, long arg3) {
                final Voucher voucher = (Voucher)parent.getItemAtPosition(position);
                final Dialog dialog = new Dialog(getActivity());
                final View view = listVoucher.getChildAt(position);
                if (voucher.getTerpakai() == 0) {
                    dialog.setContentView(R.layout.dialog_order_voucher);
                    dialog.setTitle("Kode Voucher");
                    Button button = (Button) dialog.findViewById(R.id.confirm);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText kode = (EditText) dialog.findViewById(R.id.kode);
                            String code = kode.getText().toString();
                            cekKode(voucher.getId(), code);
                            Button button = (Button) view.findViewById(R.id.button);
                            button.setVisibility(View.GONE);
                            view.setEnabled(false);
                            voucher.setTerpakai(2);
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            }
        });

        return view;
    }

    private void ambilData() {
        JsonObjectRequest jsonKate = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
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
                item.setGambar(feedObj.getString("gambar"));
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

    private void cekKode(int id, String kode) {
        StringRequest jsonKate = new StringRequest(URL_CONFIRM + "&id=" + id + "&kode=" + kode, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                ambilData();
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

    private void cancelVoucher(int id) {
        StringRequest jsonKate = new StringRequest(URL_CANCEL + "&id=" + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                ambilData();
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
                convertView = inflater.inflate(R.layout.list_voucher, null);
            final View v = convertView;
            TextView nama = (TextView) convertView.findViewById(R.id.nama);
            TextView poin = (TextView) convertView.findViewById(R.id.poin);
            TextView status = (TextView) convertView.findViewById(R.id.status);
            ImageView gambar = (ImageView) convertView.findViewById(R.id.gambar);
            final ImageView close = (ImageView) convertView.findViewById(R.id.close);
            final Button redeem = (Button)convertView.findViewById(R.id.button);
            final Voucher item = list.get(position);
            nama.setText(item.getNama());
            poin.setText(item.getPoin() + "");
            if (item.getTerpakai() == 0) {
                status.setText("BELUM DIGUNAKAN");
                status.setTextColor(Color.parseColor("#7bb241"));
                status.setVisibility(View.GONE);
                redeem.setVisibility(View.VISIBLE);
                v.setEnabled(true);
            } else if (item.getTerpakai() == 1) {
                status.setText("BATAL DIGUNAKAN");
                status.setTextColor(Color.parseColor("#ff0000"));
                status.setVisibility(View.VISIBLE);
                redeem.setVisibility(View.GONE);
                v.setEnabled(false);
            } else if (item.getTerpakai() == 2) {
                status.setText("TELAH DIGUNAKAN");
                status.setTextColor(Color.parseColor("#7bb241"));
                status.setVisibility(View.VISIBLE);
                redeem.setVisibility(View.GONE);
                v.setEnabled(false);
            }
            Glide.with(getActivity())
                    .load(Constant.URLADMIN + item.getGambar())
                    .placeholder(R.drawable.loading)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(gambar);
            redeem.setText("REDEEM");
            redeem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(getActivity());
                    if (item.getTerpakai() == 0) {
                        dialog.setContentView(R.layout.dialog_order_voucher);
                        dialog.setTitle("Kode Voucher");
                        Button button = (Button) dialog.findViewById(R.id.confirm);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditText kode = (EditText) dialog.findViewById(R.id.kode);
                                String code = kode.getText().toString();
                                cekKode(item.getId(), code);
                                redeem.setVisibility(View.GONE);
                                close.setVisibility(View.GONE);
                                v.setEnabled(false);
                                item.setTerpakai(2);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                }
            });
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setCancelable(false);
                    alert.setTitle("KONFIRMASI PEMBATALAN");
                    alert.setMessage("Anda yakin ingin membatalkan voucher ini? Poin yang terpakai pada voucher ini tidak dapat dikembalikan jika anda membatalkan voucher ini.");
                    alert.setPositiveButton("YA", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            loading = ProgressDialog.show(getActivity(), "Membatalkan Voucher", "Please wait...", false, true);
                            cancelVoucher(item.getId());
                            redeem.setVisibility(View.GONE);
                            close.setVisibility(View.GONE);
                            v.setEnabled(false);
                            item.setTerpakai(1);
                            dialog.dismiss();
                        }
                    });

                    alert.setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });
                    alert.show();
                }
            });
            return convertView;
        }
    }
}
