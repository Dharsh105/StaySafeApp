package com.example.safetyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etEmail, etPassword;
    Button btnRegister;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);

        db = FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Enter Name");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Enter Email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Enter Password");
            return;
        }

        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (!querySnapshot.isEmpty()) {

                        Toast.makeText(this,"Email already registered",Toast.LENGTH_SHORT).show();

                    } else {

                        Map<String, Object> user = new HashMap<>();
                        user.put("name", name);
                        user.put("email", email);
                        user.put("password", password);

                        db.collection("users")
                                .add(user)
                                .addOnSuccessListener(documentReference -> {

                                    // ✅ ONLY SET USERNAME (NO CLEAR)
                                    SharedPreferences loginPref =
                                            getSharedPreferences("LOGIN_PREF", MODE_PRIVATE);

                                    loginPref.edit().putString("username", name).apply();

                                    SharedPreferences sp =
                                            getSharedPreferences("UserData", MODE_PRIVATE);

                                    sp.edit().putString("username", name)
                                            .putString("email", email)
                                            .putBoolean("isLoggedIn", true)
                                            .apply();

                                    Toast.makeText(this,"Registration Successful",Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this,"Error: " + e.getMessage(),Toast.LENGTH_SHORT).show());
                    }
                });
    }
}