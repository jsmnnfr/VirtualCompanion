package com.example.virtualcompanion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MoodResultActivity extends BaseActivity {

    private static final String[] MOOD_LABELS = {
            "Neutral", "Happy", "Sad", "Angry", "Anxious"
    };

    private static final String[] MOOD_MESSAGES = {
            "You’re doing your best, and that’s enough!",
            "Happiness looks good on you. Keep it!",
            "It’s okay to feel low. You're not alone.",
            "Your feelings are loud, but you’re stronger.",
            "Breathe. You’re safe. You’ve got this."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_result);

        // ===== MOOD REFLECTION =====
        int moodIndex = getIntent().getIntExtra("selected_mood", -1);

        if (moodIndex == -1) {
            moodIndex = DatabaseManager.get(this).getLatestMood();
        }
        final int finalMoodIndex = moodIndex;

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

        int[] overlays = "male".equalsIgnoreCase(gender)
                ? new int[]{
                R.drawable.emote_neutral_b_moodresult,
                R.drawable.emote_happy_b_moodresult,
                R.drawable.emote_sad_b_moodresult,
                R.drawable.emote_angry_b_moodresult,
                R.drawable.emote_anxious_b_moodresult
        }
                : new int[]{
                R.drawable.emote_neutral_g_moodresult,
                R.drawable.emote_happy_g_moodresult,
                R.drawable.emote_sad_g_moodresult,
                R.drawable.emote_angry_g_moodresult,
                R.drawable.emote_anxious_g_moodresult
        };

        ImageView emotionOverlay = findViewById(R.id.emotionOverlay);
        ImageView resultPetBase = findViewById(R.id.resultPetBase);
        TextView resultMoodLabel = findViewById(R.id.resultMoodLabel);
        TextView moodMessage = findViewById(R.id.moodMessage);

        if (resultPetBase != null) {
            resultPetBase.setImageResource(emotions[moodIndex]);
        }

        if (emotionOverlay != null) {
            emotionOverlay.setImageResource((overlays[moodIndex]));
        }

        if (resultMoodLabel != null) {
            resultMoodLabel.setText(MOOD_LABELS[moodIndex]);
        }

        if (moodMessage != null) {
            moodMessage.setText(MOOD_MESSAGES[moodIndex]);
        }

        String flow = getIntent().getStringExtra("flow");

        if ("QUEST_COMPLETE".equals(flow)) {

            if (moodMessage != null) {
                moodMessage.setText("You completed all your tasks. How do you feel now?");
            }

            if (resultMoodLabel != null) {
                resultMoodLabel.setText("Well Done");
            }

        } else if ("HAPPY_MOOD".equals(flow)) {

            if (moodMessage != null) {
                moodMessage.setText("You're feeling happy today! No need for you to do some tasks! Have a great day ahead!");
            }

            if (resultMoodLabel != null) {
                resultMoodLabel.setText("Happy");
            }
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
                    Toast.makeText(this, "[DEV] +100 coins added", Toast.LENGTH_SHORT).show();
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
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        // Home to MoodResultActivity
        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Intent intent = new Intent(
                        MoodResultActivity.this,
                        MoodResultActivity.class
                );
                intent.putExtra("selected_mood", finalMoodIndex);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        // ================= QUESTS BUTTON - ALWAYS ENABLED =================
        if (navTasks != null) {
            navTasks.setAlpha(1.0f); // Always visible
            navTasks.setOnClickListener(v -> {
                Intent intent = new Intent(
                        MoodResultActivity.this,
                        QuestsActivity.class
                );
                intent.putExtra("selected_mood", finalMoodIndex);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        // Customize → CustomTopActivity
        if (navCustomize != null) {
            navCustomize.setOnClickListener(v -> {
                Intent intent = new Intent(
                        MoodResultActivity.this,
                        CustomTopActivity.class
                );
                intent.putExtra("selected_mood", finalMoodIndex);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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