package com.dietplanner.meal;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPlanActivity extends AppCompatActivity {

    Spinner spinnerMeals;
    EditText etCalories;
    Button btnSave;
    SupabaseApi api;
    String username, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addplan);

        spinnerMeals = findViewById(R.id.spinnerMeals);
        etCalories = findViewById(R.id.etCalories);
        btnSave = findViewById(R.id.btnSave);

        username = getIntent().getStringExtra("username");
        name = getIntent().getStringExtra("name");

        api = RetrofitClient.getRestClient().create(SupabaseApi.class);

        btnSave.setOnClickListener(v -> {
            String meal = spinnerMeals.getSelectedItem().toString();
            int calories;
            try {
                calories = Integer.parseInt(etCalories.getText().toString().trim());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Enter calories", Toast.LENGTH_SHORT).show();
                return;
            }
            Plan p = new Plan();
            p.name = name;
            p.meal = meal;
            p.calories = calories;

            Call<Void> call = api.insertPlan(p);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddPlanActivity.this, "Plan saved", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddPlanActivity.this, "Failed to save: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(AddPlanActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
