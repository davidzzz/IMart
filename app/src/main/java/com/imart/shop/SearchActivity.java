package com.imart.shop;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.imart.shop.app.myapp;
import com.imart.shop.model.Cart;
import com.imart.shop.model.ItemMenu;
import com.imart.shop.util.Constant;
import com.imart.shop.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    Toolbar mToolbar;
    private GridView listview;
    private AdapterSinz adapter = null;
    private List<ItemMenu> arraylist;
    private ArrayList<Cart> cartList;
    private LinearLayout estimasi;
    String URL, URL_MENU, id;
    private static final int PRODUK_DETAIL = 100;
    private int poin = 0, total_item = 0, totalBarang = 0, currentPosition;

    TextView txtTotal, total_notif, countCart;
    DecimalFormat formatduit = new DecimalFormat();
    ProgressBar loading;
    boolean onActivityResult = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Constant.COLOR));
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Constant.COLOR);
        } else {
            window.setTitleColor(Constant.COLOR);
        }
        txtTotal = (TextView) findViewById(R.id.txtTotal);
        arraylist = new ArrayList<ItemMenu>();
        total_notif = (TextView) findViewById(R.id.total_notif);
        estimasi = (LinearLayout) findViewById(R.id.lytOrder);
        loading = (ProgressBar) findViewById(R.id.prgLoading);
        estimasi.setVisibility(View.GONE);
        estimasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchActivity.this, CartActivity.class);
                i.putExtra("poin", poin);
                i.putParcelableArrayListExtra("cartList", cartList);
                startActivity(i);
            }
        });
        id = getIntent().getStringExtra("id");
        listview = (GridView) findViewById(R.id.list_view);
        adapter = new AdapterSinz(this, arraylist);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                currentPosition = position;
                ItemMenu item = (ItemMenu) parent.getItemAtPosition(position);
                Intent i = new Intent(SearchActivity.this, ProdukDetailActivity.class);
                i.putExtra("id", item.getIdMenu());
                i.putExtra("nama", item.getNamaMenu());
                i.putExtra("porsi", item.getQuantity());
                startActivityForResult(i, PRODUK_DETAIL);
            }
        });
    }

    public boolean isLogin() {
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        return sessionManager.isLoggedIn();
    }

    private void getData() {
        arraylist.clear();
        JsonObjectRequest jsonKate = new JsonObjectRequest(URL_MENU, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
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
            JSONArray produkArray = response.getJSONArray("produk");
            for (int i = 0; i < produkArray.length(); i++) {
                JSONObject feedObj = (JSONObject) produkArray.get(i);
                ItemMenu item = new ItemMenu();
                item.setIdMenu(feedObj.getString("id"));
                item.setNamaMenu(feedObj.getString("nama"));
                item.setHarga(feedObj.getInt("harga"));
                item.setSatuan(feedObj.getString("satuan"));
                item.setPoin(feedObj.getInt("poin"));
                item.setDeskripsi(feedObj.getString("deskripsi"));
                item.setGambar(feedObj.getString("gambar"));
                arraylist.add(item);
            }
        } catch (JSONException e) {
        }
        adapter.notifyDataSetChanged();
        loading.setVisibility(View.GONE);
        loadCart();
    }

    public void loadCart(){
        cartList = Constant.cartList;
        poin = 0;
        total_item = 0;
        totalBarang = 0;
        for (int i = 0; i < cartList.size(); i++) {
            Cart c = cartList.get(i);
            totalBarang += (c.getQuantity() * c.getHarga());
            total_item += c.getQuantity();
            poin += (c.getQuantity() * c.getPoin());
        }
        for (int i = 0; i < arraylist.size(); i++) {
            ItemMenu item = (ItemMenu) adapter.getItem(i);
            Cart c = adapter.findCart(item.getIdMenu());
            if (c != null) {
                item.setQuantity(c.getQuantity());
                adapter.notifyDataSetChanged();
            }
        }
        if (cartList.size() == 0) {
            estimasi.setVisibility(View.GONE);
        } else {
            estimasi.setVisibility(View.VISIBLE);
            txtTotal.setText("Rp " + formatduit.format(totalBarang));
            total_notif.setText(total_item + "");
            if (countCart != null) {
                countCart.setText(total_item + "");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PRODUK_DETAIL:
                if (data != null) {
                    Cart c = data.getParcelableExtra("cart");
                    Cart cartLama = adapter.findCart(c.getIdMenu());
                    if (cartLama != null) {
                        cartList.remove(cartLama);
                        totalBarang -= (cartLama.getQuantity() * cartLama.getHarga());
                        total_item -= cartLama.getQuantity();
                        poin -= (cartLama.getQuantity() * cartLama.getPoin());
                    }
                    cartList.add(c);
                    ItemMenu item = (ItemMenu) adapter.getItem(currentPosition);
                    item.setQuantity(c.getQuantity());
                    adapter.notifyDataSetChanged();
                    totalBarang += (c.getQuantity() * c.getHarga());
                    total_item += c.getQuantity();
                    poin += (c.getQuantity() * c.getPoin());
                    if (totalBarang == 0) {
                        estimasi.setVisibility(View.GONE);
                    } else {
                        estimasi.setVisibility(View.VISIBLE);
                        txtTotal.setText("Rp " + formatduit.format(totalBarang));
                        total_notif.setText(total_item + "");
                        countCart.setText(total_item + "");
                    }
                }
                onActivityResult = true;
                break;
        }
    }

    public class AdapterSinz extends BaseAdapter {
        private Context context;
        private List<ItemMenu> list;
        private LayoutInflater inflater;
        DecimalFormat formatData = new DecimalFormat();

        public AdapterSinz(Context context, List<ItemMenu> list) {
            this.context = context;
            this.list = list;
        }

        public Cart findCart(String id) {
            for (int i = 0; i < cartList.size(); i++) {
                Cart cart = cartList.get(i);
                if (cart.getIdMenu().equals(id)) {
                    return cart;
                }
            }
            return null;
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
        public boolean isEnabled(int position) {
            return true;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ItemMenu item = list.get(position);
            if (inflater == null)
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null)
                convertView = inflater.inflate(R.layout.daftar_produk, null);
            final TextView qty = (TextView) convertView.findViewById(R.id.edtQty);
            qty.setText("" + item.getQuantity());
            TextView nama = (TextView) convertView.findViewById(R.id.textproduk);
            nama.setText(item.getNamaMenu());

            ImageView gambar = (ImageView) convertView.findViewById(R.id.gambar);
            if (!item.getGambar().equals("null")) {
                Glide.with(SearchActivity.this).load(Constant.URLADMIN + item.getGambar())
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(gambar);
            } else {
                Glide.with(SearchActivity.this).load(R.drawable.shop)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(gambar);
            }

            TextView harga = (TextView) convertView.findViewById(R.id.textharga);
            String format = formatData.format(item.getHarga());
            harga.setText("Rp " + format);
            TextView satuan = (TextView) convertView.findViewById(R.id.satuan);
            satuan.setText(" / " + item.getSatuan());

            Button plus = (Button) convertView.findViewById(R.id.btnplus);
            Button min = (Button) convertView.findViewById(R.id.btnmin);

            // bagian button plus di tekan
            plus.setOnClickListener(new View.OnClickListener() {
                ItemMenu qty = list.get(position);

                @Override
                public void onClick(View v) {
                    if (isLogin()) {
                        qty.setQuantity(qty.getQuantity() + 1);
                        int SubTotal = (qty.getQuantity() * qty.getHarga());
                        qty.setTotal(SubTotal);
                        poin += qty.getPoin();
                        Cart cart = findCart(qty.getIdMenu());
                        if (cart != null) {
                            cart.setQuantity(qty.getQuantity());
                            cart.setTotal(SubTotal);
                        } else {
                            cart = new Cart();
                            cart.setIdMenu(qty.getIdMenu());
                            cart.setNamaMenu(qty.getNamaMenu());
                            cart.setQuantity(qty.getQuantity());
                            cart.setHarga(qty.getHarga());
                            cart.setPoin(qty.getPoin());
                            cart.setTotal(SubTotal);
                            cart.setGambar(qty.getGambar());
                            cart.setSatuan(qty.getSatuan());
                            cartList.add(cart);
                        }
                        notifyDataSetChanged();
                        TotalAmount(qty.getHarga());
                    }else{
                        Intent login = new Intent(context, LoginActivity.class);
                        startActivity(login);
                        finish();
                    }
                }
            });

            // bagian button min di tekan
            min.setOnClickListener(new View.OnClickListener() {
                ItemMenu qty = list.get(position);

                @Override
                public void onClick(View v) {
                    Cart cart = findCart(qty.getIdMenu());
                    if (qty.getQuantity() == 0) {
                        Toast.makeText(context, "Kamu belum order produk ini.", Toast.LENGTH_SHORT).show();
                        cartList.remove(cart);
                    } else if (qty.getQuantity() == 1) {
                        qty.setQuantity(0);
                        qty.setTotal(0);
                        poin -= qty.getPoin();
                        cartList.remove(cart);
                        notifyDataSetChanged();
                        TotalAmount(-qty.getHarga());
                    } else {
                        qty.setQuantity(qty.getQuantity() - 1);
                        int SubTotal = (qty.getQuantity() * qty.getHarga());
                        qty.setTotal(SubTotal);
                        poin -= qty.getPoin();
                        if (cart != null) {
                            cart.setQuantity(qty.getQuantity());
                            cart.setTotal(SubTotal);
                        } else {
                            cart = new Cart();
                            cart.setIdMenu(qty.getIdMenu());
                            cart.setNamaMenu(qty.getNamaMenu());
                            cart.setQuantity(qty.getQuantity());
                            cart.setHarga(qty.getHarga());
                            cart.setPoin(qty.getPoin());
                            cart.setTotal(SubTotal);
                            cart.setGambar(qty.getGambar());
                            cart.setSatuan(qty.getSatuan());
                            cartList.add(cart);
                        }
                        notifyDataSetChanged();
                        TotalAmount(-qty.getHarga());
                    }
                }
            });

            return convertView;
        }
    }

    public void TotalAmount(int harga) {
        totalBarang += harga;
        if (harga < 0) {
            total_item--;
        } else {
            total_item++;
        }
        if (totalBarang == 0) {
            estimasi.setVisibility(View.GONE);
        } else {
            estimasi.setVisibility(View.VISIBLE);
            txtTotal.setText("Rp " + formatduit.format(totalBarang));
            total_notif.setText(total_item + "");
        }
        countCart.setText(total_item + "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.search);
        MenuItemCompat.setActionView(item, R.layout.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(this);
        MenuItem shop = menu.findItem(R.id.shop);
        MenuItemCompat.setActionView(shop, R.layout.badge);
        RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(shop);
        RelativeLayout layout = (RelativeLayout) notifCount.findViewById(R.id.badge_layout);
        countCart = (TextView) notifCount.findViewById(R.id.badge);
        countCart.setText(total_item + "");
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchActivity.this, CartActivity.class);
                i.putExtra("poin", poin);
                i.putParcelableArrayListExtra("cartList", cartList);
                startActivity(i);
            }
        });
        return super.onCreateOptionsMenu(menu);
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
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String text) {
        URL_MENU = Constant.URLAPI + "key=" + Constant.KEY + "&tag=search&id=" + id + "&search=" + text;
        getData();
        return false;
    }

    @Override
    public void onResume() {
        if (!onActivityResult) {
            loadCart();
        } else {
            onActivityResult = false;
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (arraylist != null) {
            arraylist.clear();
            listview.setAdapter(null);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        cekData();
    }

    private void cekData() {
        if (cartList.size() > 0) {
            Constant.cartList = cartList;
            Constant.poin = poin;
            Constant.jumlah = total_item;
        }
        finish();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }
}
