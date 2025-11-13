package com.dietplanner.meal;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    EditText etQuery;
    Button btnSearch;
    TableLayout table;
    SupabaseApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        etQuery = findViewById(R.id.etQuery);
        btnSearch = findViewById(R.id.btnSearch);
        table = findViewById(R.id.resultsTable);

        api = RetrofitClient.getRpcClient().create(SupabaseApi.class);

        btnSearch.setOnClickListener(v -> {
            String q = etQuery.getText().toString();
            Map<String, String> body = new HashMap<>();
            body.put("q", q);

            // call vulnerable RPC
            Call<List<Plan>> call = api.searchPlansVulnerable(body);
            call.enqueue(new Callback<List<Plan>>() {
                @Override
                public void onResponse(Call<List<Plan>> call, Response<List<Plan>> response) {
                    if (!response.isSuccessful() || response.body() == null) {
                        Toast.makeText(SearchActivity.this, "Search error", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<Plan> results = response.body();
                    displayResults(results);
                }
                @Override
                public void onFailure(Call<List<Plan>> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(SearchActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void displayResults(List<Plan> plans){
        table.removeAllViews();
        // header
        TableRow header = new TableRow(this);
        header.addView(textView("Name", true));
        header.addView(textView("Meal", true));
        header.addView(textView("Calories", true));
        table.addView(header);

        for (Plan p : plans) {
            TableRow row = new TableRow(this);
            row.addView(textView(p.name, false));
            row.addView(textView(p.meal, false));
            row.addView(textView(String.valueOf(p.calories), false));
            table.addView(row);
        }
    }

    private TextView textView(String text, boolean bold){
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(8,8,8,8);
        if (bold) tv.setTextAppearance(android.R.style.TextAppearance_Material_Medium);
        return tv;
    }
}
