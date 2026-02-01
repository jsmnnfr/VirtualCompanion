package com.example.virtualcompanion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MoodResultActivity extends BaseActivity {

    private static final String[] MOOD_LABELS = {
            "Neutral", "Happy", "Sad", "Angry", "Anxious"
    };

    private static final String[] MOOD_MESSAGES = {
            "It’s okay to feel steady today.",
            "Great to see you feeling good today!",
            "I know today feels heavy. You’re not alone.",
            "Strong emotions are valid.",
            "It’s okay to feel uneasy. I’ve got you."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_result);

        // ===== MOOD REFLECTION =====
        int moodIndex = DatabaseManager.get(this).getLatestMood();
        String gender = DatabaseManager.get(this).getGender();

        int[] emotions = "male".equalsIgnoreCase(gender)
                ? new int[]{
                R.drawable.emotion_neutral,
                R.drawable.emotion_happy,
                R.drawable.emotion_sad,
                R.drawable.emotion_angry,
                R.drawable.emotion_anxious
        }
                : new int[]{
                R.drawable.emotion_neutral_g,
                R.drawable.emotion_happy_g,
                R.drawable.emotion_sad_g,
                R.drawable.emotion_angry_g,
                R.drawable.emotion_anxious_g
        };

        ImageView emotionOverlay = findViewById(R.id.emotionOverlay);
        ImageView resultPetBase = findViewById(R.id.resultPetBase);
        TextView resultMoodLabel = findViewById(R.id.resultMoodLabel);
        TextView moodMessage = findViewById(R.id.moodMessage);

        if (emotionOverlay != null) {
            emotionOverlay.setImageResource(
                    "male".equalsIgnoreCase(gender)
                            ? R.drawable.emotion_neutral
                            : R.drawable.emotion_neutral_g
            );
        }

        if (resultPetBase != null) {
            resultPetBase.setImageResource(emotions[moodIndex]);
        }

        if (resultMoodLabel != null) {
            resultMoodLabel.setText(MOOD_LABELS[moodIndex]);
        }

        if (moodMessage != null) {
            moodMessage.setText(MOOD_MESSAGES[moodIndex]);
        }

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
