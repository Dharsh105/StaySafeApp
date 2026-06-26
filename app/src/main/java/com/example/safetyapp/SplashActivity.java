package com.example.safetyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            SharedPreferences sp = getSharedPreferences("UserData", MODE_PRIVATE);
            boolean isLoggedIn = sp.getBoolean("isLoggedIn", false);

            if (isLoggedIn) {

                // 🔥 Direct Home
                startActivity(new Intent(SplashActivity.this, MainActivity.class));

            } else {

                // 🔥 Go to Login
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }

            finish();

        }, 2000);
    }
}