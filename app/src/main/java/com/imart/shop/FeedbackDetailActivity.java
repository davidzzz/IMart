package com.imart.shop;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.widget.RatingBar;
import android.widget.TextView;

import com.imart.shop.model.Feedback;
import com.imart.shop.util.Constant;

public class FeedbackDetailActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView teksNama, teksKomentar, teksWaktu;
    RatingBar rating1, rating2, rating3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("RESERVASI");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Constant.COLOR));
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Constant.COLOR);
        } else {
            window.setTitleColor(Constant.COLOR);
        }
        teksNama = (TextView) findViewById(R.id.nama);
        teksKomentar = (TextView) findViewById(R.id.komentar);
        teksWaktu = (TextView) findViewById(R.id.waktu);
        rating1 = (RatingBar) findViewById(R.id.kelayakan_harga);
        rating2 = (RatingBar) findViewById(R.id.kebersihan);
        rating3 = (RatingBar) findViewById(R.id.pelayanan);
        Feedback feedback = getIntent().getParcelableExtra("feedback");
        teksNama.setText(feedback.getNama());
        teksKomentar.setText(feedback.getKomentar());
        teksWaktu.setText(feedback.getWaktu());
        rating1.setRating(feedback.getRating1());
        rating2.setRating(feedback.getRating2());
        rating3.setRating(feedback.getRating3());
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
