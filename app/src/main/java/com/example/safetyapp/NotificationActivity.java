package com.example.safetyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationActivity extends AppCompatActivity {

    Switch soundSwitch, vibrationSwitch;
    Button btnSelectTone;
    TextView tvSelectedTone;

    Uri selectedRingtoneUri;

    static final int RINGTONE_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // INIT
        soundSwitch = findViewById(R.id.soundSwitch);
        vibrationSwitch = findViewById(R.id.vibrationSwitch);
        btnSelectTone = findViewById(R.id.btnSelectTone);
        tvSelectedTone = findViewById(R.id.tvSelectedTone);

        // LOAD SAVED SETTINGS
        SharedPreferences sp = getSharedPreferences("SETTINGS", MODE_PRIVATE);

        soundSwitch.setChecked(sp.getBoolean("sound", true));
        vibrationSwitch.setChecked(sp.getBoolean("vibration", true));

        String savedTone = sp.getString("toneName", "Default Tone");
        tvSelectedTone.setText("Tone : " + savedTone);

        // SWITCH EVENTS
        soundSwitch.setOnCheckedChangeListener((b, isChecked) -> {
            sp.edit().putBoolean("sound", isChecked).apply();
            Toast.makeText(this,
                    isChecked ? "Sound ON" : "Sound OFF",
                    Toast.LENGTH_SHORT).show();
        });

        vibrationSwitch.setOnCheckedChangeListener((b, isChecked) -> {
            sp.edit().putBoolean("vibration", isChecked).apply();
            Toast.makeText(this,
                    isChecked ? "Vibration ON" : "Vibration OFF",
                    Toast.LENGTH_SHORT).show();
        });

        // 🎵 SELECT RINGTONE BUTTON
        btnSelectTone.setOnClickListener(v -> {

            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);

            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
                    RingtoneManager.TYPE_NOTIFICATION);

            startActivityForResult(intent, RINGTONE_REQUEST_CODE);
        });
    }

    // 🎵 RESULT
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RINGTONE_REQUEST_CODE && resultCode == RESULT_OK) {

            Uri uri = data.getParcelableExtra(
                    RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null) {

                selectedRingtoneUri = uri;

                String toneName = RingtoneManager
                        .getRingtone(this, uri)
                        .getTitle(this);

                tvSelectedTone.setText("Tone : " + toneName);

                // SAVE
                SharedPreferences sp = getSharedPreferences("SETTINGS", MODE_PRIVATE);
                sp.edit()
                        .putString("toneUri", uri.toString())
                        .putString("toneName", toneName)
                        .apply();

                Toast.makeText(this,
                        "Tone Selected",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}