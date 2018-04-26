package com.imart.shop;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.imart.shop.adapter.SubAdapter;
import com.imart.shop.app.myapp;
import com.imart.shop.model.FlashDeal;
import com.imart.shop.model.ItemSub;
import com.imart.shop.util.Constant;
import com.imart.shop.util.SessionManager;
import com.imart.shop.util.Utils;
import com.imart.shop.view.ExpandableHeightGridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    View view;
    //bagian toolbar
    SwipeRefreshLayout mSwipeRefreshLayout;
    //session manager disini
    SessionManager session;
    HashMap<String, String> user;
    String akses,id;
    private ExpandableHeightGridView gridView;
    private List<ItemSub> itemList;
    private ItemSub object;
    private SubAdapter adapter;
    private SliderLayout mDemoSlider;
    String URL, URLKATE, URLFD, URLSTAT, URLBANNER, URLDEL, nama_promo, gambar_promo, order_id;
    float r;
    TextView alert;
    Adapter flashDealAdapter;
    ArrayList<FlashDeal> listFlashDeal;
    RecyclerView recyclerView;
    int colorValue;
    LinearLayout layoutStatistik;

    public MainFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.kategori, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        konfigurasi();
        session = new SessionManager(getActivity().getApplicationContext());
        user = session.getUserDetails();
        akses = user.get(SessionManager.KEY_AKSES);
        layoutStatistik = (LinearLayout) view.findViewById(R.id.layout_statistik);
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
        adapter = new SubAdapter(getActivity(), itemList, colorValue);
        layoutStatistik.setVisibility(View.GONE);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                if (Utils.isConnectedToInternet(getActivity())) {
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
                    Intent intentlist = new Intent(getActivity(), SubKategoriActivity.class);
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
        recyclerView = (RecyclerView) view.findViewById(R.id.cardView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setAdapter(flashDealAdapter);
        recyclerView.setLayoutManager(layoutManager);

        return view;
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
                        final Dialog dialog = new Dialog(getActivity());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.banner);
                        ImageView gbr = (ImageView) dialog.findViewById(R.id.gambar);
                        Glide.with(getActivity()).load(Constant.URLADMIN + gambar)
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
                        final Dialog dialog = new Dialog(getActivity());
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
                loading = ProgressDialog.show(getActivity(),"Send Data...","Please wait...",false,true);
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
                Intent Menu = new Intent(getActivity(), MainActivity.class);
                Menu.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(Menu);
                getActivity().finish();
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
            } else {
                Toast.makeText(getActivity(), "Terjadi kesalahan saat mengisi rating", Toast.LENGTH_LONG).show();
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

                        TextSliderView textSliderView = new TextSliderView(getActivity());
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
                    TextView teksKategori = (TextView) getActivity().findViewById(R.id.total_kategori);
                    TextView teksProduk = (TextView) getActivity().findViewById(R.id.total_produk);
                    TextView teksOrder = (TextView) getActivity().findViewById(R.id.total_order);
                    TextView teksValue = (TextView) getActivity().findViewById(R.id.total_value);
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
            gridView.setHorizontalSpacing(0);
            gridView.setVerticalSpacing(0);
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
        final LinearLayout layoutFlashDeal = (LinearLayout) getActivity().findViewById(R.id.layout_flash_deal);
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
                        TextView waktu = (TextView) getActivity().findViewById(R.id.waktu);

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
                    RelativeLayout layout = (RelativeLayout) getActivity().findViewById(R.id.layout_main);
                    layout.setBackgroundColor(colorValue);
                    LinearLayout homeLayout = (LinearLayout) getActivity().findViewById(R.id.layout_home);
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
                    Intent i = new Intent(getActivity(), FlashDealActivity.class);
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
            Glide.with(getActivity()).load(Constant.URLADMIN + fd.getGambar())
                    .placeholder(R.drawable.loading)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.gambar);
        }
        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public boolean isLogin() {
        SessionManager sessionManager = new SessionManager(getActivity());
        return sessionManager.isLoggedIn();
    }

    @Override
    public void onResume() {
        mDemoSlider.startAutoCycle();
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
    public void onDestroy() {
        gridView.setAdapter(null);
        mDemoSlider.stopAutoCycle();
        Glide.get(getActivity()).clearMemory();
        super.onDestroy();
    }
}
