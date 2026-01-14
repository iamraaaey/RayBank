package com.example.raybank.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Transaction model class representing a bank transaction.
 */
public class Transaction {
    private String transactionId;
    private String type; // "DEPOSIT" or "WITHDRAW"
    private double amount;
    private double balanceAfter;
    private String date;
    private String description;

    /**
     * Default constructor.
     */
    public Transaction() {
        this.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    /**
     * Constructor to create a new transaction.
     * 
     * @param type         Transaction type ("DEPOSIT", "WITHDRAW", "TRANSFER")
     * @param amount       Transaction amount
     * @param balanceAfter Balance after transaction
     * @param description  Description of the transaction
     */
    public Transaction(String type, double amount, double balanceAfter, String description) {
        this.transactionId = "TXN" + System.currentTimeMillis();
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        this.description = description;
    }

    // Getters and Setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(double balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
