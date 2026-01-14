package com.example.raybank;

import com.example.raybank.model.Transaction;
import com.example.raybank.model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * AuthManager class for handling authentication with hardcoded Malaysian user
 * data.
 * This provides demo users for testing the banking application.
 */
public class AuthManager {
    private static AuthManager instance;
    private List<User> hardcodedUsers;

    private AuthManager() {
        initializeHardcodedUsers();
    }

    public static synchronized AuthManager getInstance() {
        if (instance == null) {
            instance = new AuthManager();
        }
        return instance;
    }

    /**
     * Initialize hardcoded users with Malaysian names and data.
     */
    private void initializeHardcodedUsers() {
        hardcodedUsers = new ArrayList<>();

        // User 1: Raynold Anak Kabai
        User raynold = new User();
        raynold.setUserId("USER001");
        raynold.setEmail("raynold");
        raynold.setPassword("raynold123");
        raynold.setFullName("Raynold Anak Kabai");
        raynold.setPhoneNumber("+60123456789");
        raynold.setAccountNumber("1234567890");
        raynold.setBalance(5000.00);
        raynold.setLanguage("ms");

        // Add some initial transactions for Raynold
        Transaction t1 = new Transaction("DEPOSIT", 5000.00, 5000.00, "Initial Deposit");
        raynold.addTransaction(t1);

        hardcodedUsers.add(raynold);

        // User 2: Siti Nurhaliza
        User siti = new User();
        siti.setUserId("USER002");
        siti.setEmail("siti");
        siti.setPassword("siti123");
        siti.setFullName("Siti Nurhaliza binti Hassan");
        siti.setPhoneNumber("+60198765432");
        siti.setAccountNumber("0987654321");
        siti.setBalance(10000.00);
        siti.setLanguage("ms");

        // Add some initial transactions for Siti
        Transaction t2 = new Transaction("DEPOSIT", 10000.00, 10000.00, "Initial Deposit");
        siti.addTransaction(t2);

        hardcodedUsers.add(siti);

        // User 3: Kumar Rajesh
        User kumar = new User();
        kumar.setUserId("USER003");
        kumar.setEmail("kumar");
        kumar.setPassword("kumar123");
        kumar.setFullName("Kumar Rajesh");
        kumar.setPhoneNumber("+60167891234");
        kumar.setAccountNumber("5555666677");
        kumar.setBalance(7500.50);
        kumar.setLanguage("en");

        // Add some initial transactions for Kumar
        Transaction t3 = new Transaction("DEPOSIT", 7500.50, 7500.50, "Initial Deposit");
        kumar.addTransaction(t3);

        hardcodedUsers.add(kumar);
    }

    /**
     * Authenticate user with username and password.
     * 
     * @param username The username (email field is used)
     * @param password The password
     * @return User object if authentication successful, null otherwise
     */
    public User authenticate(String username, String password) {
        if (username == null || password == null) {
            return null;
        }

        for (User user : hardcodedUsers) {
            if (user.getEmail().equals(username.trim()) &&
                    user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Get all hardcoded users (for demo purposes).
     * 
     * @return List of all demo users
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(hardcodedUsers);
    }

    /**
     * Get user by username.
     * 
     * @param username The username to search for
     * @return User object if found, null otherwise
     */
    public User getUserByUsername(String username) {
        if (username == null) {
            return null;
        }

        for (User user : hardcodedUsers) {
            if (user.getEmail().equals(username.trim())) {
                return user;
            }
        }
        return null;
    }
}
