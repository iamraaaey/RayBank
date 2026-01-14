package com.example.raybank;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.raybank.model.User;
import com.example.raybank.utils.DataManager;

/**
 * Account settings activity for app preferences.
 */
public class AccountSettingsActivity extends AppCompatActivity {
    private DataManager dataManager;
    private User currentUser;

    private Switch switchLanguage;
    private Switch switchBiometric;
    private TextView textViewLanguageLabel;
    private TextView textViewBiometricLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.account_settings);

        dataManager = new DataManager(this);
        currentUser = dataManager.getCurrentUser();

        if (currentUser == null) {
            finish();
            return;
        }

        initializeViews();
        loadSettings();
        setupClickListeners();
    }

    private void initializeViews() {
        switchLanguage = findViewById(R.id.switchLanguage);
        switchBiometric = findViewById(R.id.switchBiometric);
        textViewLanguageLabel = findViewById(R.id.textViewLanguageLabel);
        textViewBiometricLabel = findViewById(R.id.textViewBiometricLabel);
    }

    private void loadSettings() {
        String language = currentUser.getLanguage();
        switchLanguage.setChecked(language != null && language.equals("ms"));
        switchBiometric.setChecked(currentUser.isBiometricEnabled());

        updateLanguageLabels();
    }

    private void updateLanguageLabels() {
        boolean isMalay = switchLanguage.isChecked();
        textViewLanguageLabel
                .setText(isMalay ? getString(R.string.language_malay) : getString(R.string.language_english));
        textViewBiometricLabel.setText(getString(R.string.biometric_authentication));
    }

    private void setupClickListeners() {
        switchLanguage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentUser.setLanguage(isChecked ? "ms" : "en");
            dataManager.saveUser(currentUser);
            dataManager.setCurrentUser(currentUser);
            updateLanguageLabels();
            // Note: In a real app, you would restart the activity to apply language changes
        });

        switchBiometric.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentUser.setBiometricEnabled(isChecked);
            dataManager.saveUser(currentUser);
            dataManager.setCurrentUser(currentUser);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
