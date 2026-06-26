package com.example.safetyapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AskQuestionActivity extends AppCompatActivity {

    EditText etQuestion;
    Button btnSubmit;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question);

        etQuestion = findViewById(R.id.etQuestion);
        btnSubmit = findViewById(R.id.btnSubmit);

        db = FirebaseFirestore.getInstance();

        btnSubmit.setOnClickListener(v -> submitQuestion());
    }

    private void submitQuestion() {

        String question = etQuestion.getText().toString().trim();

        if (TextUtils.isEmpty(question)) {
            etQuestion.setError("Please enter your question");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("question", question);
        data.put("timestamp", System.currentTimeMillis());

        db.collection("questions")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this,
                            "Question Submitted",
                            Toast.LENGTH_SHORT).show();

                    etQuestion.setText("");
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Error: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }
}