package com.example.safetyapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class GPSTrackingActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    FusedLocationProviderClient fusedLocationClient;
    LocationCallback locationCallback;

    LinearLayout btnTrack, btnFollowMe;
    TextView txtTrack, tvSpeed, tvDistance;

    boolean isTracking = false;

    Marker marker;
    Polyline polyline;
    List<LatLng> routePoints = new ArrayList<>();

    Location previousLocation = null;
    float totalDistance = 0;

    FirebaseFirestore db;

    static final int LOCATION_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpstracking);

        btnTrack = findViewById(R.id.btnTrack);
        btnFollowMe = findViewById(R.id.btnFollowMe);

        txtTrack = findViewById(R.id.txtTrack);
        tvSpeed = findViewById(R.id.tvSpeed);
        tvDistance = findViewById(R.id.tvDistance);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        db = FirebaseFirestore.getInstance();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // 🔥 TRACK BUTTON FIXED
        btnTrack.setOnClickListener(v -> {

            if (!isTracking) {

                // PERMISSION CHECK
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_CODE);
                    return;
                }

                isTracking = true;
                txtTrack.setText("Stop Tracking");
                startLocationUpdates();

                Toast.makeText(this, "Tracking Started", Toast.LENGTH_SHORT).show();

            } else {

                isTracking = false;
                txtTrack.setText("Start Tracking");
                stopLocationUpdates();

                Toast.makeText(this, "Tracking Stopped", Toast.LENGTH_SHORT).show();
            }
        });

        // FOLLOW PAGE
        btnFollowMe.setOnClickListener(v -> {
            startActivity(new Intent(this, FollowMeActivity.class));
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
        }
    }

    private void startLocationUpdates() {

        LocationRequest request = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 4000).build();

        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(@NonNull LocationResult result) {

                if (result == null || mMap == null) return;

                Location location = result.getLastLocation();

                if (location != null) {

                    LatLng latLng = new LatLng(
                            location.getLatitude(),
                            location.getLongitude());

                    updateMarker(latLng);
                    updateRoute(latLng);
                    updateSpeed(location);
                    updateDistance(location);
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        fusedLocationClient.requestLocationUpdates(
                request, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    private void updateMarker(LatLng loc) {

        if (marker == null) {
            marker = mMap.addMarker(new MarkerOptions().position(loc));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 16));
        } else {
            marker.setPosition(loc);
        }
    }

    private void updateRoute(LatLng loc) {

        routePoints.add(loc);

        if (polyline == null) {
            polyline = mMap.addPolyline(
                    new PolylineOptions().addAll(routePoints).width(8).color(Color.BLUE));
        } else {
            polyline.setPoints(routePoints);
        }
    }

    private void updateSpeed(Location loc) {
        float speed = loc.getSpeed() * 3.6f;
        tvSpeed.setText("Speed : " + (int) speed + " km/h");
    }

    private void updateDistance(Location loc) {

        if (previousLocation != null) {
            totalDistance += previousLocation.distanceTo(loc);
        }

        previousLocation = loc;

        tvDistance.setText("Distance : " +
                String.format("%.2f", totalDistance / 1000) + " km");
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int code,
                                           @NonNull String[] permissions,
                                           @NonNull int[] results) {

        super.onRequestPermissionsResult(code, permissions, results);

        if (code == LOCATION_PERMISSION_CODE &&
                results.length > 0 &&
                results[0] == PackageManager.PERMISSION_GRANTED) {

            startLocationUpdates(); // 🔥 IMPORTANT FIX
        }
    }
}