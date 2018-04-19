package com.imart.shop;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.imart.shop.app.myapp;
import com.imart.shop.model.Voucher;
import com.imart.shop.util.Constant;
import com.imart.shop.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class VoucherList extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    ListView listVoucher;
    SwipeRefreshLayout swipeRefreshLayout;
    Adapter adapter;
    ArrayList<Voucher> list = new ArrayList<>();
    String URL, URL_SEND;
    ProgressDialog loading;
    SessionManager session;
    HashMap<String, String> user;
    String userid, akses;
    int poin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voucher_list, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        listVoucher = (ListView) view.findViewById(R.id.list_voucher);
        adapter = new Adapter(list);
        URL = Constant.URLADMIN + "api/voucher.php?key=" + Constant.KEY + "&tag=list";
        URL_SEND = Constant.URLADMIN + "api/voucher.php?key=" + Constant.KEY + "&tag=buy";
        session = new SessionManager(getActivity());
        user = session.getUserDetails();
        poin = Integer.parseInt(user.get(SessionManager.KEY_POIN));
        userid = user.get(session.KEY_PASSENGER_ID);
        akses = user.get(SessionManager.KEY_AKSES);

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
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                Voucher v = (Voucher) parent.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), VoucherDetail.class);
                intent.putExtra("id", v.getId());
                startActivity(intent);
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
                item.setTanggal(feedObj.getString("tanggal_expired"));
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
                convertView = inflater.inflate(R.layout.list_voucher, null);
            TextView teks_nama = (TextView) convertView.findViewById(R.id.nama);
            TextView teks_poin = (TextView) convertView.findViewById(R.id.poin);
            ImageView gambar = (ImageView) convertView.findViewById(R.id.gambar);
            final Voucher item = list.get(position);
            teks_nama.setText(item.getNama());
            teks_poin.setText(item.getPoin() + " POINTS");
            Glide.with(getActivity())
                    .load(Constant.URLADMIN + item.getGambar())
                    .placeholder(R.drawable.loading)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(gambar);
            Button buy = (Button)convertView.findViewById(R.id.button);
            buy.setText("BUY");
            if (akses.equals("1")) {
                buy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        alert.setCancelable(false);
                        alert.setTitle("KONFIRMASI PEMBELIAN");
                        alert.setMessage("Anda yakin ingin membeli voucher ini?");
                        alert.setPositiveButton("YA", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Calendar now = Calendar.getInstance();
                                now.set(Calendar.MILLISECOND, 0);
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.YEAR, Integer.parseInt(item.getTanggal().substring(0, 4)));
                                c.set(Calendar.MONTH, Integer.parseInt(item.getTanggal().substring(5, 7)));
                                c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(item.getTanggal().substring(8, 10)));
                                c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(item.getTanggal().substring(11, 13)));
                                c.set(Calendar.MINUTE, Integer.parseInt(item.getTanggal().substring(14, 16)));
                                c.set(Calendar.SECOND, Integer.parseInt(item.getTanggal().substring(17)));
                                c.set(Calendar.MILLISECOND, 0);
                                if (poin < item.getPoin()) {
                                    Toast.makeText(getActivity(), "Poin anda tidak mencukupi untuk membeli voucher ini.", Toast.LENGTH_SHORT).show();
                                } else if (now.compareTo(c) > 0) {
                                    Toast.makeText(getActivity(), "Voucher ini telah kedaluwarsa.", Toast.LENGTH_SHORT).show();
                                } else {
                                    loading = ProgressDialog.show(getActivity(), "Membeli Voucher", "Please wait...", false, true);
                                    new BuyVoucher(item.getId(), item.getPoin()).execute();
                                }
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
            } else {
                buy.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    class BuyVoucher extends AsyncTask<Void,Void,Boolean> {
        String response;
        int id, poinVoucher;

        public BuyVoucher(int id, int poin){
            this.id = id;
            this.poinVoucher = poin;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                java.net.URL url = new URL(URL_SEND);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpURLConnection.connect();

                String parameter = "idUser=" + userid + "&idVoucher=" + id + "&poin=" + poinVoucher + "&key=" + Constant.KEY;

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
                return true;
            } catch (Exception e) {
                loading.dismiss();
                return false;
            }
        }

        @Override
        public void onPostExecute(Boolean result) {
            if (result != null && result) {
                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                int point = Integer.parseInt(user.get(SessionManager.KEY_POIN));
                session.updateValue(SessionManager.KEY_POIN, String.valueOf(point - poinVoucher));
                poin = point - poinVoucher;
            } else {
                Toast.makeText(getActivity(), "Voucher tidak berhasil dibeli", Toast.LENGTH_LONG).show();
            }
        }
    }
}
