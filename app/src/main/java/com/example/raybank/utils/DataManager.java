package com.example.raybank.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.raybank.model.Transaction;
import com.example.raybank.model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * DataManager utility class for handling JSON-based data storage using SharedPreferences.
 */
public class DataManager {
    private static final String PREFS_NAME = "BankAppPrefs";
    private static final String KEY_USERS = "users";
    private static final String KEY_CURRENT_USER = "current_user";
    private static final String KEY_ONBOARDING_COMPLETE = "onboarding_complete";

    private SharedPreferences sharedPreferences;
    private Context context;

    public DataManager(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Save a user to the JSON list.
     */
    public boolean saveUser(User user) {
        try {
            JSONArray usersArray = getUsersArray();
            
            // Check if user already exists
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject userObj = usersArray.getJSONObject(i);
                if (userObj.getString("email").equals(user.getEmail())) {
                    // Update existing user
                    usersArray.put(i, userToJson(user));
                    sharedPreferences.edit().putString(KEY_USERS, usersArray.toString()).apply();
                    return true;
                }
            }
            
            // Add new user
            usersArray.put(userToJson(user));
            sharedPreferences.edit().putString(KEY_USERS, usersArray.toString()).apply();
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get user by email.
     */
    public User getUserByEmail(String email) {
        try {
            JSONArray usersArray = getUsersArray();
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject userObj = usersArray.getJSONObject(i);
                if (userObj.getString("email").equals(email)) {
                    return jsonToUser(userObj);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Authenticate user with email and password.
     */
    public User authenticateUser(String email, String password) {
        User user = getUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    /**
     * Save current logged-in user.
     */
    public void setCurrentUser(User user) {
        try {
            JSONObject userJson = userToJson(user);
            sharedPreferences.edit().putString(KEY_CURRENT_USER, userJson.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get current logged-in user.
     */
    public User getCurrentUser() {
        try {
            String userJsonStr = sharedPreferences.getString(KEY_CURRENT_USER, null);
            if (userJsonStr != null) {
                JSONObject userJson = new JSONObject(userJsonStr);
                return jsonToUser(userJson);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Clear current user (logout).
     */
    public void clearCurrentUser() {
        sharedPreferences.edit().remove(KEY_CURRENT_USER).apply();
    }

    /**
     * Check if onboarding is complete.
     */
    public boolean isOnboardingComplete() {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETE, false);
    }

    /**
     * Set onboarding as complete.
     */
    public void setOnboardingComplete(boolean complete) {
        sharedPreferences.edit().putBoolean(KEY_ONBOARDING_COMPLETE, complete).apply();
    }

    /**
     * Get all users as JSONArray.
     */
    private JSONArray getUsersArray() throws JSONException {
        String usersStr = sharedPreferences.getString(KEY_USERS, "[]");
        return new JSONArray(usersStr);
    }

    /**
     * Convert User object to JSONObject.
     */
    private JSONObject userToJson(User user) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("userId", user.getUserId());
        json.put("email", user.getEmail());
        json.put("password", user.getPassword());
        json.put("fullName", user.getFullName());
        json.put("phoneNumber", user.getPhoneNumber());
        json.put("accountNumber", user.getAccountNumber());
        json.put("balance", user.getBalance());
        json.put("language", user.getLanguage());
        json.put("biometricEnabled", user.isBiometricEnabled());
        
        // Convert transactions
        JSONArray transactionsArray = new JSONArray();
        for (Transaction transaction : user.getTransactions()) {
            transactionsArray.put(transactionToJson(transaction));
        }
        json.put("transactions", transactionsArray);
        
        return json;
    }

    /**
     * Convert JSONObject to User object.
     */
    private User jsonToUser(JSONObject json) throws JSONException {
        User user = new User();
        user.setUserId(json.getString("userId"));
        user.setEmail(json.getString("email"));
        user.setPassword(json.getString("password"));
        user.setFullName(json.getString("fullName"));
        user.setPhoneNumber(json.getString("phoneNumber"));
        user.setAccountNumber(json.getString("accountNumber"));
        user.setBalance(json.getDouble("balance"));
        user.setLanguage(json.optString("language", "en"));
        user.setBiometricEnabled(json.optBoolean("biometricEnabled", false));
        
        // Convert transactions
        JSONArray transactionsArray = json.optJSONArray("transactions");
        if (transactionsArray != null) {
            List<Transaction> transactions = new ArrayList<>();
            for (int i = 0; i < transactionsArray.length(); i++) {
                transactions.add(jsonToTransaction(transactionsArray.getJSONObject(i)));
            }
            user.setTransactions(transactions);
        }
        
        return user;
    }

    /**
     * Convert Transaction object to JSONObject.
     */
    private JSONObject transactionToJson(Transaction transaction) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("transactionId", transaction.getTransactionId());
        json.put("type", transaction.getType());
        json.put("amount", transaction.getAmount());
        json.put("balanceAfter", transaction.getBalanceAfter());
        json.put("date", transaction.getDate());
        json.put("description", transaction.getDescription());
        return json;
    }

    /**
     * Convert JSONObject to Transaction object.
     */
    private Transaction jsonToTransaction(JSONObject json) throws JSONException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(json.getString("transactionId"));
        transaction.setType(json.getString("type"));
        transaction.setAmount(json.getDouble("amount"));
        transaction.setBalanceAfter(json.getDouble("balanceAfter"));
        transaction.setDate(json.getString("date"));
        transaction.setDescription(json.optString("description", ""));
        return transaction;
    }
}
