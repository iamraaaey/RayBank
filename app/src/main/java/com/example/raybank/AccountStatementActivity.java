package com.example.raybank;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.raybank.model.Transaction;
import com.example.raybank.model.User;
import com.example.raybank.utils.CurrencyFormatter;
import com.example.raybank.utils.DataManager;
import java.util.Collections;
import java.util.List;

/**
 * Account statement activity showing transaction history.
 */
public class AccountStatementActivity extends AppCompatActivity {
    private DataManager dataManager;
    private User currentUser;
    private RecyclerView recyclerViewTransactions;
    private TextView textViewNoTransactions;
    private TransactionAdapter transactionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_statement);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.account_statement);

        dataManager = new DataManager(this);
        currentUser = dataManager.getCurrentUser();

        if (currentUser == null) {
            finish();
            return;
        }

        initializeViews();
        loadTransactions();
    }

    private void initializeViews() {
        recyclerViewTransactions = findViewById(R.id.recyclerViewTransactions);
        textViewNoTransactions = findViewById(R.id.textViewNoTransactions);

        recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadTransactions() {
        List<Transaction> transactions = currentUser.getTransactions();

        if (transactions == null || transactions.isEmpty()) {
            recyclerViewTransactions.setVisibility(android.view.View.GONE);
            textViewNoTransactions.setVisibility(android.view.View.VISIBLE);
        } else {
            recyclerViewTransactions.setVisibility(android.view.View.VISIBLE);
            textViewNoTransactions.setVisibility(android.view.View.GONE);

            // Reverse to show latest first
            Collections.reverse(transactions);
            transactionAdapter = new TransactionAdapter(transactions);
            recyclerViewTransactions.setAdapter(transactionAdapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Simple adapter for displaying transactions.
     */
    private class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
        private List<Transaction> transactions;

        public TransactionAdapter(List<Transaction> transactions) {
            this.transactions = transactions;
        }

        @Override
        public TransactionViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_transaction, parent, false);
            return new TransactionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TransactionViewHolder holder, int position) {
            Transaction transaction = transactions.get(position);
            holder.bind(transaction);
        }

        @Override
        public int getItemCount() {
            return transactions.size();
        }

        class TransactionViewHolder extends RecyclerView.ViewHolder {
            private TextView textViewType;
            private TextView textViewAmount;
            private TextView textViewDate;
            private TextView textViewBalance;

            public TransactionViewHolder(android.view.View itemView) {
                super(itemView);
                textViewType = itemView.findViewById(R.id.textViewType);
                textViewAmount = itemView.findViewById(R.id.textViewAmount);
                textViewDate = itemView.findViewById(R.id.textViewDate);
                textViewBalance = itemView.findViewById(R.id.textViewBalance);
            }

            public void bind(Transaction transaction) {
                String type = transaction.getType().equals("DEPOSIT") ? getString(R.string.deposit)
                        : getString(R.string.withdraw);
                textViewType.setText(type);

                String amountPrefix = transaction.getType().equals("DEPOSIT") ? "+" : "-";
                textViewAmount.setText(amountPrefix + CurrencyFormatter.formatRM(transaction.getAmount()));
                textViewAmount.setTextColor(transaction.getType().equals("DEPOSIT")
                        ? ContextCompat.getColor(itemView.getContext(), android.R.color.holo_green_dark)
                        : ContextCompat.getColor(itemView.getContext(), android.R.color.holo_red_dark));

                textViewDate.setText(transaction.getDate());
                textViewBalance.setText(getString(R.string.balance) + ": " +
                        CurrencyFormatter.formatRM(transaction.getBalanceAfter()));
            }
        }
    }
}
