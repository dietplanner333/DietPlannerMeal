package com.dietplanner.meal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dietplanner.meal.databinding.ActivityLoginBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private SupabaseApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        api = RetrofitClient.getRestClient().create(SupabaseApi.class);

        // Pre-fill login
        binding.inputUsername.setText("");
        binding.inputPassword.setText("");

        binding.btnLogin.setOnClickListener(v -> {
            String username = binding.inputUsername.getText().toString().trim();
            String password = binding.inputPassword.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            login(username, password);
        });
    }

    private void login(String username, String password) {
        // Query Supabase for userdetails
        Call<List<UserDetail>> call = api.getUserByUsername("name,salt,hash", "eq." + username);
        call.enqueue(new Callback<List<UserDetail>>() {
            @Override
            public void onResponse(Call<List<UserDetail>> call, Response<List<UserDetail>> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserDetail user = response.body().get(0);
                String computedHash = HashUtil.sha256Hex(user.salt + password);

                if (computedHash != null && computedHash.equals(user.hash)) {
                    // Login success
                    /*startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();*/
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("username", user.username);
                    intent.putExtra("name", user.name);  // or user.username if you don’t have a separate name field
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserDetail>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
