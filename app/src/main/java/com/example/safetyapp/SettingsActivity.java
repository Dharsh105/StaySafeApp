package com.example.safetyapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    LinearLayout profileLayout, sosLayout, notiLayout, langLayout,
            askLayout, faqLayout, privacyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarSettings);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Init
        profileLayout = findViewById(R.id.profileLayout);
        sosLayout = findViewById(R.id.sosLayout);
        notiLayout = findViewById(R.id.notiLayout);

        askLayout = findViewById(R.id.askLayout);
        faqLayout = findViewById(R.id.faqLayout);
        privacyLayout = findViewById(R.id.privacyLayout);

        // Clicks
        profileLayout.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        sosLayout.setOnClickListener(v ->
                startActivity(new Intent(this, SosSettingsActivity.class)));

        notiLayout.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationActivity.class)));


        askLayout.setOnClickListener(v ->
                startActivity(new Intent(this, AskQuestionActivity.class)));

        faqLayout.setOnClickListener(v ->
                startActivity(new Intent(this, FAQActivity.class)));

        privacyLayout.setOnClickListener(v ->
                startActivity(new Intent(this, PrivacyPolicyActivity.class)));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}