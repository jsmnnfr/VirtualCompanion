package com.example.virtualcompanion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MoodResultActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mood_result);

        // ================= OUTFIT LAYERS =================
        try {
            ImageView topLayer = findViewById(R.id.topLayer);
            ImageView bottomLayer = findViewById(R.id.bottomLayer);
            ImageView hatLayer = findViewById(R.id.hatLayer);
            ImageView glassesLayer = findViewById(R.id.glassesLayer);

            // Load saved outfits
            loadOutfit(topLayer, OutfitManager.getTop(this));
            loadOutfit(bottomLayer, OutfitManager.getBottom(this));
            loadOutfit(hatLayer, OutfitManager.getHat(this));
            loadOutfit(glassesLayer, OutfitManager.getGlasses(this));
        } catch (Exception e) {
            // Outfit layers not in layout, skip
        }

        // ================= COIN DISPLAY =================
        try {
            android.widget.TextView coinAmount = findViewById(R.id.coinAmount);
            if (coinAmount != null) {
                int coins = DatabaseManager.get(this).getCoins();
                coinAmount.setText(String.valueOf(coins));
                
                // CHEAT MODE: Long press to add 100 coins
                coinAmount.setOnLongClickListener(v -> {
                    DatabaseManager.get(this).addCoins(100);
                    int newCoins = DatabaseManager.get(this).getCoins();
                    coinAmount.setText(String.valueOf(newCoins));
                    android.widget.Toast.makeText(this, "[DEV] +100 coins added", android.widget.Toast.LENGTH_SHORT).show();
                    return true;
                });
            }
        } catch (Exception e) {
            // Coin display failed, skip
        }

        // Top Settings Icon
        ImageView settingsIcon = findViewById(R.id.settingsIcon);

        // Bottom Navigation
        ImageView navHome = findViewById(R.id.navHome);
        ImageView navTasks = findViewById(R.id.navQuests);
        ImageView navCustomize = findViewById(R.id.navCustomize);

        // Settings → SettingsActivity
        if (settingsIcon != null) {
            settingsIcon.setOnClickListener(v -> {

                Intent intent = new Intent(
                        MoodResultActivity.this,
                        SettingsActivity.class
                );

                startActivity(intent);
            });
        }

        // Home to MoodResultActivity
        if (navHome != null) {
            navHome.setOnClickListener(v -> {

                Intent intent = new Intent(
                        MoodResultActivity.this,
                        MoodResultActivity.class
                );

                startActivity(intent);
            });
        }

        // Quests → QuestsActivity
        if (navTasks != null) {
            navTasks.setOnClickListener(v -> {

                Intent intent = new Intent(
                        MoodResultActivity.this,
                        QuestsActivity.class
                );

                startActivity(intent);
            });
        }

        // Customize → CustomTopActivity
        if (navCustomize != null) {
            navCustomize.setOnClickListener(v -> {

                Intent intent = new Intent(
                        MoodResultActivity.this,
                        CustomTopActivity.class
                );

                startActivity(intent);
            });
        }
    }

    /**
     * Load outfit layer from OutfitManager
     */
    private void loadOutfit(ImageView layer, int resId) {
        if (layer == null) return;

        if (resId == 0) {
            layer.setVisibility(ImageView.GONE);
        } else {
            layer.setImageResource(resId);
            layer.setVisibility(ImageView.VISIBLE);
        }
    }
}
