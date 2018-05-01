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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.imart.shop.model.ItemDetail;
import com.imart.shop.util.Constant;

import java.util.ArrayList;
import java.util.List;

public class SentPoinOrderActivity extends BaseActivity {
    String nomorUser, idOrder;
    EditText teksTransaksi;
    TextView teksTotal, teksPoin;
    Button btnSend;
    ListView listview;
    Adapter adapter;
    ArrayList<ItemDetail> item_detail;
    int total = 0, poin = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_poin_order);
        getSupportActionBar().setTitle("SENT POIN AUTOMATIC");
        teksTransaksi = (EditText) findViewById(R.id.nomor_transaksi);
        btnSend = (Button) findViewById(R.id.btn_send);
        teksTotal = (TextView) findViewById(R.id.total_order);
        teksPoin = (TextView) findViewById(R.id.poin);
        listview = (ListView) findViewById(R.id.listview);
        item_detail = getIntent().getParcelableArrayListExtra("item_detail");
        nomorUser = getIntent().getStringExtra("nomor_user");
        idOrder = getIntent().getStringExtra("id_order");
        for (int i = 0; i < item_detail.size(); i++) {
            total += item_detail.get(i).getSubtotal();
            poin += item_detail.get(i).getPoin();
        }
        teksTotal.setText("Rp. " + total);
        teksPoin.setText(poin + " POINT");
        adapter = new Adapter(item_detail);
        listview.setAdapter(adapter);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nomor = teksTransaksi.getText().toString();
                if (nomor.length() > 0) {
                    Intent intent = new Intent(SentPoinOrderActivity.this, KonfirmasiPoinActivity.class);
                    intent.putExtra("nomor", nomor);
                    intent.putExtra("idOrder", idOrder);
                    intent.putExtra("total", String.valueOf(total));
                    intent.putExtra("poin", String.valueOf(poin));
                    intent.putExtra("nomorUser", nomorUser);
                    startActivity(intent);
                } else {
                    Toast.makeText(SentPoinOrderActivity.this, "Nomor Transaksi harus diisi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // app icon in action bar clicked; go home
            this.finish();
            overridePendingTransition(R.anim.open_main, R.anim.close_next);
        }
        return super.onOptionsItemSelected(item);
    }

    public class Adapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<ItemDetail> list;

        public Adapter(List<ItemDetail> list) {
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
                convertView = inflater.inflate(R.layout.list_order_detail, null);
            TextView teks_nama = (TextView) convertView.findViewById(R.id.nama);
            TextView teks_subtotal = (TextView) convertView.findViewById(R.id.subtotal);
            TextView teks_porsi = (TextView) convertView.findViewById(R.id.porsi);
            TextView teks_poin = (TextView) convertView.findViewById(R.id.poin);
            final ItemDetail item = list.get(position);
            teks_nama.setText(item.getNama());
            teks_subtotal.setText("Rp. " + item.getSubtotal());
            teks_porsi.setText(item.getQty() + " PORSI");
            teks_poin.setText(item.getPoin() + " POINT");
            return convertView;
        }
    }
}
