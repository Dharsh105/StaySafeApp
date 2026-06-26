package com.example.safetyapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HelpActivity extends AppCompatActivity {

    Button btnEmailSupport, btnCallSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        btnEmailSupport = findViewById(R.id.btnEmailSupport);
        btnCallSupport = findViewById(R.id.btnCallSupport);

        // Email Support
        btnEmailSupport.setOnClickListener(v -> {

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:support@safetyapp.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support Request");
            startActivity(emailIntent);
        });

        // Call Support
        btnCallSupport.setOnClickListener(v -> {

            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:9876543210"));
            startActivity(callIntent);
        });
    }
}