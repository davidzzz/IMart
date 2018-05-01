package com.imart.shop;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.imart.shop.app.myapp;
import com.imart.shop.model.Cart;
import com.imart.shop.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class FlashDealActivity extends BaseActivity {
    String id, URL, nama, idProduk, gambar, satuan;
    private SliderLayout mDemoSlider;
    private TextView teksNama, teksTanggal, teksHarga, teksHargaAsli, teksPoin, teksQty, teksSisa, deskripsi, total;
    private Button order;
    ArrayList<Cart> cartList;
    int harga, qty, poin, totalPoin = 0, item, sisa;
    Cart cart;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_deal);
        getSupportActionBar().setTitle(getIntent().getStringExtra("nama"));
        teksNama = (TextView) findViewById(R.id.nama);
        teksTanggal = (TextView) findViewById(R.id.tanggal);
        teksHarga = (TextView) findViewById(R.id.harga);
        teksHargaAsli = (TextView) findViewById(R.id.harga_asli);
        teksPoin = (TextView) findViewById(R.id.poin);
        teksQty = (TextView) findViewById(R.id.qty);
        teksSisa = (TextView) findViewById(R.id.sisa);
        deskripsi = (TextView) findViewById(R.id.deskripsi);
        total = (TextView) findViewById(R.id.total);
        order = (Button) findViewById(R.id.order);
        mDemoSlider = (SliderLayout) findViewById(R.id.slider);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        id = getIntent().getStringExtra("id");
        URL = Constant.URLADMIN + "api/flash_deal.php?key=" + Constant.KEY + "&tag=listdetail&id=" + id;
        getData();
        cartList = new ArrayList<>();
        Button plus = (Button) findViewById(R.id.btnplus);
        Button min = (Button) findViewById(R.id.btnmin);

        // bagian button plus di tekan
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qty < sisa) {
                    qty++;
                    int SubTotal = qty * harga;
                    totalPoin += poin;
                    if (cart != null) {
                        cart.setQuantity(qty);
                        cart.setTotal(SubTotal);
                    } else {
                        cart = new Cart();
                        cart.setIdMenu(idProduk);
                        cart.setNamaMenu(nama);
                        cart.setGambar(gambar);
                        cart.setQuantity(qty);
                        cart.setHarga(harga);
                        cart.setPoin(poin);
                        cart.setSatuan(satuan);
                        cart.setTotal(SubTotal);
                        cartList.add(cart);
                    }
                    teksQty.setText(qty + "");
                    total.setText("Total: Rp. " + SubTotal);
                } else {
                    Toast.makeText(FlashDealActivity.this, "Jumlah porsi sudah mencapai batas.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // bagian button min di tekan
        min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qty == 1) {
                    qty = 0;
                    totalPoin -= poin;
                    cartList.remove(cart);
                    teksQty.setText("0");
                    total.setText("Total: Rp. 0");
                } else if (qty > 1) {
                    qty--;
                    int SubTotal = qty * harga;
                    totalPoin -= poin;
                    if (cart != null) {
                        cart.setQuantity(qty);
                        cart.setTotal(SubTotal);
                    } else {
                        cart = new Cart();
                        cart.setIdMenu(idProduk);
                        cart.setNamaMenu(nama);
                        cart.setGambar(gambar);
                        cart.setQuantity(qty);
                        cart.setHarga(harga);
                        cart.setPoin(poin);
                        cart.setSatuan(satuan);
                        cart.setTotal(SubTotal);
                        cartList.add(cart);
                    }
                    teksQty.setText(qty + "");
                    total.setText("Total: Rp. " + SubTotal);
                }
            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FlashDealActivity.this, CartActivity.class);
                i.putExtra("poin", totalPoin);
                i.putExtra("idFlashDeal", id);
                i.putParcelableArrayListExtra("cartList", cartList);
                startActivity(i);
            }
        });
    }

    private void getData() {
        JsonObjectRequest jsonKate = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                parseJsonKategory(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        jsonKate.setRetryPolicy(new DefaultRetryPolicy(5000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myapp.getInstance().addToRequestQueue(jsonKate);

        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(5000);
    }

    private void parseJsonKategory(JSONObject response) {
        try {
            JSONArray data = response.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject feedObj = (JSONObject) data.get(i);
                String judul = feedObj.getString("judul");
                String tanggal = feedObj.getString("tanggal_akhir");
                SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, Integer.parseInt(tanggal.substring(0, 4)));
                c.set(Calendar.MONTH, Integer.parseInt(tanggal.substring(5, 7)) - 1);
                c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tanggal.substring(8, 10)));
                gambar = feedObj.getString("gambar");
                satuan = feedObj.getString("satuan");
                nama = feedObj.getString("nama_produk");
                idProduk = feedObj.getString("produk_id");
                harga = Integer.parseInt(feedObj.getString("harga"));
                poin = Integer.parseInt(feedObj.getString("poin"));
                item = Integer.parseInt(feedObj.getString("item"));
                sisa = Integer.parseInt(feedObj.getString("sisa"));
                progressBar.setMax(item);
                progressBar.setProgress(sisa);
                teksSisa.setText(sisa + " / " + item);
                teksNama.setText(nama);
                teksTanggal.setText("s/d " + formatter.format(c.getTime()));
                teksHarga.setText("Rp. " + harga + " / " + satuan);
                teksHargaAsli.setText("Rp. " + feedObj.getString("harga_asli") + " / " + satuan, TextView.BufferType.SPANNABLE);
                teksPoin.setText("Poin : " + poin);
                Spannable spannable = (Spannable)teksHargaAsli.getText();
                spannable.setSpan(new StrikethroughSpan(), 0, teksHargaAsli.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    deskripsi.setText(Html.fromHtml(feedObj.getString("deskripsi"), Html.FROM_HTML_MODE_COMPACT), TextView.BufferType.SPANNABLE);
                } else {
                    deskripsi.setText(Html.fromHtml(feedObj.getString("deskripsi")), TextView.BufferType.SPANNABLE);
                }
                if (sisa <= 0) {
                    order.setEnabled(false);
                }
                TextSliderView textSliderView = new TextSliderView(FlashDealActivity.this);
                textSliderView
                        .image(Constant.URLADMIN + gambar)
                        .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                        .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                            @Override
                            public void onSliderClick(BaseSliderView slider) {
                                Intent i = new Intent(FlashDealActivity.this, GambarActivity.class);
                                i.putExtra("url", slider.getUrl());
                                startActivity(i);
                            }
                        });

                mDemoSlider.addSlider(textSliderView);
            }
        } catch (JSONException e) {
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
            cekData();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        cekData();
    }

    private void cekData() {
        if (cartList.size() > 0) {
            ShowDialog();
        } else {
            this.finish();
            overridePendingTransition(R.anim.open_main, R.anim.close_next);
        }
    }

    private void ShowDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.delete_confirm);
        builder.setMessage(getString(R.string.delete_msg));
        builder.setPositiveButton(getString(R.string.ya), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                cartList.clear();
                finish();
            }
        });

        builder.setNegativeButton(getString(R.string.tidak), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
