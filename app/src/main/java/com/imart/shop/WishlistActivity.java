package com.imart.shop;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.imart.shop.app.myapp;
import com.imart.shop.model.Wishlist;
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
import java.util.HashMap;
import java.util.List;

public class WishlistActivity extends AppCompatActivity {
    private Toolbar toolbar;
    EditText namaProduk, merek;
    Button send;
    String URL_SEND, userid, akses, produk, brand;
    SessionManager session;
    HashMap<String, String> user;
    ProgressDialog loading;
    int colorValue;
    ListView listView;
    ArrayList<Wishlist> list = new ArrayList<>();
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("WISHLIST");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Constant.COLOR));
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Constant.COLOR);
        } else {
            window.setTitleColor(Constant.COLOR);
        }
        colorValue = getIntent().getIntExtra("color", 0);
        LinearLayout layout = (LinearLayout)findViewById(R.id.activity_wishlist);
        layout.setBackgroundColor(colorValue);
        URL_SEND = Constant.URLADMIN + "api/wishlist.php";
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
        userid = user.get(session.KEY_PASSENGER_ID);
        akses = user.get(SessionManager.KEY_AKSES);
        LinearLayout layout_form = (LinearLayout) findViewById(R.id.form_wishlist);
        listView = (ListView) findViewById(R.id.list_wishlist);
        if (akses.equals("1")) {
            layout_form.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            namaProduk = (EditText) findViewById(R.id.nama_produk);
            merek = (EditText) findViewById(R.id.merek);
            send = (Button) findViewById(R.id.send);
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    produk = namaProduk.getText().toString();
                    brand = merek.getText().toString();
                    if (produk.equals("")) {
                        Toast.makeText(WishlistActivity.this, "Nama Produk tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    } else if (brand.equals("")) {
                        Toast.makeText(WishlistActivity.this, "Merek tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    } else {
                        loading = ProgressDialog.show(WishlistActivity.this, "Kirim Wishlist", "Please wait...", false, true);
                        new SendWishlist().execute();
                    }
                }
            });
        } else {
            layout_form.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            adapter = new Adapter(list);
            ambilData();
        }
    }

    class SendWishlist extends AsyncTask<Void,Void,Boolean> {
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

                String parameter = "idUser=" + userid + "&namaProduk=" + produk + "&merek=" + brand + "&key=" + Constant.KEY;

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
                Toast.makeText(WishlistActivity.this, "Wishlist berhasil dikirim", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(WishlistActivity.this, "Wishlist gagal dikirim", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void ambilData() {
        String URL = Constant.URLADMIN + "api/wishlist.php?key=" + Constant.KEY + "&tag=list";
        loading = ProgressDialog.show(this, "Loading", "Sedang mengambil data", false, true);
        JsonObjectRequest jsonKate = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                list.clear();
                parseJsonKategory(response);
                loading.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
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
                Wishlist wishlist = new Wishlist();
                wishlist.setNama(feedObj.getString("nama"));
                wishlist.setProduk(feedObj.getString("nama_produk"));
                wishlist.setMerek(feedObj.getString("merek"));
                wishlist.setWaktu(feedObj.getString("waktu"));
                list.add(wishlist);
            }
            listView.setAdapter(adapter);
        } catch (JSONException e) {
        }
        adapter.notifyDataSetChanged();
    }

    public class Adapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<Wishlist> list;

        public Adapter(List<Wishlist> list) {
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
                inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null)
                convertView = inflater.inflate(R.layout.list_wishlist, null);
            Wishlist w = list.get(position);
            TextView nama = (TextView) convertView.findViewById(R.id.nama);
            nama.setText(w.getNama());
            TextView produk = (TextView) convertView.findViewById(R.id.nama_produk);
            produk.setText(w.getProduk());
            TextView merek = (TextView) convertView.findViewById(R.id.merek);
            merek.setText(w.getMerek());
            TextView waktu = (TextView) convertView.findViewById(R.id.waktu);
            waktu.setText(w.getWaktu());
            return convertView;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // app icon in action bar clicked; go home
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
