package com.example.raybank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.example.raybank.model.User;
import com.example.raybank.utils.DataManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * LoginActivity handles user authentication using hardcoded demo users.
 * Provides a modern, Material Design login interface for MyBank MY.
 */
public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editTextUsername;
    private TextInputEditText editTextPassword;
    private MaterialButton buttonLogin;

    private AuthManager authManager;
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize managers
        authManager = AuthManager.getInstance();
        dataManager = new DataManager(this);

        // Check if user is already logged in
        User currentUser = dataManager.getCurrentUser();
        if (currentUser != null) {
            navigateToMain();
            return;
        }

        // Initialize views
        initializeViews();
        setupListeners();
        setupBackPressHandler();
    }

    /**
     * Initialize UI components.
     */
    private void initializeViews() {
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
    }

    /**
     * Setup button click listeners.
     */
    private void setupListeners() {
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });
    }

    /**
     * Handle login button click.
     * Validates input and authenticates user against hardcoded data.
     */
    private void handleLogin() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString();

        // Validate input
        if (username.isEmpty()) {
            Toast.makeText(this, R.string.error_email_empty, Toast.LENGTH_SHORT).show();
            editTextUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, R.string.error_password_empty, Toast.LENGTH_SHORT).show();
            editTextPassword.requestFocus();
            return;
        }

        // Authenticate user
        User user = authManager.authenticate(username, password);

        if (user != null) {
            // Authentication successful
            // Save user to DataManager for session persistence
            dataManager.setCurrentUser(user);
            dataManager.saveUser(user);

            // Show success message
            String welcomeMessage = getString(R.string.welcome_to_mybank) + ", " + user.getFullName();
            Toast.makeText(this, welcomeMessage, Toast.LENGTH_SHORT).show();

            // Navigate to main activity
            navigateToMain();
        } else {
            // Authentication failed
            Toast.makeText(this, R.string.error_invalid_credentials, Toast.LENGTH_LONG).show();
            editTextPassword.setText("");
            editTextPassword.requestFocus();
        }
    }

    /**
     * Navigate to MainActivity.
     */
    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Setup back press handler to disable back button on login screen.
     */
    private void setupBackPressHandler() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // User must login to proceed
                finishAffinity();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}
