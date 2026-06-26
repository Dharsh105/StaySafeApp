package com.example.safetyapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FakeCallActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    TextView txtCaller;
    ImageView btnAccept, btnDecline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_call);

        txtCaller = findViewById(R.id.txtCaller);
        btnAccept = findViewById(R.id.btnAccept);
        btnDecline = findViewById(R.id.btnDecline);

        // Caller name
        String name = getIntent().getStringExtra("name");
        if(name == null) name = "Unknown";
        txtCaller.setText(name + " Calling...");

        // 🔊 Ringtone SAFE
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.ringtone);
            if(mediaPlayer != null){
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        // Accept
        btnAccept.setOnClickListener(v -> stopCall());

        // Decline
        btnDecline.setOnClickListener(v -> stopCall());
    }

    void stopCall(){
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } catch (Exception e){}
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopCall();
    }
}