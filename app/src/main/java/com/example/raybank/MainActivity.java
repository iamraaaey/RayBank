package com.example.raybank;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.raybank.model.Transaction;
import com.example.raybank.model.User;
import com.example.raybank.utils.DataManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Locale;
import java.text.NumberFormat;

/**
 * MainActivity - Modern Dashboard for RayBank banking application.
 * Features: Balance display, deposit/withdraw/transfer functionality, QR
 * scanning, bottom navigation.
 */
public class MainActivity extends AppCompatActivity {
    private BankAccount bankAccount;
    private User currentUser;
    private DataManager dataManager;

    // UI Components
    private TextView textViewWelcome;
    private TextView textViewUserName;
    private CardView cardViewBalance;
    private TextView textViewBalance;
    private TextView textViewAccountNumber;
    private ImageView buttonCopyAccount;
    private CardView cardDeposit;
    private CardView cardWithdraw;
    private CardView cardTransfer;
    private CardView cardScanQR;
    private BottomNavigationView bottomNavigationView;

    private boolean isAccountInitialized = false;
    private NumberFormat currencyFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize DataManager and get current user
        dataManager = new DataManager(this);
        currentUser = dataManager.getCurrentUser();

        // If no user is logged in, redirect to login
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
        setupAccount();
        setupClickListeners();
        setupBottomNavigation();
        updateUI();

