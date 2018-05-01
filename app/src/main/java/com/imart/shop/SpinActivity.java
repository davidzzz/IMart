package com.imart.shop;

import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
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
import java.util.HashMap;
import java.util.Random;

public class SpinActivity extends BaseActivity {
    private ImageView ivWheel;
    private Button rotate;
    private float initDegree = 0.0f;
    TextView teksPoin;
    String URL, URL_SEND, URL_CONF, URL_POIN, userid;
    int idArray[] = {R.id.prize1, R.id.prize2, R.id.prize3, R.id.prize4, R.id.prize5, R.id.prize6, R.id.prize7, R.id.prize8};
    int tingkatArray[] = {0, 0, 0, 0, 0, 0, 0, 0};
    int poinArray[] = {0, 0, 0, 0, 0, 0, 0, 0};
    int pos = 0, poin, cost;
    SessionManager session;
    HashMap<String, String> user;
    Voucher[] voucherArray = {null, null, null, null, null, null, null, null};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin);
        getSupportActionBar().setTitle("JACKPOT");
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
        poin = Integer.parseInt(user.get(SessionManager.KEY_POIN));
        userid = user.get(session.KEY_PASSENGER_ID);
        teksPoin = (TextView) findViewById(R.id.poin);
        teksPoin.setText("" + poin);
        URL = Constant.URLAPI + "key=" + Constant.KEY + "&tag=jackpot";
        URL_CONF = Constant.URLAPI + "key=" + Constant.KEY + "&tag=konfigurasi&id=1";
        URL_POIN = Constant.URLAPI + "key=" + Constant.KEY + "&tag=updatePoin&id=" + userid;
        URL_SEND = Constant.URLADMIN + "api/voucher.php?key=" + Constant.KEY + "&tag=buy";
        konfigurasi();
        ambilData();

        ivWheel = (ImageView) findViewById(R.id.iv_wheel);
        rotate = (Button) findViewById(R.id.btn_rotate);
        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cost <= poin) {
                    poin -= cost;
                    session.updateValue(SessionManager.KEY_POIN, String.valueOf(poin));
                    teksPoin.setText("" + poin);
                    rotate();
                } else {
                    Toast.makeText(SpinActivity.this, "Poin Anda tidak mencukupi untuk memainkan jackpot ini", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void konfigurasi() {
        JsonObjectRequest jsonKate = new JsonObjectRequest(Request.Method.GET, URL_CONF, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject feedObj = response.getJSONObject("data");
                    cost = feedObj.getInt("value");
                    TextView teksCost = (TextView) findViewById(R.id.cost);
                    teksCost.setText("" + cost + "");
                } catch (JSONException e) {
                    cost = 0;
                    TextView cost = (TextView) findViewById(R.id.cost);
                    cost.setText("- ");
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

    private void ambilData() {
        JsonObjectRequest jsonKate = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray feedArray = response.getJSONArray("data");
                    String teks;
                    for (int i = 0; i < feedArray.length(); i++) {
                        JSONObject feedObj = (JSONObject) feedArray.get(i);
                        if (feedObj.getString("id_voucher").equals("null")) {
                            teks = feedObj.getInt("poin") + " POIN";
                        } else {
                            Voucher item = new Voucher();
                            item.setId(Integer.parseInt(feedObj.getString("id_voucher")));
                            item.setNama(feedObj.getString("nama"));
                            item.setPoin(Integer.parseInt(feedObj.getString("poinVoucher")));
                            voucherArray[i] = item;
                            teks = feedObj.getString("nama");
                        }
                        TextView keterangan = (TextView) findViewById(idArray[i]);
                        keterangan.setText(teks);
                        tingkatArray[i] = feedObj.getInt("tingkat");
                        poinArray[i] = feedObj.getInt("poin");
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

    public void rotate() {
        Random r = new Random();
        float angka = 0.0f;
        for (int i = 0; i < tingkatArray[pos]; i++){
            angka = r.nextFloat();
        }
        final float angle = angka * 360f;
        final float mAngleToRotate = 360f * 12 + angle; // rotate 12 rounds
        final RotateAnimation wheelRotation = new RotateAnimation(initDegree, mAngleToRotate, ivWheel.getWidth()/2.0f, ivWheel.getHeight()/2.0f);
        wheelRotation.setDuration(3000); // rotate 12 rounds in 3 seconds
        wheelRotation.setInterpolator(this, android.R.interpolator.accelerate_decelerate);
        wheelRotation.setFillAfter(true);
        wheelRotation.setFillEnabled(true);
        ivWheel.startAnimation(wheelRotation);

        wheelRotation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                if (angle >= 0.0f && angle < 45f) {
                    pos = 0;
                } else if (angle >= 45f && angle < 90f) {
                    pos = 1;
                } else if (angle >= 90f && angle < 135f) {
                    pos = 2;
                } else if (angle >= 135f && angle < 180f) {
                    pos = 3;
                } else if (angle >= 180f && angle < 225f) {
                    pos = 4;
                } else if (angle >= 225f && angle < 270f) {
                    pos = 5;
                } else if (angle >= 270f && angle < 315f) {
                    pos = 6;
                } else {
                    pos = 7;
                }
                if (voucherArray[pos] != null) {
                    Voucher item = voucherArray[pos];
                    new BuyVoucher(item.getId()).execute();
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(SpinActivity.this);
                    alert.setTitle("Reward");
                    if (poinArray[pos] > 0) {
                        poin += poinArray[pos];
                        session.updateValue(SessionManager.KEY_POIN, String.valueOf(poin));
                        teksPoin.setText("" + poin);
                        alert.setMessage("Selamat, Anda mendapatkan poin sebesar " + poinArray[pos]);
                    } else {
                        alert.setMessage("Maaf, Anda kurang beruntung");
                    }
                    alert.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                }
                updatePoin();
                initDegree = angle;
            }

            public void onAnimationRepeat(Animation animation) {

            }

            public void onAnimationStart(Animation animation) {

            }
        });
    }

    private void updatePoin() {
        StringRequest jsonKate = new StringRequest(URL_POIN + "&poin=" + poin, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        jsonKate.setRetryPolicy(new DefaultRetryPolicy(5000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        myapp.getInstance().addToRequestQueue(jsonKate);
    }

    class BuyVoucher extends AsyncTask<Void,Void,Boolean> {
        String response;
        int id;

        public BuyVoucher(int id){
            this.id = id;
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

                String parameter = "idUser=" + userid + "&idVoucher=" + id + "&key=" + Constant.KEY;

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
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public void onPostExecute(Boolean result) {
            if (result != null && result) {
                AlertDialog.Builder alert = new AlertDialog.Builder(SpinActivity.this);
                alert.setTitle("Reward");
                alert.setMessage("Selamat, Anda memenangkan voucher " + voucherArray[pos].getNama());
                alert.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
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
