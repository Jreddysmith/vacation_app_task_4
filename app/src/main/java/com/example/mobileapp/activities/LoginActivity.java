package com.example.mobileapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.mobileapp.R;
import com.example.mobileapp.models.User;
import com.example.mobileapp.viewmodels.UserViewModel;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton, registerButton;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            userViewModel.getUserByUsername(username, user -> {
                if (user != null && user.getPassword().equals(password)) {
                    saveUserName(user.getUsername());
                    runOnUiThread(() -> {
//                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        Toast.makeText(LoginActivity.this, "Welcome, " + user.getUsername() + "!", Toast.LENGTH_SHORT).show();
                    });

                    // Store the logged-in user ID
                    getSharedPreferences("VacationApp", MODE_PRIVATE)
                            .edit()
                            .putInt("loggedInUserId", user.getId())
                            .apply();

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
//                    Toast.makeText(LoginActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });

        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }
    private void saveUserName(String userName) {
        SharedPreferences sharedPreferences = getSharedPreferences("VacationApp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("loggedInUserName", userName);
        editor.apply();
    }
}
