package com.example.safetyapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.ArrayList;

public class SOSActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 100;

    private FusedLocationProviderClient fusedLocationClient;

    private TextView txtCount;

    private int pressCount = 0;
    private long lastPressTime = 0;

    private final int MAX_INTERVAL = 3000;

    private String num1="", num2="", num3="";

    private String username="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sosactivity);

        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);

        txtCount = findViewById(R.id.txtCount);

        Button btnReset = findViewById(R.id.btnReset);

        btnReset.setOnClickListener(v -> resetCount());

        SharedPreferences loginPref =
                getSharedPreferences("LOGIN_PREF", MODE_PRIVATE);

        username = loginPref.getString("username","");

        loadSOSNumbers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSOSNumbers();
    }

    private void loadSOSNumbers(){

        SharedPreferences sp =
                getSharedPreferences("SOS_PREFS_" + username, MODE_PRIVATE);

        num1 = sp.getString("num1","");
        num2 = sp.getString("num2","");
        num3 = sp.getString("num3","");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){

        if(keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
                keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){

            long currentTime = System.currentTimeMillis();

            if(currentTime - lastPressTime > MAX_INTERVAL){
                pressCount = 0;
            }

            lastPressTime = currentTime;

            pressCount++;

            txtCount.setText("Count: " + pressCount);

            if(pressCount == 3){

                sendSOS();
                resetCount();
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void resetCount(){

        pressCount = 0;
        txtCount.setText("Count: 0");
    }

    private void sendSOS(){

        loadSOSNumbers();

        if(num1.isEmpty() && num2.isEmpty() && num3.isEmpty()){

            Toast.makeText(this,
                    "No SOS number saved",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if(!hasPermissions()){

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.SEND_SMS,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CALL_PHONE
                    },
                    REQUEST_CODE
            );

            return;
        }

        getLocationAndSendSMS();
        makeEmergencyCall();
    }

    private boolean hasPermissions(){

        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED

                &&

                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED

                &&

                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CALL_PHONE)
                        == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("MissingPermission")
    private void getLocationAndSendSMS(){

        fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null).addOnSuccessListener(location -> {

            String message;

            if(location!=null){

                double lat = location.getLatitude();
                double lon = location.getLongitude();

                message = "🚨 SOS! I need help!\n"
                        + "📍 My Location:\n"
                        + "https://maps.google.com/?q="
                        + lat + "," + lon;

            }else{

                message = "🚨 SOS! I need help!";
            }

            sendSMS(message);
        });
    }

    private void sendSMS(String message){

        try{

            SmsManager smsManager = SmsManager.getDefault();

            ArrayList<String> parts =
                    smsManager.divideMessage(message);

            if(!num1.isEmpty())
                smsManager.sendMultipartTextMessage(
                        num1,null,parts,null,null);

            if(!num2.isEmpty())
                smsManager.sendMultipartTextMessage(
                        num2,null,parts,null,null);

            if(!num3.isEmpty())
                smsManager.sendMultipartTextMessage(
                        num3,null,parts,null,null);

            Toast.makeText(this,
                    "SOS Message Sent",
                    Toast.LENGTH_LONG).show();

        }catch (Exception e){

            Toast.makeText(this,
                    "SMS Failed",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void makeEmergencyCall(){

        try{

            if(num1==null || num1.isEmpty()) return;

            Intent callIntent =
                    new Intent(Intent.ACTION_CALL);

            callIntent.setData(Uri.parse("tel:" + num1));

            startActivity(callIntent);

        }catch (Exception e){

            Toast.makeText(this,
                    "Call Failed",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults){

        super.onRequestPermissionsResult(
                requestCode,permissions,grantResults);

        if(requestCode == REQUEST_CODE){

            if(grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED){

                getLocationAndSendSMS();
                makeEmergencyCall();

            }else{

                Toast.makeText(this,
                        "Permission Denied",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}