package com.example.safetyapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    HistoryAdapter historyAdapter;
    ArrayList<String> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // MUST MATCH XML FILE NAME
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.recyclerHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        historyList = new ArrayList<>();
        historyList.add("SOS Button Pressed");
        historyList.add("Fake Call Activated");
        historyList.add("SMS Alert Sent");
        historyList.add("GPS Location Shared");

        historyAdapter = new HistoryAdapter(historyList);
        recyclerView.setAdapter(historyAdapter);
    }
}
