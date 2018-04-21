package com.imart.shop;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.imart.shop.app.myapp;
import com.imart.shop.model.Help;
import com.imart.shop.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends AppCompatActivity {
    Adapter adapter;
    ArrayList<Help> list = new ArrayList<>();
    ListView listHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("HELP");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Constant.COLOR));
        listHelp = (ListView) findViewById(R.id.listHelp);
        adapter = new Adapter(list);
        listHelp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                Help help = (Help) parent.getItemAtPosition(position);
                Intent i = new Intent(HelpActivity.this, HelpDetailActivity.class);
                i.putExtra("id", help.getId());
                startActivity(i);
            }
        });
        ambilData();
    }

    private void ambilData() {
        String URL = Constant.URLAPI + "key=" + Constant.KEY + "&tag=help";
        JsonObjectRequest jsonKate = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                list.clear();
                parseJsonKategory(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
                Help help = new Help();
                help.setId(feedObj.getInt("id"));
                help.setJudul(feedObj.getString("judul"));
                help.setIsi(feedObj.getString("isi"));
                list.add(help);
            }
            listHelp.setAdapter(adapter);
        } catch (JSONException e) {
        }
        adapter.notifyDataSetChanged();
    }

    public class Adapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<Help> list;

        public Adapter(List<Help> list) {
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
                convertView = inflater.inflate(R.layout.list_help, null);
            Help help = list.get(position);
            TextView judul = (TextView) convertView.findViewById(R.id.judul);
            judul.setText(help.getJudul());
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