        // Check if coming from QR scanner with account number
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("FROM_QR_SCAN", false)) {
            String transferAccount = intent.getStringExtra("TRANSFER_ACCOUNT");
            if (transferAccount != null && !transferAccount.isEmpty()) {
                // Auto-open transfer dialog with pre-filled account
                showTransferDialogWithAccount(transferAccount);
            }
        }
    }

    /**
     * Initialize all UI components.
     */
    private void initializeViews() {
        textViewWelcome = findViewById(R.id.textViewWelcome);
        textViewUserName = findViewById(R.id.textViewUserName);
        cardViewBalance = findViewById(R.id.cardViewBalance);
        textViewBalance = findViewById(R.id.textViewBalance);
        textViewAccountNumber = findViewById(R.id.textViewAccountNumber);
        buttonCopyAccount = findViewById(R.id.buttonCopyAccount);
        cardDeposit = findViewById(R.id.cardDeposit);
        cardWithdraw = findViewById(R.id.cardWithdraw);
        cardTransfer = findViewById(R.id.cardTransfer);
        cardScanQR = findViewById(R.id.cardScanQR);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    /**
     * Setup bank account from user data.
     */
    private void setupAccount() {
        // Initialize bank account with user's current balance
        bankAccount = new BankAccount(currentUser.getBalance());
        isAccountInitialized = true;
    }

    /**
     * Setup click listeners for buttons.
     */
    private void setupClickListeners() {
        // Copy account number
        buttonCopyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyAccountNumber();
            }
        });

        // Deposit
        cardDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDepositDialog();
            }
        });

        // Withdraw
        cardWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWithdrawDialog();
            }
        });

        // Transfer
        cardTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTransferDialog();
            }
        });

        // Scan QR
        cardScanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQRScanInfo();
            }
        });
    }

    /**
     * Copy account number to clipboard.
     */
    private void copyAccountNumber() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Account Number", currentUser.getAccountNumber());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, getString(R.string.account_copied), Toast.LENGTH_SHORT).show();
    }

    /**
     * Show deposit dialog.
     */
    /**
     * Show deposit dialog.
     */
    private void showDepositDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_deposit, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        TextInputEditText editTextAmount = dialogView.findViewById(R.id.editTextDepositAmount);
        com.google.android.material.chip.ChipGroup chipGroup = dialogView.findViewById(R.id.chipGroupDeposit); // Ensure
                                                                                                               // ID
                                                                                                               // exists
                                                                                                               // in XML
        MaterialButton buttonConfirm = dialogView.findViewById(R.id.buttonConfirmDeposit);
        MaterialButton buttonCancel = dialogView.findViewById(R.id.buttonCancelDeposit);

        // Setup ChipGroup listener
        if (chipGroup != null) {
            chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId != -1) {
                    com.google.android.material.chip.Chip chip = group.findViewById(checkedId);
                    if (chip != null) {
                        String text = chip.getText().toString().replace("RM", "").trim();
                        editTextAmount.setText(text);
                        editTextAmount.setSelection(text.length());
                    }
                }
            });
        }

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountStr = editTextAmount.getText().toString().trim();
                if (performDeposit(amountStr)) {
                    dialog.dismiss();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * Show withdraw dialog.
     */
    private void showWithdrawDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_withdraw, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        TextInputEditText editTextAmount = dialogView.findViewById(R.id.editTextWithdrawAmount);
        com.google.android.material.chip.ChipGroup chipGroup = dialogView.findViewById(R.id.chipGroupWithdraw); // Need
                                                                                                                // to
                                                                                                                // ensure
                                                                                                                // ID in
                                                                                                                // XML
        MaterialButton buttonConfirm = dialogView.findViewById(R.id.buttonConfirmWithdraw);
        MaterialButton buttonCancel = dialogView.findViewById(R.id.buttonCancelWithdraw);

        // Setup ChipGroup listener
        if (chipGroup != null) {
            chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId != -1) {
                    com.google.android.material.chip.Chip chip = group.findViewById(checkedId);
                    if (chip != null) {
                        String text = chip.getText().toString().replace("RM", "").trim();
                        editTextAmount.setText(text);
                        editTextAmount.setSelection(text.length());
                    }
                }
            });
        }

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountStr = editTextAmount.getText().toString().trim();
                if (performWithdrawal(amountStr)) {
                    dialog.dismiss();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * Show transfer dialog.
     */
    private void showTransferDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_transfer, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        TextInputEditText editTextRecipient = dialogView.findViewById(R.id.editTextRecipientAccount);
        TextInputEditText editTextAmount = dialogView.findViewById(R.id.editTextTransferAmount);
        com.google.android.material.chip.ChipGroup chipGroup = dialogView.findViewById(R.id.chipGroupTransfer);
        MaterialButton buttonConfirm = dialogView.findViewById(R.id.buttonConfirmTransfer);
        MaterialButton buttonCancel = dialogView.findViewById(R.id.buttonCancelTransfer);

        // Setup ChipGroup listener
        if (chipGroup != null) {
            chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId != -1) {
                    com.google.android.material.chip.Chip chip = group.findViewById(checkedId);
                    if (chip != null) {
                        String text = chip.getText().toString().replace("RM", "").trim();
                        editTextAmount.setText(text);
                        editTextAmount.setSelection(text.length());
                    }
                }
            });
        }

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipientAccount = editTextRecipient.getText().toString().trim();
                String amountStr = editTextAmount.getText().toString().trim();
                if (performTransfer(recipientAccount, amountStr)) {
                    dialog.dismiss();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * Show transfer dialog with pre-filled account number (from QR scan).
     */
    private void showTransferDialogWithAccount(String accountNumber) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_transfer, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        TextInputEditText editTextRecipient = dialogView.findViewById(R.id.editTextRecipientAccount);
        TextInputEditText editTextAmount = dialogView.findViewById(R.id.editTextTransferAmount);
        com.google.android.material.chip.ChipGroup chipGroup = dialogView.findViewById(R.id.chipGroupTransfer);
        MaterialButton buttonConfirm = dialogView.findViewById(R.id.buttonConfirmTransfer);
        MaterialButton buttonCancel = dialogView.findViewById(R.id.buttonCancelTransfer);

        // Pre-fill account number from QR scan
        editTextRecipient.setText(accountNumber);

        // Setup ChipGroup listener
        if (chipGroup != null) {
            chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId != -1) {
                    com.google.android.material.chip.Chip chip = group.findViewById(checkedId);
                    if (chip != null) {
                        String text = chip.getText().toString().replace("RM", "").trim();
                        editTextAmount.setText(text);
                        editTextAmount.setSelection(text.length());
                    }
                }
            });
        }

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipientAccount = editTextRecipient.getText().toString().trim();
                String amountStr = editTextAmount.getText().toString().trim();
                if (performTransfer(recipientAccount, amountStr)) {
                    dialog.dismiss();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * Launch QR scanner activity.
     */
    private void showQRScanInfo() {
        Intent intent = new Intent(this, QRScannerActivity.class);
        startActivity(intent);
    }

    /**
     * Setup bottom navigation bar.
     */
    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int itemId = item.getItemId();

                        if (itemId == R.id.navigation_home) {
                            // Already on home
                            return true;
                        } else if (itemId == R.id.navigation_statement) {
                            // Navigate to Statement
                            Intent intent = new Intent(MainActivity.this, StatementActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (itemId == R.id.navigation_qr_scan) {
                            // Navigate to QR Scanner
                            Intent intent = new Intent(MainActivity.this, QRScannerActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (itemId == R.id.navigation_profile) {
                            // Navigate to Profile
                            Intent intent = new Intent(MainActivity.this, ProfileSettingsActivity.class);
                            startActivity(intent);
                            return true;
                        }

                        return false;
                    }
                });
    }

    /**
     * Perform deposit transaction.
     * Validates input amount, interacts with BankAccount model, updates User
     * balance,
     * creates a transaction record, saves data, and refreshes the UI.
     * 
     * @param amountStr The deposit amount as a string
     * @return true if successful, false otherwise
     */
    private boolean performDeposit(String amountStr) {
        if (!isAccountInitialized) {
            showError(getString(R.string.error_account_not_initialized));
            return false;
        }

        if (TextUtils.isEmpty(amountStr)) {
            showError(getString(R.string.error_amount_empty));
            return false;
        }

        try {
            double amount = Double.parseDouble(amountStr);

            if (amount <= 0) {
                showError(getString(R.string.error_negative_amount));
                return false;
            }

            if (bankAccount.deposit(amount)) {
                double newBalance = bankAccount.getBalance();
                currentUser.setBalance(newBalance);

                // Create transaction record
                Transaction transaction = new Transaction(
                        "DEPOSIT",
                        amount,
                        newBalance,
                        getString(R.string.transaction_deposit));
                currentUser.addTransaction(transaction);

                // Save user data
                dataManager.saveUser(currentUser);
                dataManager.setCurrentUser(currentUser);

                // Update UI
                updateBalanceDisplay();
                showSuccess(getString(R.string.deposit_success) + " " + formatCurrency(amount));
                return true;
            } else {
                showError(getString(R.string.error_invalid_deposit));
                return false;
            }
        } catch (NumberFormatException e) {
            showError(getString(R.string.error_invalid_number));
            return false;
        }
    }

    /**
     * Perform withdrawal transaction.
     * Validates input amount and sufficient funds, interacts with BankAccount
     * model,
     * updates User balance, creates a transaction record, saves data, and refreshes
     * the UI.
     * 
     * @param amountStr The withdrawal amount as a string
     * @return true if successful, false otherwise
     */
    private boolean performWithdrawal(String amountStr) {
        if (!isAccountInitialized) {
            showError(getString(R.string.error_account_not_initialized));
            return false;
        }

        if (TextUtils.isEmpty(amountStr)) {
            showError(getString(R.string.error_amount_empty));
            return false;
        }

        try {
            double amount = Double.parseDouble(amountStr);

            if (amount <= 0) {
                showError(getString(R.string.error_negative_amount));
                return false;
            }

            if (bankAccount.withdraw(amount)) {
                double newBalance = bankAccount.getBalance();
                currentUser.setBalance(newBalance);

                // Create transaction record
                Transaction transaction = new Transaction(
                        "WITHDRAW",
                        amount,
                        newBalance,
                        getString(R.string.transaction_withdraw));
                currentUser.addTransaction(transaction);

                // Save user data
                dataManager.saveUser(currentUser);
                dataManager.setCurrentUser(currentUser);

                // Update UI
                updateBalanceDisplay();
                showSuccess(getString(R.string.withdraw_success) + " " + formatCurrency(amount));
                return true;
            } else {
                if (amount > bankAccount.getBalance()) {
                    String errorMsg = getString(R.string.error_insufficient_funds) + "\n" +
                            getString(R.string.current_balance) + ": " +
                            formatCurrency(bankAccount.getBalance());
                    showError(errorMsg);
                } else {
                    showError(getString(R.string.error_invalid_withdraw));
                }
                return false;
            }
        } catch (NumberFormatException e) {
            showError(getString(R.string.error_invalid_number));
            return false;
        }
    }

    /**
     * Perform transfer transaction.
     * Validates recipient account and amount, ensures sufficient funds,
     * simulates the transfer by withdrawing from the current user,
     * creates a transaction record, saves data, and refreshes the UI.
     * 
     * @param recipientAccount The account number of the recipient
     * @param amountStr        The transfer amount as a string
     * @return true if successful, false otherwise
     */
    private boolean performTransfer(String recipientAccount, String amountStr) {
        if (!isAccountInitialized) {
            showError(getString(R.string.error_account_not_initialized));
            return false;
        }

        if (TextUtils.isEmpty(recipientAccount)) {
            showError(getString(R.string.error_recipient_empty));
            return false;
        }

        if (recipientAccount.equals(currentUser.getAccountNumber())) {
            showError(getString(R.string.error_same_account));
            return false;
        }

        if (TextUtils.isEmpty(amountStr)) {
            showError(getString(R.string.error_amount_empty));
            return false;
        }

        try {
            double amount = Double.parseDouble(amountStr);

            if (amount <= 0) {
                showError(getString(R.string.error_negative_amount));
                return false;
            }

            if (bankAccount.withdraw(amount)) {
                double newBalance = bankAccount.getBalance();
                currentUser.setBalance(newBalance);

                // Create transaction record
                Transaction transaction = new Transaction(
                        "TRANSFER",
                        amount,
                        newBalance,
                        getString(R.string.transaction_transfer_sent) + " to " + recipientAccount);
                currentUser.addTransaction(transaction);

                // Save user data
                dataManager.saveUser(currentUser);
                dataManager.setCurrentUser(currentUser);

                // Update UI
                updateBalanceDisplay();
                showSuccess(getString(R.string.transfer_success) + " " + formatCurrency(amount));
                return true;
            } else {
                if (amount > bankAccount.getBalance()) {
                    String errorMsg = getString(R.string.error_insufficient_funds) + "\n" +
                            getString(R.string.current_balance) + ": " +
                            formatCurrency(bankAccount.getBalance());
                    showError(errorMsg);
                } else {
                    showError(getString(R.string.error_invalid_account));
                }
                return false;
            }
        } catch (NumberFormatException e) {
            showError(getString(R.string.error_invalid_number));
            return false;
        }
    }

    /**
     * Update balance display with current balance.
     */
    private void updateBalanceDisplay() {
        if (bankAccount != null) {
            textViewBalance.setText(formatCurrency(bankAccount.getBalance()));
            textViewAccountNumber.setText(currentUser.getAccountNumber());
        }
    }

    /**
     * Update all UI elements.
     */
    private void updateUI() {
        // Set user name
        textViewUserName.setText(currentUser.getFullName());

        // Update balance display
        updateBalanceDisplay();
    }

    /**
     * Format currency in Malaysian Ringgit (RM).
     */
    private String formatCurrency(double amount) {
        return currencyFormat.format(amount);
    }

    /**
     * Show error message.
     */
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Show success message.
     */
    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh user data when returning to activity
        currentUser = dataManager.getCurrentUser();
        if (currentUser != null) {
            bankAccount = new BankAccount(currentUser.getBalance());
            isAccountInitialized = true;
            updateUI();
        }

        // Ensure home is selected in bottom nav
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }
    }
}
