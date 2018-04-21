package com.imart.shop;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import com.imart.shop.app.myapp;
import com.imart.shop.model.ItemSub;
import com.imart.shop.util.Constant;
import com.imart.shop.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class AddProduk extends AppCompatActivity {
    private Toolbar toolbar;
    Button btnAdd;
    Spinner spin;
    String nama, price, status;
    String sub_id, userid;
    EditText edtTitle, edtPrice;
    RadioGroup grup;
    RadioButton rdStatus, rdStatus1, rdStatus2;
    ItemSub item;
    //variable session get id
    SessionManager session;
    HashMap<String, String> user;
    String URL_ADD, URL_KAT, id_menu, URL_DELETE;
    ArrayList<ItemSub> listKategori = new ArrayList<>();
    ArrayAdapter<ItemSub> adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_produk);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        id_menu = getIntent().getStringExtra("id");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(id_menu != null ? "Edit" : "Tambah" + " Produk");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Constant.COLOR));
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
        userid = user.get(SessionManager.KEY_PASSENGER_ID);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        edtTitle = (EditText) findViewById(R.id.edtTitle);// ini nama produk
        edtPrice = (EditText) findViewById(R.id.edtPrice);// ini harga barang
        spin = (Spinner) findViewById(R.id.spinnerKat);
        grup = (RadioGroup) findViewById(R.id.rdGroup);//radio status
        rdStatus1 = (RadioButton) findViewById(R.id.rdReady);
        rdStatus2 = (RadioButton) findViewById(R.id.rdClose);
        URL_KAT = Constant.URLAPI + "key=" + Constant.KEY + "&tag=" + Constant.TAG_SUB;
        URL_DELETE = Constant.URLADMIN +"api/delete_produk.php";
        if (id_menu != null) {
            URL_ADD = Constant.URLADMIN + "api/editproduk.php";
            edtTitle.setText(getIntent().getStringExtra("nama"));
            edtPrice.setText(getIntent().getIntExtra("harga", 0) + "");
        } else {
            URL_ADD = Constant.URLADMIN + "api/produk.php";
        }
        setSpin();
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nama = edtTitle.getText().toString();
                price = edtPrice.getText().toString();
                if (edtTitle.length() == 0) {
                    edtTitle.setError("Judul tidak boleh kosong");
                    edtTitle.requestFocus();
                } else if (edtPrice.length() == 0) {
                    edtPrice.setError("Harga tidak boleh kosong");
                    edtPrice.requestFocus();
                } else {
                    int selec = grup.getCheckedRadioButtonId();
                    rdStatus = (RadioButton) findViewById(selec);
                    status = rdStatus.getText().toString();

                    KirimData();
                }
            }
        });
    }

    private void KirimData() {
        final ProgressDialog loading = ProgressDialog.show(this, "Loading...", "Please wait...", false, true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ADD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        Toast.makeText(AddProduk.this, s, Toast.LENGTH_LONG).show();
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();
                //Adding parameters
                params.put("name", nama);
                params.put("price", price);
                params.put("sub_id", sub_id);
                params.put("status", status);
                params.put("key", Constant.KEY);
                params.put("produk_id", id_menu);
                params.put("idUser", userid);
                //returning parameters
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myapp.getInstance().addToRequestQueue(stringRequest);
    }

    public void daftarKategori() {
        String URLKATE = Constant.URLAPI + "key=" + Constant.KEY + "&tag=" + Constant.TAG_SUB;;
        JsonObjectRequest jsonKate = new JsonObjectRequest(Request.Method.GET, URLKATE, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray feedArray = response.getJSONArray("data");
                    for (int i = 0; i < feedArray.length(); i++) {
                        JSONObject feedObj = (JSONObject) feedArray.get(i);
                        ItemSub itemSub = new ItemSub();
                        itemSub.setId(feedObj.getString("sub_id"));
                        itemSub.setName(feedObj.getString("nama_sub"));
                        listKategori.add(itemSub);
                    }
                } catch (JSONException e) {
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        jsonKate.setRetryPolicy(new DefaultRetryPolicy(5000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myapp.getInstance().addToRequestQueue(jsonKate);
    }

    private void setSpin() {
        daftarKategori();
        adapter = new ArrayAdapter<>(AddProduk.this, android.R.layout.simple_spinner_dropdown_item, listKategori);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item = (ItemSub) parent.getItemAtPosition(position);
                sub_id = item.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.deleteItem) {
            deleteItem();
        }

        if (id == android.R.id.home) {
            // app icon in action bar clicked; go home
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteItem() {
        final ProgressDialog loading = ProgressDialog.show(this, "Loading...", "Please wait...", false, true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DELETE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        Toast.makeText(AddProduk.this, s, Toast.LENGTH_LONG).show();
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();
                //Adding parameters
                params.put("id", id_menu);
                params.put("key", Constant.KEY);
                params.put("idUser", userid);
                //returning parameters
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myapp.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        spin.setAdapter(null);
        super.onDestroy();
    }
}
