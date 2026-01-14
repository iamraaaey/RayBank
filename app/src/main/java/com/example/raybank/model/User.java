package com.example.raybank.model;

import java.util.ArrayList;
import java.util.List;

/**
 * User model class representing a bank account user.
 */
public class User {
    private String userId;
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String accountNumber;
    private double balance;
    private List<Transaction> transactions;
    private String language; // "en" or "ms"
    private boolean biometricEnabled;

    /**
     * Default constructor.
     */
    public User() {
        this.transactions = new ArrayList<>();
        this.language = "en";
        this.biometricEnabled = false;
    }

    /**
     * Constructor to create a new user.
     * 
     * @param userId      Unique user ID
     * @param email       User email address
     * @param password    User password
     * @param fullName    User full name
     * @param phoneNumber User phone number
     */
    public User(String userId, String email, String password, String fullName, String phoneNumber) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.accountNumber = generateAccountNumber();
        this.balance = 0.0;
        this.transactions = new ArrayList<>();
        this.language = "en";
        this.biometricEnabled = false;
    }

    /**
     * Generates a random account number.
     * 
     * @return String representation of the account number
     */
    private String generateAccountNumber() {
        // Generate a simple account number
        return "ACC" + System.currentTimeMillis() % 1000000000;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isBiometricEnabled() {
        return biometricEnabled;
    }

    public void setBiometricEnabled(boolean biometricEnabled) {
        this.biometricEnabled = biometricEnabled;
    }
}
