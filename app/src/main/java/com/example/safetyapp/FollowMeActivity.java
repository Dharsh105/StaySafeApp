package com.example.safetyapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class FollowMeActivity extends AppCompatActivity {

    EditText etCurrent, etDestination;
    LinearLayout btnWalk, btnBike, btnCar, btnBus, btnStart;

    String travelMode = "walking";

    FusedLocationProviderClient fusedLocationClient;
    double currentLat = 0, currentLon = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_me);

        // INIT
        etCurrent = findViewById(R.id.etCurrent);
        etDestination = findViewById(R.id.etDestination);

        btnWalk = findViewById(R.id.btnWalk);
        btnBike = findViewById(R.id.btnBike);
        btnCar = findViewById(R.id.btnCar);
        btnBus = findViewById(R.id.btnBus);
        btnStart = findViewById(R.id.btnStart);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getCurrentLocation();

        // MODE SELECT
        btnWalk.setOnClickListener(v -> {
            travelMode = "walking";
            Toast.makeText(this, "Walk Selected", Toast.LENGTH_SHORT).show();
        });

        btnBike.setOnClickListener(v -> {
            travelMode = "bicycling";
            Toast.makeText(this, "Bike Selected", Toast.LENGTH_SHORT).show();
        });

        btnCar.setOnClickListener(v -> {
            travelMode = "driving";
            Toast.makeText(this, "Car Selected", Toast.LENGTH_SHORT).show();
        });

        btnBus.setOnClickListener(v -> {
            travelMode = "transit";
            Toast.makeText(this, "Bus Selected", Toast.LENGTH_SHORT).show();
        });

        // START NAVIGATION
        btnStart.setOnClickListener(v -> openNavigation());
    }

    private void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        currentLat = location.getLatitude();
                        currentLon = location.getLongitude();
                        etCurrent.setText(currentLat + "," + currentLon);
                    }
                });
    }

    private void openNavigation() {

        String destination = etDestination.getText().toString();

        if (destination.isEmpty()) {
            Toast.makeText(this, "Enter destination", Toast.LENGTH_SHORT).show();
            return;
        }

        String uri = "https://www.google.com/maps/dir/?api=1"
                + "&origin=" + currentLat + "," + currentLon
                + "&destination=" + destination
                + "&travelmode=" + travelMode;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            getCurrentLocation();
        }
    }
}