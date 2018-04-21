package com.imart.shop;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.imart.shop.app.myapp;
import com.imart.shop.model.Feedback;
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

public class FeedbackActivity extends AppCompatActivity {
    private Toolbar toolbar;
    RatingBar rating1, rating2, rating3;
    EditText komentar;
    Button send;
    String URL_SEND, userid, akses, feedback;
    SessionManager session;
    HashMap<String, String> user;
    ProgressDialog loading;
    int colorValue;
    float rate1, rate2, rate3;
    ListView listView;
    ArrayList<Feedback> list = new ArrayList<>();
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("FEEDBACK");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Constant.COLOR));
        colorValue = getIntent().getIntExtra("color", 0);
        LinearLayout layout = (LinearLayout)findViewById(R.id.activity_feedback);
        layout.setBackgroundColor(colorValue);
        URL_SEND = Constant.URLADMIN + "api/feedback.php";
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
        userid = user.get(session.KEY_PASSENGER_ID);
        akses = user.get(SessionManager.KEY_AKSES);
        LinearLayout layout_form = (LinearLayout) findViewById(R.id.form_feedback);
        listView = (ListView) findViewById(R.id.list_feedback);
        if (akses.equals("1")) {
            layout_form.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            rating1 = (RatingBar) findViewById(R.id.kelayakan_harga);
            rating2 = (RatingBar) findViewById(R.id.kebersihan);
            rating3 = (RatingBar) findViewById(R.id.pelayanan);
            komentar = (EditText) findViewById(R.id.feedback);
            send = (Button) findViewById(R.id.send);
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    feedback = komentar.getText().toString();
                    if (feedback.equals("")) {
                        Toast.makeText(FeedbackActivity.this, "Komentar tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    } else {
                        loading = ProgressDialog.show(FeedbackActivity.this, "Kirim Feedback", "Please wait...", false, true);
                        rate1 = rating1.getRating();
                        rate2 = rating2.getRating();
                        rate3 = rating3.getRating();
                        new SendFeedback().execute();
                    }
                }
            });
        } else {
            layout_form.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            adapter = new Adapter(list);
            ambilData();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                    Feedback feedback = list.get(position);
                    Intent intent = new Intent(FeedbackActivity.this, FeedbackDetailActivity.class);
                    intent.putExtra("feedback", feedback);
                    startActivity(intent);
                }
            });
        }
    }

    class SendFeedback extends AsyncTask<Void,Void,Boolean> {
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

                String parameter = "idUser=" + userid + "&rating1=" + rate1 + "&rating2=" + rate2 + "&rating3=" + rate3
                        + "&komentar=" + feedback + "&key=" + Constant.KEY;

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
                Dialog dialog = new Dialog(FeedbackActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.feedback_success);
                dialog.show();
            } else {
                Toast.makeText(FeedbackActivity.this, "Feedback gagal dikirim", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void ambilData() {
        String URL = Constant.URLADMIN + "api/feedback.php?key=" + Constant.KEY + "&tag=list";
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
                Feedback feedback = new Feedback();
                feedback.setNama(feedObj.getString("nama"));
                feedback.setRating1(Float.parseFloat(feedObj.getString("feedback_1")));
                feedback.setRating2(Float.parseFloat(feedObj.getString("feedback_2")));
                feedback.setRating3(Float.parseFloat(feedObj.getString("feedback_3")));
                feedback.setRating4(Float.parseFloat(feedObj.getString("feedback_4")));
                feedback.setKomentar(feedObj.getString("komentar"));
                feedback.setWaktu(feedObj.getString("waktu"));
                list.add(feedback);
            }
            listView.setAdapter(adapter);
        } catch (JSONException e) {
        }
        adapter.notifyDataSetChanged();
    }

    public class Adapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<Feedback> list;

        public Adapter(List<Feedback> list) {
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
                convertView = inflater.inflate(R.layout.list_feedback, null);
            Feedback feedback = list.get(position);
            TextView nama = (TextView) convertView.findViewById(R.id.nama);
            nama.setText(feedback.getNama());
            TextView rating = (TextView) convertView.findViewById(R.id.rating);
            float avgRating = (feedback.getRating1() + feedback.getRating2() + feedback.getRating3() + feedback.getRating4()) / 4;
            rating.setText("Rating : " + avgRating);
            TextView waktu = (TextView) convertView.findViewById(R.id.waktu);
            waktu.setText(feedback.getWaktu());
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
