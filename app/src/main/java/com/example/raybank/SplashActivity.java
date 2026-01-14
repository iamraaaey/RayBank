package com.example.raybank;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.example.raybank.utils.DataManager;

/**
 * Splash screen activity that shows on app launch.
 */
public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        DataManager dataManager = new DataManager(this);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;

                // Check if onboarding is complete
                if (!dataManager.isOnboardingComplete()) {
                    // Go to onboarding
                    intent = new Intent(SplashActivity.this, OnboardingActivity.class);
                } else if (dataManager.getCurrentUser() != null) {
                    // User is logged in, go to main activity
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    // Go to sign in
                    intent = new Intent(SplashActivity.this, SignInActivity.class);
                }

                startActivity(intent);
                finish();
            }
        }, SPLASH_DURATION);
    }
}
