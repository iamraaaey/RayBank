package com.example.raybank;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.raybank.model.User;
import com.example.raybank.utils.DataManager;

/**
 * Sign up activity for new user registration.
 */
public class SignUpActivity extends AppCompatActivity {
    private EditText editTextFullName;
    private EditText editTextEmail;
    private EditText editTextPhone;
    private EditText editTextInitialBalance;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private Button buttonSignUp;
    private TextView textViewSignIn;
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        dataManager = new DataManager(this);

        editTextFullName = findViewById(R.id.editTextFullName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextInitialBalance = findViewById(R.id.editTextInitialBalance);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        textViewSignIn = findViewById(R.id.textViewSignIn);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Attempts to sign up a new user.
     * Validates all inputs including initial balance, checks for duplicate email,
     * and persists the new user if valid.
     */
    private void signUp() {
        String fullName = editTextFullName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String initialBalanceStr = editTextInitialBalance.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(fullName)) {
            showError(getString(R.string.error_name_empty));
            return;
        }

        if (TextUtils.isEmpty(email)) {
            showError(getString(R.string.error_email_empty));
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError(getString(R.string.error_invalid_email));
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            showError(getString(R.string.error_phone_empty));
            return;
        }

        double initialBalance = 0.0;
        if (TextUtils.isEmpty(initialBalanceStr)) {
            showError(getString(R.string.error_empty_initial_balance));
            return;
        }

        try {
            initialBalance = Double.parseDouble(initialBalanceStr);
            if (initialBalance < 0) {
                showError(getString(R.string.error_negative_initial_balance));
                return;
            }
        } catch (NumberFormatException e) {
            showError(getString(R.string.error_invalid_number));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            showError(getString(R.string.error_password_empty));
            return;
        }

        if (password.length() < 6) {
            showError(getString(R.string.error_password_short));
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError(getString(R.string.error_password_mismatch));
            return;
        }

        // Check if user already exists
        if (dataManager.getUserByEmail(email) != null) {
            showError(getString(R.string.error_email_exists));
            return;
        }

        // Create new user
        String userId = "USER" + System.currentTimeMillis();
        User newUser = new User(userId, email, password, fullName, phone);
        newUser.setBalance(initialBalance);

        if (dataManager.saveUser(newUser)) {
            dataManager.setCurrentUser(newUser);
            Toast.makeText(this, getString(R.string.account_created_success), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            showError(getString(R.string.error_signup_failed));
        }
    }

    /**
     * Shows a toast error message.
     * 
     * @param message The error message to display
     */
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
