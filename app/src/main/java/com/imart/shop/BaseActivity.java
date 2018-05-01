package com.imart.shop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.imart.shop.adapter.DrawerMenuItemAdapter;
import com.imart.shop.model.DrawerMenuItem;
import com.imart.shop.util.Constant;
import com.imart.shop.util.SessionManager;
import com.imart.shop.view.RoundImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BaseActivity extends AppCompatActivity implements AdapterView.OnItemClickListener
{
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    ListView mLvDrawerMenu;
    List<DrawerMenuItem> menuItems;
    DrawerMenuItemAdapter mDrawerMenuAdapter;
    SessionManager session;
    HashMap<String, String> user;
    String photo, akses;

    protected void onCreateDrawer()
    {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Constant.COLOR));
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
        akses = user.get(SessionManager.KEY_AKSES);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLvDrawerMenu = (ListView) findViewById(R.id.lv_drawer_menu);
        View headerView = getLayoutInflater().inflate(R.layout.header_drawer, mLvDrawerMenu, false);
        LinearLayout lytHd = (LinearLayout) headerView.findViewById(R.id.lytHeader);
        ImageView imghd = (ImageView) headerView.findViewById(R.id.image);
        TextView namaHeader = (TextView) headerView.findViewById(R.id.nama);
        TextView desHeader = (TextView) headerView.findViewById(R.id.des);
        if (isLogin()) {
            lytHd.setVisibility(View.VISIBLE);
            namaHeader.setText(user.get(SessionManager.KEY_NAME));
            desHeader.setText(user.get(SessionManager.KEY_EMAIL));
            photo = user.get(SessionManager.KEY_PHOTO);

            Glide.with(this)
                    .load(photo != null && !photo.equals("-") ? photo : R.drawable.user)
                    .transform(new RoundImage(this))
                    .into(imghd);
        } else {
            lytHd.setVisibility(View.GONE);
        }
        mLvDrawerMenu.addHeaderView(headerView);
        menuItems = new ArrayList<DrawerMenuItem>();
        //buat list beda klo login dan tidak login
        DrawerMenuItem test1 = new DrawerMenuItem(1, "Profile", R.drawable.qr);
        DrawerMenuItem test2 = new DrawerMenuItem(2, "Voucher / Point", R.drawable.voucher);
        DrawerMenuItem test3 = new DrawerMenuItem(3, "My Flash Deal", R.drawable.flash_deal);
        DrawerMenuItem test4 = new DrawerMenuItem(4, "Kategori", R.drawable.category);
        DrawerMenuItem test5 = new DrawerMenuItem(5, "History", R.drawable.history);
        DrawerMenuItem test6 = new DrawerMenuItem(6, "About", R.drawable.about_black);
        DrawerMenuItem test7 = new DrawerMenuItem(7, isLogin() ? "Logout" : "Login", R.drawable.user);
        menuItems.add(test1);
        menuItems.add(test2);
        menuItems.add(test3);
        menuItems.add(test4);
        menuItems.add(test5);
        menuItems.add(test6);
        menuItems.add(test7);
        if (!isLogin()) {
            DrawerMenuItem test8 = new DrawerMenuItem(8, "Daftar", R.drawable.user);
            menuItems.add(test8);
        }
        mDrawerMenuAdapter = new DrawerMenuItemAdapter(this, R.layout.layout_drawer_menu_item, menuItems);
        mLvDrawerMenu.setAdapter(mDrawerMenuAdapter);
        mLvDrawerMenu.setOnItemClickListener(this);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mLvDrawerMenu)) {
            mDrawerLayout.closeDrawer(mLvDrawerMenu);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                mDrawerLayout.closeDrawer(mLvDrawerMenu);
                break;
            case 1:
                if (isLogin()) {
                    Intent profile = new Intent(this, MenuActivity.class);
                    startActivity(profile);
                } else {
                    Intent i = new Intent(this, LoginActivity.class);
                    startActivity(i);
                }
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                break;
            case 2:
                if (isLogin()) {
                    Intent intentVoucher = new Intent(this, TabActivity.class);
                    intentVoucher.putExtra("tipe", "voucher");
                    startActivity(intentVoucher);
                } else {
                    Intent i = new Intent(this, LoginActivity.class);
                    startActivity(i);
                }
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                break;
            case 3:
                if (isLogin()) {
                    Intent intentHistory = new Intent(this, TabActivity.class);
                    intentHistory.putExtra("tipe", "history");
                    intentHistory.putExtra("isFlashDeal", true);
                    startActivity(intentHistory);
                } else {
                    Intent i = new Intent(this, LoginActivity.class);
                    startActivity(i);
                }
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                break;
            case 4:
                if (isLogin()) {
                    Intent intent = new Intent(this, KategoriActivity.class);
                    intent.putExtra("color", Constant.COLORVALUE);
                    startActivity(intent);
                } else {
                    Intent i = new Intent(this, LoginActivity.class);
                    startActivity(i);
                }
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                break;
            case 5:
                if (isLogin()) {
                    Intent intentHistory = new Intent(this, TabActivity.class);
                    intentHistory.putExtra("tipe", "history");
                    startActivity(intentHistory);
                } else {
                    Intent i = new Intent(this, LoginActivity.class);
                    startActivity(i);
                }
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                break;
            case 6:
                Intent intent = new Intent(this, HelpDetailActivity.class);
                intent.putExtra("id", 1);
                startActivity(intent);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                break;
            case 7:
                if (isLogin()) {
                    Logout();
                } else {
                    Intent i = new Intent(this, LoginActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.open_next, R.anim.close_next);
                }
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                break;
            case 8:
                if (!isLogin()) {
                    Intent i = new Intent(this, RegisterActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.open_next, R.anim.close_next);
                }
                break;
        }
    }

    public void Logout() {
        SharedPreferences pref = getSharedPreferences(SessionManager.PREF_NAME, SessionManager.PRIVATE_MODE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        startActivity(i);

        finish();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public boolean isLogin() {
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        return sessionManager.isLoggedIn();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID)
    {
        super.setContentView(layoutResID);
        onCreateDrawer();
    }

    @Override
    protected void onDestroy() {
        mLvDrawerMenu.setAdapter(null);
        super.onDestroy();
    }
}