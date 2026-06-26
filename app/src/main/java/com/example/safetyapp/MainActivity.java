package com.example.safetyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

// ⭐ AdMob Imports
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    LinearLayout cardSOS, cardCall, cardGPS, cardSMS, cardTips;

    SharedPreferences sp;

    // 🔥 DOUBLE PRESS
    private boolean doublePress = false;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🌐 APPLY LANGUAGE FIRST
        loadLanguage();

        // 🔐 LOGIN CHECK
        sp = getSharedPreferences("UserData", MODE_PRIVATE);
        boolean isLoggedIn = sp.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        // ⭐⭐⭐ AdMob Initialize & Load Banner ⭐⭐⭐
        MobileAds.initialize(this, initializationStatus -> {});
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        // 🔧 TOOLBAR
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 📂 DRAWER
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 👤 USER NAME
        View headerView = navigationView.getHeaderView(0);
        TextView tvUserName = headerView.findViewById(R.id.tvUserName);

        String name = sp.getString("username", "User");
        tvUserName.setText("Hello " + name + " 👋");

        // 🔘 CARDS
        cardSOS = findViewById(R.id.cardSOS);
        cardCall = findViewById(R.id.cardCall);
        cardGPS = findViewById(R.id.cardGPS);
        cardSMS = findViewById(R.id.cardSMS);
        cardTips = findViewById(R.id.cardTips);

        // 🔥 CLICK EVENTS
        cardSOS.setOnClickListener(v ->
                startActivity(new Intent(this, SOSActivity.class)));

        cardCall.setOnClickListener(v ->
                startActivity(new Intent(this, FakeCallActivity.class)));

        cardGPS.setOnClickListener(v ->
                startActivity(new Intent(this, GPSTrackingActivity.class)));

        cardSMS.setOnClickListener(v ->
                startActivity(new Intent(this, SMSAlertActivity.class)));

        cardTips.setOnClickListener(v ->
                startActivity(new Intent(this, SafetyTipsActivity.class)));

        // 📑 DRAWER MENU
        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));

            } else if (id == R.id.nav_history) {
                startActivity(new Intent(this, HistoryActivity.class));

            } else if (id == R.id.nav_help) {
                startActivity(new Intent(this, HelpActivity.class));

            } else if (id == R.id.nav_logout) {

                sp.edit().clear().apply();

                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    // 🌐 LANGUAGE LOAD METHOD
    private void loadLanguage() {

        SharedPreferences sp = getSharedPreferences("SETTINGS", MODE_PRIVATE);
        String lang = sp.getString("lang", "en");

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        getResources().updateConfiguration(
                config,
                getResources().getDisplayMetrics()
        );
    }

    // 🔥 VOLUME DOUBLE PRESS
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

            if (doublePress) {

                Intent intent = new Intent(this, FakeCallActivity.class);
                intent.putExtra("name", "Mom");
                startActivity(intent);

                doublePress = false;

            } else {

                doublePress = true;
                handler.postDelayed(() -> doublePress = false, 800);
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}