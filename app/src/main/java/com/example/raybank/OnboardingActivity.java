package com.example.raybank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.raybank.utils.DataManager;

/**
 * Onboarding activity with multiple screens introducing the app.
 */
public class OnboardingActivity extends AppCompatActivity {
    private Button buttonNext;
    private Button buttonSkip;
    private TextView textViewTitle;
    private TextView textViewDescription;

    private int currentPage = 0;
    private final int[] titles = {
            R.string.onboarding_title_1,
            R.string.onboarding_title_2,
            R.string.onboarding_title_3
    };
    private final int[] descriptions = {
            R.string.onboarding_desc_1,
            R.string.onboarding_desc_2,
            R.string.onboarding_desc_3
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        buttonNext = findViewById(R.id.buttonNext);
        buttonSkip = findViewById(R.id.buttonSkip);
        textViewTitle = findViewById(R.id.textViewOnboardingTitle);
        textViewDescription = findViewById(R.id.textViewOnboardingDescription);

        updateUI();

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage < titles.length - 1) {
                    currentPage++;
                    updateUI();
                } else {
                    completeOnboarding();
                }
            }
        });

        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeOnboarding();
            }
        });
    }

    /**
     * Updates the UI elements based on the current onboarding page.
     * Sets the title, description, and button text accordingly.
     */
    private void updateUI() {
        textViewTitle.setText(titles[currentPage]);
        textViewDescription.setText(descriptions[currentPage]);

        if (currentPage == titles.length - 1) {
            buttonNext.setText(R.string.get_started);
        } else {
            buttonNext.setText(R.string.next);
        }
    }

    /**
     * Mark onboarding as complete and navigate to the Sign In screen.
     */
    private void completeOnboarding() {
        DataManager dataManager = new DataManager(this);
        dataManager.setOnboardingComplete(true);

        Intent intent = new Intent(OnboardingActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }
}
