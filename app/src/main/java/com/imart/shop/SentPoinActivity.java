package com.imart.shop;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.imart.shop.util.Constant;

public class SentPoinActivity extends AppCompatActivity {
    Toolbar toolbar;
    Button btnQRScan, btnManualScan, btnOK, btnCancel;
    LinearLayout layoutScan, layoutManualScan;
    EditText teksNomorResi, teksTotalOrder, teksPoin, teksNomorUser;
    String nomorResi, totalOrder, poin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_poin);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("SENT POIN MANUAL");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Constant.COLOR));
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Constant.COLOR);
        } else {
            window.setTitleColor(Constant.COLOR);
        }
        layoutScan = (LinearLayout) findViewById(R.id.layout_scan);
        layoutManualScan = (LinearLayout) findViewById(R.id.layout_manual_scan);
        layoutManualScan.setVisibility(View.GONE);
        teksNomorResi = (EditText) findViewById(R.id.nomor_resi);
        teksTotalOrder = (EditText) findViewById(R.id.total_order);
        teksPoin = (EditText) findViewById(R.id.poin);
        teksNomorUser = (EditText) findViewById(R.id.nomor_user);
        btnQRScan = (Button) findViewById(R.id.btn_qr_scan);
        btnManualScan = (Button) findViewById(R.id.btn_manual_scan);
        btnOK = (Button) findViewById(R.id.btn_ok);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnQRScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cek()) {
                    new IntentIntegrator(SentPoinActivity.this).initiateScan();
                }
            }
        });
        btnManualScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cek()) {
                    layoutManualScan.setVisibility(View.VISIBLE);
                    layoutScan.setVisibility(View.GONE);
                    teksNomorResi.setEnabled(false);
                    teksTotalOrder.setEnabled(false);
                    teksPoin.setEnabled(false);
                }
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nomorUser = teksNomorUser.getText().toString();
                if (nomorUser.length() > 0) {
                    Intent intent = new Intent(SentPoinActivity.this, KonfirmasiPoinActivity.class);
                    intent.putExtra("nomor", nomorResi);
                    intent.putExtra("total", totalOrder);
                    intent.putExtra("poin", poin);
                    intent.putExtra("nomorUser", nomorUser);
                    startActivity(intent);
                } else {
                    Toast.makeText(SentPoinActivity.this, "Nomor User harus diisi", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutManualScan.setVisibility(View.GONE);
                layoutScan.setVisibility(View.VISIBLE);
                teksNomorResi.setEnabled(true);
                teksTotalOrder.setEnabled(true);
                teksPoin.setEnabled(true);
            }
        });
    }

    public boolean cek(){
        nomorResi = teksNomorResi.getText().toString();
        totalOrder = teksTotalOrder.getText().toString();
        poin = teksPoin.getText().toString();
        if (nomorResi.length() == 0 || totalOrder.length() == 0 || poin.length() == 0) {
            Toast.makeText(this, "Nomor Resi, Total Order, Poin harus diisi", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Tidak dapat terdeteksi", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(SentPoinActivity.this, KonfirmasiPoinActivity.class);
                intent.putExtra("nomor", nomorResi);
                intent.putExtra("total", totalOrder);
                intent.putExtra("poin", poin);
                intent.putExtra("nomorUser", result.getContents());
                startActivity(intent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
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
            this.finish();
            overridePendingTransition(R.anim.open_main, R.anim.close_next);
        }
        return super.onOptionsItemSelected(item);
    }
}
