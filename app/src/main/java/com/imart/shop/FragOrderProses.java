package com.imart.shop;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import com.imart.shop.adapter.OrderAdapter;
import com.imart.shop.app.myapp;
import com.imart.shop.model.ItemDetail;
import com.imart.shop.model.ItemOrder;
import com.imart.shop.util.Constant;
import com.imart.shop.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class FragOrderProses extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView list;
    private List<ItemOrder> itemList, daftarItem;
    private ItemOrder object;
    private OrderAdapter adapter;
    SessionManager session;
    HashMap<String, String> user;
    String URL_ORDER,userid,URL_CANCEL,URL_ACCEPT;
    Button tanggal;
    EditText search;
    boolean isFlashDeal;
    int preLast = 0;
    Timer timer = new Timer();
    private final long DELAY = 500;

    public static FragOrderProses newInstance(boolean isFlashDeal){
        Bundle args = new Bundle();
        args.putBoolean("isFlashDeal", isFlashDeal);

        FragOrderProses fragment = new FragOrderProses();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        list =(ListView)rootView.findViewById(R.id.ListView1);
        session = new SessionManager(getActivity().getApplicationContext());
        itemList = new ArrayList<ItemOrder>();
        daftarItem = new ArrayList<ItemOrder>();
        user = session.getUserDetails();
        userid = user.get(session.KEY_PASSENGER_ID);
        isFlashDeal = getArguments().getBoolean("isFlashDeal");
        adapter = new OrderAdapter(getActivity(), daftarItem, this);
        URL_CANCEL = Constant.URLADMIN+"api/cancel.php";
        URL_ACCEPT = Constant.URLADMIN+"api/accept_user.php";
        URL_ORDER = Constant.URLADMIN+"api/history.php?key=" + Constant.KEY + "&tag=order&user_id="+userid + "&isFlashDeal=" + isFlashDeal;
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                    ambilData();
            }
        });
        search = (EditText) rootView.findViewById(R.id.search_text);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                ambilData();
                            }
                        },
                        DELAY
                );
            }
        });
        tanggal = (Button) rootView.findViewById(R.id.tanggal);
        pilihTanggal();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                object = itemList.get(position);
                Intent detail = new Intent(getActivity(), OrderDetail.class);
                detail.putExtra("item_order", object);
                startActivity(detail);
            }
        });
        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem == totalItemCount && totalItemCount != 0)
                {
                    if (preLast != lastItem)
                    {
                        preLast = lastItem;
                        addItems();
                    }
                }
            }
        });

        return rootView;
    }

    public void addItems(){
        if (daftarItem.size() < itemList.size()) {
            int temp = daftarItem.size();
            for (int i = daftarItem.size(); i < itemList.size(); i++) {
                if (i > temp + 9) {
                    break;
                }
                daftarItem.add(itemList.get(i));
            }
            adapter.notifyDataSetChanged();
        }
    }

    public List<ItemOrder> getListItem() {
        return itemList;
    }

    public BaseAdapter getAdapter() {
        return adapter;
    }

    public void pilihTanggal() {
        final Calendar c = Calendar.getInstance();
        tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int y = c.get(Calendar.YEAR);
                int m = c.get(Calendar.MONTH);
                int d = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String day, month;
                                c.set(year, monthOfYear, dayOfMonth);
                                if (dayOfMonth < 10) {
                                    day = '0' + String.valueOf(dayOfMonth);
                                } else {
                                    day = String.valueOf(dayOfMonth);
                                }
                                monthOfYear++; // disini bulan dimulai dari 0
                                if (monthOfYear < 10) {
                                    month = '0' + String.valueOf(monthOfYear);
                                } else {
                                    month = String.valueOf(monthOfYear);
                                }
                                tanggal.setText(year + "-" + month + "-" + day);
                                ambilData();
                            }
                        }, y, m, d);
                datePickerDialog.show();
            }
        });
    }

    public void Accept(final String order_id) {
        final ProgressDialog loading = ProgressDialog.show(getActivity(),"Send Data...","Please wait...",false,true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ACCEPT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
                        ambilData();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                        Toast.makeText(getActivity(), "Server error, Please Try again..", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();
                //Adding parameters
                params.put("order_id", order_id);
                params.put("key", Constant.KEY);
                //returning parameters
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myapp.getInstance().addToRequestQueue(stringRequest);
    }

    public void Cancel(final String order_id) {
        final ProgressDialog loading = ProgressDialog.show(getActivity(),"Send Data...","Please wait...",false,true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CANCEL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
                        ambilData();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                        Toast.makeText(getActivity(), "Server error, Please Try again..", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();
                //Adding parameters
                params.put("order_id", order_id);
                params.put("key", Constant.KEY);
                params.put("user_id", userid);
                //returning parameters
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myapp.getInstance().addToRequestQueue(stringRequest);
    }

    private void ambilData() {
        String tgl = tanggal.getText().toString().equals("TANGGAL") ? "" : tanggal.getText().toString();
        URL_ORDER += "&search=" + search.getText().toString() + "&tanggal=" + tgl;
        JsonObjectRequest jsonKate = new JsonObjectRequest(Request.Method.GET, URL_ORDER, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Dismissing progress dialog\
                itemList.clear();
                daftarItem.clear();
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
        String id = "";
        ItemOrder item = new ItemOrder();
        try {
            JSONArray feedArray = response.getJSONArray("data");
            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);
                if (!feedObj.getString("order_id").equals(id)) {
                    itemList.add(item);
                    id = feedObj.getString("order_id");
                    item = new ItemOrder();
                    item.setId(feedObj.getString("order_id"));
                    item.setNama(feedObj.getString("nama"));
                    item.setTotal(feedObj.getInt("total_order"));
                    item.setStatus(feedObj.getString("status_order"));
                    item.setTanggal(feedObj.getString("time"));
                    item.setCatatan(feedObj.getString("notes"));
                    item.setAddress(feedObj.getString("address"));
                    item.setTelp(feedObj.getString("telp_order"));
                    item.setGcm_id(feedObj.getString("gcm"));
                    item.setTtlongkir(feedObj.getInt("ttl_ongkir"));
                    item.setOnTheSpot(feedObj.getString("on_the_spot").equals("1"));
                }
                ItemDetail itemDetail = new ItemDetail();
                itemDetail.setNama(feedObj.getString("nama_produk"));
                itemDetail.setQty(feedObj.getInt("qty"));
                itemDetail.setSubtotal(feedObj.getInt("subtotal"));
                item.setItemDetail(itemDetail);
            }
            itemList.add(item);
            itemList.remove(0);
            for (int i = 0; i < itemList.size(); i++) {
                if (i > 9) {
                    break;
                }
                daftarItem.add(itemList.get(i));
            }
            list.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        ambilData();
    }

    @Override
    public void onResume() {
        ambilData();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        list.setAdapter(null);
        timer.cancel();
        Glide.get(getActivity()).clearMemory();
        super.onDestroy();
    }
}
