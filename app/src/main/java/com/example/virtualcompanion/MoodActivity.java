package com.example.virtualcompanion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MoodActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mood);

        // Submit Button
        MaterialButton submitButton = findViewById(R.id.submitButton);

        // Top Settings Icon
        ImageView settingsIcon = findViewById(R.id.settingsIcon);

        // Bottom Navigation
        ImageView navHome = findViewById(R.id.navHome);
        ImageView navQuests = findViewById(R.id.navQuests);
        ImageView navCustomize = findViewById(R.id.navCustomize);

        // Submit → Mood Result
        submitButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MoodActivity.this,
                    MoodResultActivity.class
            );

            startActivity(intent);
            // ❌ NO finish()
        });

        // Settings → Settings
        if (settingsIcon != null) {
            settingsIcon.setOnClickListener(v -> {

                Intent intent = new Intent(
                        MoodActivity.this,
                        SettingsActivity.class
                );

                startActivity(intent);
            });
        }

        // Home - DISABLED with message
        if (navHome != null) {
            navHome.setAlpha(0.3f);
            navHome.setOnClickListener(v -> {
                Toast.makeText(MoodActivity.this, "Please set your mood first", Toast.LENGTH_SHORT).show();
            });
        }

        // Quests - DISABLED with message
        if (navQuests != null) {
            navQuests.setAlpha(0.3f);
            navQuests.setOnClickListener(v -> {
                Toast.makeText(MoodActivity.this, "Please set your mood first", Toast.LENGTH_SHORT).show();
            });
        }

        // Customize - DISABLED with message
        if (navCustomize != null) {
            navCustomize.setAlpha(0.3f);
            navCustomize.setOnClickListener(v -> {
                Toast.makeText(MoodActivity.this, "Please set your mood first", Toast.LENGTH_SHORT).show();
            });
        }
        
        // Update coin display
        updateCoinDisplay();
    }
    
    private void updateCoinDisplay() {
        android.widget.TextView coinAmount = findViewById(R.id.coinAmount);
        if (coinAmount != null) {
            try {
                int coins = DatabaseManager.get(this).getCoins();
                coinAmount.setText(String.valueOf(coins));
            } catch (Exception e) {
                coinAmount.setText("0");
            }
            
            // CHEAT MODE: Long press to add 100 coins
            coinAmount.setOnLongClickListener(v -> {
                DatabaseManager.get(this).addCoins(100);
                updateCoinDisplay();
                Toast.makeText(this, "[DEV] +100 coins added", Toast.LENGTH_SHORT).show();
                return true;
            });
        }
    }
}