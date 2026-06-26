package com.example.safetyapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity {

    TextView tvName, tvEmail;
    ImageView imgProfile;
    Button btnEditProfile, btnLogout;

    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageRef;

    String userName;
    Uri imageUri;

    ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            imageUri = result.getData().getData();
                            uploadImageToFirebase();
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        imgProfile = findViewById(R.id.imgProfile);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        SharedPreferences sp = getSharedPreferences("UserData", MODE_PRIVATE);
        userName = sp.getString("username", "User");

        tvName.setText(userName);

        loadUserData();
        loadProfileImage();

        imgProfile.setOnClickListener(v -> checkPermissionAndOpenGallery());
        btnEditProfile.setOnClickListener(v -> showEditDialog());

        btnLogout.setOnClickListener(v -> {
            sp.edit().clear().apply();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void checkPermissionAndOpenGallery() {

        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 101);
            } else {
                openGallery();
            }
        } else {
            openGallery();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void uploadImageToFirebase() {

        if (imageUri == null) return;

        StorageReference fileRef = storageRef.child(
                "profileImages/" + System.currentTimeMillis() + ".jpg");

        fileRef.putFile(imageUri)
                .continueWithTask(task -> fileRef.getDownloadUrl())
                .addOnSuccessListener(uri -> {

                    String imageUrl = uri.toString();

                    db.collection("users")
                            .whereEqualTo("name", userName)
                            .get()
                            .addOnSuccessListener(snapshot -> {
                                if (!snapshot.isEmpty()) {
                                    snapshot.getDocuments()
                                            .get(0)
                                            .getReference()
                                            .update("profileImage", imageUrl);
                                }
                            });

                    Glide.with(this).load(imageUrl).into(imgProfile);

                    Toast.makeText(this,
                            "Profile Updated",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void loadProfileImage() {

        db.collection("users")
                .whereEqualTo("name", userName)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (!snapshot.isEmpty()) {

                        String imageUrl = snapshot.getDocuments()
                                .get(0)
                                .getString("profileImage");

                        if (imageUrl != null) {
                            Glide.with(this)
                                    .load(imageUrl)
                                    .into(imgProfile);
                        }
                    }
                });
    }

    private void loadUserData() {

        db.collection("users")
                .whereEqualTo("name", userName)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (!snapshot.isEmpty()) {

                        String email = snapshot.getDocuments()
                                .get(0)
                                .getString("email");

                        tvEmail.setText(email);
                    }
                });
    }

    private void showEditDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Name");

        final EditText input = new EditText(this);
        input.setText(userName);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {

            String newName = input.getText().toString().trim();

            if (!newName.isEmpty()) {

                tvName.setText(newName);

                SharedPreferences sp =
                        getSharedPreferences("UserData", MODE_PRIVATE);

                sp.edit().putString("username", newName).apply();

                db.collection("users")
                        .whereEqualTo("name", userName)
                        .get()
                        .addOnSuccessListener(snapshot -> {

                            if (!snapshot.isEmpty()) {

                                snapshot.getDocuments()
                                        .get(0)
                                        .getReference()
                                        .update("name", newName);
                            }
                        });

                userName = newName;
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}