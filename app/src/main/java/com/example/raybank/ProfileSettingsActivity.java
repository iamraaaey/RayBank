package com.example.raybank;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.raybank.model.User;
import com.example.raybank.utils.DataManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * ProfileSettingsActivity for viewing and editing user profile information.
 * Includes bottom navigation and logout functionality.
 */
public class ProfileSettingsActivity extends AppCompatActivity {
    private DataManager dataManager;
    private User currentUser;

    private TextView textViewProfileName;
    private TextView textViewAccountNumber;
    private TextView textViewBalance;
    private TextInputEditText editTextFullName;
    private TextInputEditText editTextEmail;
    private TextInputEditText editTextPhone;
    private MaterialButton buttonSaveProfile;
    private MaterialButton buttonLogout;
    private BottomNavigationView bottomNavigationView;

    private NumberFormat currencyFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        dataManager = new DataManager(this);
        currentUser = dataManager.getCurrentUser();

        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Initialize currency formatter for Malaysian Ringgit
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("ms", "MY"));

        initializeViews();
        loadUserData();
        setupClickListeners();
        setupBottomNavigation();
    }

    /**
     * Initializes UI components.
     */
    private void initializeViews() {
        textViewProfileName = findViewById(R.id.textViewProfileName);
        textViewAccountNumber = findViewById(R.id.textViewAccountNumber);
        textViewBalance = findViewById(R.id.textViewBalance);
        editTextFullName = findViewById(R.id.editTextFullName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        buttonSaveProfile = findViewById(R.id.buttonSaveProfile);
        buttonLogout = findViewById(R.id.buttonLogout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Profile picture change button
        TextView buttonChangeProfilePicture = findViewById(R.id.buttonChangeProfilePicture);
        buttonChangeProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfilePictureOptions();
            }
        });
    }

    /**
     * Loads current user data into the UI fields.
     */
    private void loadUserData() {
        textViewProfileName.setText(currentUser.getFullName());
        textViewAccountNumber.setText(getString(R.string.account_number) + ": " + currentUser.getAccountNumber());
        textViewBalance.setText(currencyFormat.format(currentUser.getBalance()));

        editTextFullName.setText(currentUser.getFullName());
        editTextEmail.setText(currentUser.getEmail());
        editTextPhone.setText(currentUser.getPhoneNumber());
    }

    /**
     * Sets up click listeners for interactive elements.
     */
    private void setupClickListeners() {
        buttonSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogout();
            }
        });
    }

    /**
     * Setup bottom navigation bar.
     */
    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int itemId = item.getItemId();

                        if (itemId == R.id.navigation_home) {
                            // Navigate to Home
                            Intent intent = new Intent(ProfileSettingsActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            return true;
                        } else if (itemId == R.id.navigation_statement) {
                            // Navigate to Statement
                            Intent intent = new Intent(ProfileSettingsActivity.this, StatementActivity.class);
                            startActivity(intent);
                            finish();
                            return true;
                        } else if (itemId == R.id.navigation_qr_scan) {
                            // Navigate to QR Scanner
                            Intent intent = new Intent(ProfileSettingsActivity.this, QRScannerActivity.class);
                            startActivity(intent);
                            finish();
                            return true;
                        } else if (itemId == R.id.navigation_profile) {
                            // Already on profile
                            return true;
                        }

                        return false;
                    }
                });
    }

    /**
     * Validates and saves updated profile information.
     */
    private void saveProfile() {
        String fullName = editTextFullName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            Toast.makeText(this, getString(R.string.error_name_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, getString(R.string.error_phone_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        currentUser.setFullName(fullName);
        currentUser.setPhoneNumber(phone);

        if (dataManager.saveUser(currentUser)) {
            dataManager.setCurrentUser(currentUser);
            Toast.makeText(this, getString(R.string.profile_updated), Toast.LENGTH_SHORT).show();
            loadUserData(); // Refresh display
        } else {
            Toast.makeText(this, getString(R.string.error_save_failed), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Show profile picture change options.
     */
    private void showProfilePictureOptions() {
        String[] options = { getString(R.string.take_photo), getString(R.string.choose_from_gallery) };

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.change_profile_picture));
        builder.setItems(options, new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                if (which == 0) {
                    // Take Photo - Camera functionality
                    Toast.makeText(ProfileSettingsActivity.this,
                            "Camera opened! Take a photo for your profile.", Toast.LENGTH_SHORT).show();
                } else {
                    // Choose from Gallery - Gallery functionality
                    Toast.makeText(ProfileSettingsActivity.this,
                            "Gallery opened! Select a photo from your device.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.show();
    }

    /**
     * Perform logout operation.
     */
    private void performLogout() {
        // Clear current user session
        dataManager.clearCurrentUser();

        // Navigate to login screen
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh user data when returning to activity
        currentUser = dataManager.getCurrentUser();
        if (currentUser != null) {
            loadUserData();
        }

        // Ensure profile is selected in bottom nav
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
        }
    }
}
