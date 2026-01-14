package com.example.raybank;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.raybank.model.Transaction;
import com.example.raybank.model.User;
import com.example.raybank.utils.DataManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * StatementActivity displays transaction history for the current user.
 * Shows all deposits and withdrawals with dates and amounts in RM.
 */
public class StatementActivity extends AppCompatActivity {

    private RecyclerView recyclerViewTransactions;
    private LinearLayout emptyStateLayout;
    private TextView textViewAccountInfo;
    private BottomNavigationView bottomNavigationView;

    private DataManager dataManager;
    private User currentUser;
    private TransactionAdapter adapter;
    private NumberFormat currencyFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement);

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
        setupBottomNavigation();
        loadTransactions();
    }

    /**
     * Initialize UI components.
     */
    /**
     * Initialize UI components.
     */
    private void initializeViews() {
        recyclerViewTransactions = findViewById(R.id.recyclerViewTransactions);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        textViewAccountInfo = findViewById(R.id.textViewAccountInfo);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Set account info
        textViewAccountInfo.setText("Account: " + currentUser.getAccountNumber());

        // Setup RecyclerView
        recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Setup bottom navigation bar.
     */
    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.navigation_statement);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int itemId = item.getItemId();

                        if (itemId == R.id.navigation_home) {
                            // Navigate to Home
                            Intent intent = new Intent(StatementActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            return true;
                        } else if (itemId == R.id.navigation_statement) {
                            // Already on statement
                            return true;
                        } else if (itemId == R.id.navigation_qr_scan) {
                            // Navigate to QR Scanner
                            Intent intent = new Intent(StatementActivity.this, QRScannerActivity.class);
                            startActivity(intent);
                            finish();
                            return true;
                        } else if (itemId == R.id.navigation_profile) {
                            // Navigate to Profile
                            Intent intent = new Intent(StatementActivity.this, ProfileSettingsActivity.class);
                            startActivity(intent);
                            finish();
                            return true;
                        }

                        return false;
                    }
                });
    }

    /**
     * Load and display transactions.
     */
    /**
     * Load and display transactions.
     * Fetches transactions from the current user object and populates the
     * RecyclerView.
     */
    private void loadTransactions() {
        List<Transaction> transactions = currentUser.getTransactions();

        if (transactions == null || transactions.isEmpty()) {
            // Show empty state
            recyclerViewTransactions.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            // Show transactions (reverse order - newest first)
            List<Transaction> reversedTransactions = new ArrayList<>(transactions);
            Collections.reverse(reversedTransactions);

            adapter = new TransactionAdapter(reversedTransactions, currencyFormat);
            recyclerViewTransactions.setAdapter(adapter);

            recyclerViewTransactions.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to activity
        currentUser = dataManager.getCurrentUser();
        if (currentUser != null) {
            loadTransactions();
        }

        // Ensure statement is selected in bottom nav
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_statement);
        }
    }

    /**
     * RecyclerView Adapter for displaying transactions.
     */
    private static class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

        private List<Transaction> transactions;
        private NumberFormat currencyFormat;

        public TransactionAdapter(List<Transaction> transactions, NumberFormat currencyFormat) {
            this.transactions = transactions;
            this.currencyFormat = currencyFormat;
        }

        @NonNull
        @Override
        public TransactionViewHolder onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_transaction, parent, false);
            return new TransactionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
            Transaction transaction = transactions.get(position);

            // Set transaction type
            String type = transaction.getType();
            if (type.equals("DEPOSIT")) {
                holder.textViewType.setText("Deposit / Masuk");
                holder.textViewAmount
                        .setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.success_green));
                holder.textViewAmount.setText("+" + currencyFormat.format(transaction.getAmount()));
            } else if (type.equals("WITHDRAW")) {
                holder.textViewType.setText("Withdraw / Keluar");
                holder.textViewAmount
                        .setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.error_red));
                holder.textViewAmount.setText("-" + currencyFormat.format(transaction.getAmount()));
            }

            // Set date
            holder.textViewDate.setText(transaction.getDate());

            // Set balance after transaction
            holder.textViewBalance.setText("Balance: " + currencyFormat.format(transaction.getBalanceAfter()));
        }

        @Override
        public int getItemCount() {
            return transactions.size();
        }

        static class TransactionViewHolder extends RecyclerView.ViewHolder {
            TextView textViewType;
            TextView textViewAmount;
            TextView textViewDate;
            TextView textViewBalance;

            public TransactionViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewType = itemView.findViewById(R.id.textViewType);
                textViewAmount = itemView.findViewById(R.id.textViewAmount);
                textViewDate = itemView.findViewById(R.id.textViewDate);
                textViewBalance = itemView.findViewById(R.id.textViewBalance);
            }
        }
    }
}
