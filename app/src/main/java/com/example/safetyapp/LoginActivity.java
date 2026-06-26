package com.example.safetyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvRegister;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sp = getSharedPreferences("UserData", MODE_PRIVATE);
        boolean isLoggedIn = sp.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        db = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(v -> loginUser());

        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void loginUser() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

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

                    if (querySnapshot.isEmpty()) {
                        Toast.makeText(this,"User not found",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String dbPassword = querySnapshot.getDocuments().get(0).getString("password");
                    String userName = querySnapshot.getDocuments().get(0).getString("name");

                    if (dbPassword != null && dbPassword.equals(password)) {

                        // ✅ ONLY SET USERNAME (NO CLEAR)
                        SharedPreferences loginPref =
                                getSharedPreferences("LOGIN_PREF", MODE_PRIVATE);

                        loginPref.edit().putString("username", userName).apply();

                        SharedPreferences sp =
                                getSharedPreferences("UserData", MODE_PRIVATE);

                        sp.edit().putString("username", userName)
                                .putBoolean("isLoggedIn", true)
                                .apply();

                        Toast.makeText(this,"Login Successful",Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();

                    } else {
                        Toast.makeText(this,"Invalid Password",Toast.LENGTH_SHORT).show();
                    }

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,"Error: " + e.getMessage(),Toast.LENGTH_SHORT).show());
    }
}