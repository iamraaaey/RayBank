package com.example.raybank;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.raybank.model.User;
import com.example.raybank.utils.DataManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * QRScannerActivity - Scan QR codes or manually enter account numbers for
 * transfers.
 */
public class QRScannerActivity extends AppCompatActivity {
    private TextInputEditText editTextAccountNumber;
    private MaterialButton buttonProceed;
    private BottomNavigationView bottomNavigationView;
    private DataManager dataManager;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        dataManager = new DataManager(this);
        currentUser = dataManager.getCurrentUser();

        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        initializeViews();
        setupClickListeners();
        setupBottomNavigation();
    }

    /**
     * Initializes UI components.
     */
    private void initializeViews() {
        editTextAccountNumber = findViewById(R.id.editTextAccountNumber);
        buttonProceed = findViewById(R.id.buttonProceed);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    /**
     * Sets up click listeners for buttons.
     */
    private void setupClickListeners() {
        buttonProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedToTransfer();
            }
        });
    }

    /**
     * Validates input and proceeds to transfer confirmation.
     * Redirects to MainActivity with pre-filled account details.
     */
    private void proceedToTransfer() {
        String accountNumber = editTextAccountNumber.getText().toString().trim();

        if (TextUtils.isEmpty(accountNumber)) {
            Toast.makeText(this, "Please enter an account number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (accountNumber.length() != 10) {
            Toast.makeText(this, "Account number must be 10 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        if (accountNumber.equals(currentUser.getAccountNumber())) {
            Toast.makeText(this, getString(R.string.error_same_account), Toast.LENGTH_SHORT).show();
            return;
        }

        // Go back to MainActivity and trigger transfer dialog with this account number
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("TRANSFER_ACCOUNT", accountNumber);
        intent.putExtra("FROM_QR_SCAN", true);
        startActivity(intent);
        finish();
    }

    /**
     * Sets up the bottom navigation view.
     */
    private void setupBottomNavigation() {
        // Select QR scan item
        bottomNavigationView.setSelectedItemId(R.id.navigation_qr_scan);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int itemId = item.getItemId();

                        if (itemId == R.id.navigation_home) {
                            Intent intent = new Intent(QRScannerActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            return true;
                        } else if (itemId == R.id.navigation_statement) {
                            Intent intent = new Intent(QRScannerActivity.this, StatementActivity.class);
                            startActivity(intent);
                            finish();
                            return true;
                        } else if (itemId == R.id.navigation_qr_scan) {
                            // Already on QR scanner
                            return true;
                        } else if (itemId == R.id.navigation_profile) {
                            Intent intent = new Intent(QRScannerActivity.this, ProfileSettingsActivity.class);
                            startActivity(intent);
                            finish();
                            return true;
                        }

                        return false;
                    }
                });
    }
}
