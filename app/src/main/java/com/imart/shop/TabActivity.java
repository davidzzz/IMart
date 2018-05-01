package com.imart.shop;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.imart.shop.adapter.TabAdapter;
import com.imart.shop.model.ItemOrder;
import com.imart.shop.util.Constant;
import com.imart.shop.util.SessionManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class TabActivity extends BaseActivity {
    ViewPager pager;
    TabAdapter adapter;
    TabLayout tabs;
    HashMap<String, String> user;
    String tipe, akses;
    OrderList orderList, historyList;
    FragOrderProses fragOrder;
    FragmentHistory fragHistory;
    List<ItemOrder> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        tipe = getIntent().getStringExtra("tipe");
        getSupportActionBar().setIcon(R.drawable.logo_imart);
        getSupportActionBar().setTitle("");
        LinearLayout layoutPoin = (LinearLayout) findViewById(R.id.layout_poin);
        SessionManager session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
        akses = user.get(SessionManager.KEY_AKSES);

        adapter = new TabAdapter(getSupportFragmentManager());
        if (tipe.equals("history")) {
            boolean isFlashDeal = getIntent().getBooleanExtra("isFlashDeal", false);
            layoutPoin.setVisibility(View.GONE);
            if (akses.equals("2")) {
                orderList = OrderList.newInstance("order", isFlashDeal);
                historyList = OrderList.newInstance("history", isFlashDeal);
                adapter.setFragment(orderList, "Order");
                adapter.setFragment(historyList, "History");
            } else {
                fragOrder = FragOrderProses.newInstance(isFlashDeal);
                fragHistory = FragmentHistory.newInstance(isFlashDeal);
                adapter.setFragment(fragOrder, "Order");
                adapter.setFragment(fragHistory, "History");
            }
        } else if (tipe.equals("voucher")) {
            adapter.setFragment(new VoucherList(), "Voucher");
            if (akses.equals("2")) {
                layoutPoin.setVisibility(View.GONE);
                adapter.setFragment(new OrderVoucher(), "Order Voucher");
            } else {
                layoutPoin.setVisibility(View.VISIBLE);
                TextView poin = (TextView) findViewById(R.id.poin);
                poin.setText("" + user.get(SessionManager.KEY_POIN));
                Button spin = (Button) findViewById(R.id.spin);
                spin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(TabActivity.this, SpinActivity.class));
                    }
                });
                adapter.setFragment(new MyVoucher(), "My Voucher");
            }
        }
        pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);

        tabs = (TabLayout)findViewById(R.id.tabs);
        tabs.setupWithViewPager(pager);
    }

    public class CompareNama implements Comparator<ItemOrder> {
        @Override
        public int compare(ItemOrder i1, ItemOrder i2) {
            return i1.getNama().compareToIgnoreCase(i2.getNama());
        }
    }

    public class CompareTanggal implements Comparator<ItemOrder> {
        @Override
        public int compare(ItemOrder i1, ItemOrder i2) {
            return i1.getTanggal().compareToIgnoreCase(i2.getTanggal());
        }
    }

    public class CompareStatus implements Comparator<ItemOrder> {
        @Override
        public int compare(ItemOrder i1, ItemOrder i2) {
            return i1.getStatus().compareToIgnoreCase(i2.getStatus());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (tipe.equals("history")) {
            getMenuInflater().inflate(R.menu.menu_order, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }/* else if (id == R.id.nama || id == R.id.tanggal || id == R.id.status) {
            Comparator<ItemOrder> c;
            if (id == R.id.nama) {
                c = new CompareNama();
            } else if (id == R.id.tanggal) {
                c = new CompareTanggal();
            } else {
                c = new CompareStatus();
            }
            if (akses.equals("2")) {
                list = orderList.getListItem();
                Collections.sort(list, c);
                orderList.getAdapter().notifyDataSetChanged();
                list = historyList.getListItem();
                Collections.sort(list, c);
                historyList.getAdapter().notifyDataSetChanged();
            } else {
                list = fragOrder.getListItem();
                Collections.sort(list, c);
                fragOrder.getAdapter().notifyDataSetChanged();
                list = fragHistory.getListItem();
                Collections.sort(list, c);
                fragHistory.getAdapter().notifyDataSetChanged();
            }
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (tipe.equals("voucher")) {
            TextView poin = (TextView) findViewById(R.id.poin);
            poin.setText("" + user.get(SessionManager.KEY_POIN));
        }
    }
}
