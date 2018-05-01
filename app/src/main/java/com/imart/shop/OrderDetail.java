package com.imart.shop;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.imart.shop.app.myapp;
import com.imart.shop.model.ItemOrder;
import com.imart.shop.util.Constant;
import com.imart.shop.util.SessionManager;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

public class OrderDetail extends BaseActivity {
    TextView txtTotal, txtName, txtStatus,textNotes,txtbiodata,txtOngkir,txtSubtotal, orderList;
    Button btnCancel, btnAcpt, btnSend, btnEmail;
    String ID, URL_CANCEL, userid, URL_ACCEPT;
    SessionManager session;
    HashMap<String, String> user;
    LinearLayout lytbiodata,lytbtn;
    ItemOrder item_order;
    DecimalFormat formatduit = new DecimalFormat();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_detail);
        getSupportActionBar().setTitle("Order Detail");
        lytbtn = (LinearLayout)findViewById(R.id.lytbtn);
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
        userid = user.get(session.KEY_PASSENGER_ID);
        lytbiodata = (LinearLayout)findViewById(R.id.lytbiodata);
        txtTotal = (TextView) findViewById(R.id.txtTotal);
        txtName = (TextView) findViewById(R.id.txtName);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        txtbiodata = (TextView)findViewById(R.id.txtbiodata);
        textNotes = (TextView) findViewById(R.id.textNotes);
        txtOngkir = (TextView)findViewById(R.id.txtOngkir);
        txtSubtotal =(TextView)findViewById(R.id.txtSubtotal);
        orderList = (TextView) findViewById(R.id.orderList);
        URL_CANCEL = Constant.URLADMIN + "api/cancel_seller.php";
        btnAcpt = (Button) findViewById(R.id.btnAcpt);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnEmail = (Button) findViewById(R.id.btnEmail);
        item_order = getIntent().getParcelableExtra("item_order");
        ID = item_order.getId();
        String akses = user.get(SessionManager.KEY_AKSES);
        if (akses.equals("1") || !item_order.getStatus().equals("PENDING")) {
            lytbtn.setVisibility(View.GONE);
        }
        if (akses.equals("1")) {
            if (item_order.getStatus().equals("PROCESSED")) {
                btnSend.setText("ACCEPT DELIVERY");
                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final AlertDialog.Builder alert = new AlertDialog.Builder(OrderDetail.this);
                        alert.setTitle(R.string.app_name);
                        alert.setIcon(R.drawable.ic_launcher);
                        alert.setMessage("Accept Delivery");
                        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Accept(ID);
                            }
                        });
                        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });

                        alert.show();
                    }
                });
            /*} else if (item_order.getStatus().equals("PENDING")) {
                btnSend.setText("CANCEL");
                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final AlertDialog.Builder alert = new AlertDialog.Builder(OrderDetail.this);
                        alert.setTitle(R.string.app_name);
                        alert.setIcon(R.drawable.ic_launcher);
                        alert.setMessage("Cancel This Order ?");
                        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Cancel(ID);
                            }
                        });
                        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });

                        alert.show();
                    }
                });*/
            } else {
                btnSend.setVisibility(View.GONE);
            }
            btnEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SendEmail();
                }
            });
        } else {
            btnEmail.setVisibility(View.GONE);
            if (item_order.isOnTheSpot()) {
                btnSend.setText("GO TO SENT POINT");
                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String formatter = String.format(Locale.US, "%04d", Integer.parseInt(item_order.getIdUser()));
                        String nama_user = item_order.getNama().toUpperCase();
                        Intent intent = new Intent(OrderDetail.this, SentPoinOrderActivity.class);
                        intent.putParcelableArrayListExtra("item_detail", item_order.getItemDetail());
                        intent.putExtra("nomor_user", String.valueOf(nama_user.charAt(0)) + String.valueOf(nama_user.charAt(2)) + formatter);
                        intent.putExtra("id_order", item_order.getId());
                        startActivity(intent);
                    }
                });
            } else {
                btnSend.setVisibility(View.GONE);
            }
        }
        if (item_order.isOnTheSpot()) {
            lytbiodata.setVisibility(View.GONE);
            LinearLayout layout_ongkir = (LinearLayout) findViewById(R.id.layout_ongkir);
            layout_ongkir.setVisibility(View.GONE);
        }
        txtbiodata.setText("DETAIL BUYER \n\nAddress: " + item_order.getAddress() + "\nNo. Handphone : "+ item_order.getTelp());
        txtStatus.setText(item_order.getStatus().equals("") ? "PENDING" : item_order.getStatus());
        txtName.setText("Nama : " + item_order.getNama());
        textNotes.setText(item_order.getCatatan());
        txtTotal.setText(String.valueOf(formatduit.format(item_order.getTotal())));
        txtOngkir.setText(String.valueOf(formatduit.format(item_order.getTtlongkir())));
        txtSubtotal.setText(String.valueOf(formatduit.format(item_order.getTtlongkir() + item_order.getTotal())));
        String itemList = item_order.getItemDetail().toString();
        orderList.setText(itemList.substring(1, itemList.length() - 1).replace(", ", "\n"));
        orderList.setBackgroundColor(Color.parseColor("#ffffff"));
        btnAcpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AcceptOrder();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(OrderDetail.this);
                alert.setTitle(R.string.app_name);
                alert.setIcon(R.drawable.ic_launcher);
                alert.setMessage("Cancel This Order ?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final ProgressDialog loading = ProgressDialog.show(OrderDetail.this,"Send Data...","Please wait...",false,true);
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CANCEL,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String s) {
                                        //Disimissing the progress dialog
                                        loading.dismiss();
                                        Toast.makeText(OrderDetail.this, s, Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {
                                        //Dismissing the progress dialog
                                        loading.dismiss();
                                        Toast.makeText(OrderDetail.this, "Server error", Toast.LENGTH_LONG).show();
                                    }
                                }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                //Converting Bitmap to String
                                //Creating parameters
                                Map<String,String> params = new Hashtable<String, String>();
                                //Adding parameters
                                params.put("order_id", ID);
                                params.put("gcm", item_order.getGcm_id());
                                params.put("key", Constant.KEY);
                                params.put("user_id", userid);
                                //returning parameters
                                return params;
                            }
                        };
                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        myapp.getInstance().addToRequestQueue(stringRequest);
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
                alert.show();
            }
        });
    }

    private void SendEmail(){
        final ProgressDialog loading = ProgressDialog.show(OrderDetail.this,"Send email...","Please wait...",false,true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URLADMIN + "api/send_email.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        loading.dismiss();
                        Toast.makeText(OrderDetail.this, s, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                        Toast.makeText(OrderDetail.this, "Gagal mengirimkan data order ke email Anda", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();
                //Adding parameters
                params.put("order_id", ID);
                params.put("key", Constant.KEY);
                params.put("email", user.get("email"));
                //returning parameters
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myapp.getInstance().addToRequestQueue(stringRequest);
    }

    private void AcceptOrder() {
        URL_ACCEPT = Constant.URLADMIN + "api/accept.php";
        final ProgressDialog loading = ProgressDialog.show(OrderDetail.this,"Send data...","Please wait...",false,true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ACCEPT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        Toast.makeText(OrderDetail.this, s, Toast.LENGTH_LONG).show();
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                        Toast.makeText(OrderDetail.this, "Server error", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();
                //Adding parameters
                params.put("order_id", ID);
                params.put("key", Constant.KEY);
                params.put("gcm", item_order.getGcm_id());
                //returning parameters
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myapp.getInstance().addToRequestQueue(stringRequest);
    }

    public void Accept(final String order_id) {
        String URL = Constant.URLADMIN + "api/accept_user.php";
        final ProgressDialog loading = ProgressDialog.show(this,"Send Data...","Please wait...",false,true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        Toast.makeText(OrderDetail.this, s, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                        Toast.makeText(OrderDetail.this, "Server error, Please Try again..", Toast.LENGTH_LONG).show();
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
        String URL = Constant.URLADMIN + "api/cancel.php";
        final ProgressDialog loading = ProgressDialog.show(this,"Send Data...","Please wait...",false,true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        Toast.makeText(OrderDetail.this, s, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                        Toast.makeText(OrderDetail.this, "Server error, Please Try again..", Toast.LENGTH_LONG).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }
}
