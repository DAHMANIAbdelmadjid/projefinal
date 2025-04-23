package com.example.projefinal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projefinal.R;
import com.example.projefinal.database.DatabaseHelper;
import com.example.projefinal.models.User;
import com.example.projefinal.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText phoneInput;
    private TextInputEditText passwordInput;
    private Button loginButton;
    private TextView registerLink;
    
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize helpers
        databaseHelper = new DatabaseHelper(this);
        sessionManager = SessionManager.getInstance(this);

        // Check if already logged in
        if (sessionManager.isLoggedIn()) {
            startMainActivity();
            finish();
            return;
        }

        // Initialize views
        phoneInput = findViewById(R.id.phoneInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerLink = findViewById(R.id.registerLink);

        // Setup click listeners
        loginButton.setOnClickListener(v -> attemptLogin());
        registerLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }

    private void attemptLogin() {
        // Reset errors
        phoneInput.setError(null);
        passwordInput.setError(null);

        // Get values
        String phone = phoneInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(phone)) {
            phoneInput.setError("Phone number is required");
            phoneInput.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            passwordInput.requestFocus();
            return;
        }

        // Attempt authentication
        User user = databaseHelper.getUser(phone);
        if (user != null && password.equals(user.getPassword())) {
            // Login successful
            sessionManager.createLoginSession(user.getId(), user.getPhone(), user.isAdmin());
            startMainActivity();
            finish();
        } else {
            // Login failed
            Toast.makeText(this, "Invalid phone number or password", Toast.LENGTH_SHORT).show();
            passwordInput.setText("");
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
