package com.dietplanner.meal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Button btnAddPlan, btnSearch;
    TextView tvWelcome;
    String username, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAddPlan = findViewById(R.id.btnAddPlan);
        btnSearch = findViewById(R.id.btnSearch);
        tvWelcome = findViewById(R.id.tvWelcome);

        username = getIntent().getStringExtra("username");
        name = getIntent().getStringExtra("name");
        tvWelcome.setText("Welcome, " + name);

        btnAddPlan.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, AddPlanActivity.class);
            i.putExtra("username", username);
            i.putExtra("name", name);
            startActivity(i);
        });

        btnSearch.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(i);
        });
    }
}
