package com.imart.shop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

public class SearchFragment extends Fragment {
    private GridView listview;
    private AdapterSinz adapter = null;
    private List<ItemMenu> arraylist = new ArrayList<ItemMenu>();
    ArrayList<Cart> cartList = new ArrayList<>();
    private LinearLayout estimasi;
    String URL, URL_MENU;
    private static final int PRODUK_DETAIL = 100;
    int poin = 0, total_item = 0, totalBarang = 0, currentPosition;
    TextView txtTotal, total_notif, countCart;
    DecimalFormat formatduit = new DecimalFormat();
    ProgressBar loading;
    boolean onActivityResult = false;
    View view;

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        txtTotal = (TextView) view.findViewById(R.id.txtTotal);
        total_notif = (TextView) view.findViewById(R.id.total_notif);
        estimasi = (LinearLayout) view.findViewById(R.id.lytOrder);
        loading = (ProgressBar) view.findViewById(R.id.prgLoading);
        estimasi.setVisibility(View.GONE);
        estimasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CartActivity.class);
                i.putExtra("poin", poin);
                i.putParcelableArrayListExtra("cartList", cartList);
                startActivity(i);
            }
        });
        listview = (GridView) view.findViewById(R.id.list_view);
        adapter = new AdapterSinz(arraylist);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                currentPosition = position;
                ItemMenu item = (ItemMenu) parent.getItemAtPosition(position);
                Intent i = new Intent(getActivity(), ProdukDetailActivity.class);
                i.putExtra("id", item.getIdMenu());
                i.putExtra("nama", item.getNamaMenu());
                i.putExtra("porsi", item.getQuantity());
                startActivityForResult(i, PRODUK_DETAIL);
            }
        });
        return view;
    }

    public boolean isLogin() {
        SessionManager sessionManager = new SessionManager(getActivity().getApplicationContext());
        return sessionManager.isLoggedIn();
    }

    public void getData(String text) {
        arraylist.clear();
        URL_MENU = Constant.URLAPI + "key=" + Constant.KEY + "&tag=search&search=" + text;
        JsonObjectRequest jsonKate = new JsonObjectRequest(Request.Method.GET, URL_MENU, null, new Response.Listener<JSONObject>() {
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
            /*if (countCart != null) {
                countCart.setText(total_item + "");
            }*/
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                        //((MainActivity)getActivity()).getCountCart().setText(total_item + "");
                    }
                }
                onActivityResult = true;
                break;
        }
    }

    public class AdapterSinz extends BaseAdapter {
        private List<ItemMenu> list;
        private LayoutInflater inflater;
        DecimalFormat formatData = new DecimalFormat();

        public AdapterSinz(List<ItemMenu> list) {
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
                inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null)
                convertView = inflater.inflate(R.layout.daftar_produk, null);
            final TextView qty = (TextView) convertView.findViewById(R.id.edtQty);
            qty.setText("" + item.getQuantity());
            TextView nama = (TextView) convertView.findViewById(R.id.textproduk);
            nama.setText(item.getNamaMenu());

            ImageView gambar = (ImageView) convertView.findViewById(R.id.gambar);
            if (!item.getGambar().equals("null")) {
                Glide.with(getActivity()).load(Constant.URLADMIN + item.getGambar())
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(gambar);
            } else {
                Glide.with(getActivity()).load(R.drawable.shop)
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
                        Intent login = new Intent(getActivity(), LoginActivity.class);
                        startActivity(login);
                        getActivity().finish();
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
                        Toast.makeText(getActivity(), "Kamu belum order produk ini.", Toast.LENGTH_SHORT).show();
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
        //countCart.setText(total_item + "");
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
    public void onDestroy() {
        if (arraylist != null) {
            arraylist.clear();
            listview.setAdapter(null);
        }
        super.onDestroy();
    }
}
