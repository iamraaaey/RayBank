package com.example.raybank;

import com.example.raybank.model.Transaction;
import java.util.ArrayList;
import java.util.List;

/**
 * BankAccount class represents a simple bank account with deposit and withdrawal functionality.
 * Enhanced with transaction history tracking.
 */
public class BankAccount {
    private double balance;
    private List<Transaction> transactionHistory;

    /**
     * Constructor to initialize the bank account with an initial balance.
     * @param initialBalance The initial balance to set for the account
     */
    public BankAccount(double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
    }

    /**
     * Deposits money into the account.
     * @param amount The amount to deposit (must be positive)
     * @return true if deposit was successful, false otherwise
     */
    public boolean deposit(double amount) {
        if (amount <= 0) {
            return false; // Invalid deposit amount
        }
        balance += amount;
        Transaction transaction = new Transaction("DEPOSIT", amount, balance, "Deposit");
        addTransaction(transaction);
        return true;
    }

    /**
     * Withdraws money from the account.
     * @param amount The amount to withdraw (must be positive and not exceed balance)
     * @return true if withdrawal was successful, false otherwise
     */
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            return false; // Invalid withdrawal amount
        }
        if (amount > balance) {
            return false; // Insufficient funds
        }
        balance -= amount;
        Transaction transaction = new Transaction("WITHDRAW", amount, balance, "Withdrawal");
        addTransaction(transaction);
        return true;
    }

    /**
     * Gets the current balance of the account.
     * @return The current balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Gets the transaction history.
     * @return List of all transactions
     */
    public List<Transaction> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }

    /**
     * Adds a transaction to the history.
     * @param transaction The transaction to add
     */
    public void addTransaction(Transaction transaction) {
        transactionHistory.add(transaction);
    }
}
