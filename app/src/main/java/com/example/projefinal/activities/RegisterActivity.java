package com.example.projefinal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projefinal.R;
import com.example.projefinal.database.DatabaseHelper;
import com.example.projefinal.models.User;
import com.example.projefinal.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText phoneInput;
    private TextInputEditText passwordInput;
    private TextInputEditText confirmPasswordInput;
    private Button registerButton;
    private TextView loginLink;
    
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize helpers
        databaseHelper = new DatabaseHelper(this);
        sessionManager = SessionManager.getInstance(this);

        // Initialize views
        phoneInput = findViewById(R.id.phoneInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        registerButton = findViewById(R.id.registerButton);
        loginLink = findViewById(R.id.loginLink);

        // Setup click listeners
        registerButton.setOnClickListener(v -> attemptRegistration());
        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void attemptRegistration() {
        // Reset errors
        phoneInput.setError(null);
        passwordInput.setError(null);
        confirmPasswordInput.setError(null);

        // Get values
        String phone = phoneInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(phone)) {
            phoneInput.setError("Phone number is required");
            phoneInput.requestFocus();
            return;
        }

        if (phone.length() < 10) {
            phoneInput.setError("Please enter a valid phone number");
            phoneInput.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            passwordInput.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            passwordInput.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            confirmPasswordInput.requestFocus();
            return;
        }

        // Check if phone number is already registered
        if (databaseHelper.getUser(phone) != null) {
            phoneInput.setError("This phone number is already registered");
            phoneInput.requestFocus();
            return;
        }

        // Create new user
        User newUser = new User(phone, password, false); // Regular user, not admin
        long userId = databaseHelper.createUser(newUser);

        if (userId != -1) {
            // Registration successful
            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
            
            // Auto login after registration
            sessionManager.createLoginSession((int) userId, phone, false);
            
            // Start main activity
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            // Registration failed
            Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
