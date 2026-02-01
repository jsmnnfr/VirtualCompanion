package com.example.virtualcompanion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MoodActivity extends BaseActivity {

    private String currentGender = "male";
    private int selectedMoodIndex = -1;
    private ImageView mainPetImage;

    // Pet image resources based on gender and mood
    private static final int[] MALE_PET_EMOTIONS = {
            R.drawable.emotion_neutral,
            R.drawable.emotion_happy,
            R.drawable.emotion_sad,
            R.drawable.emotion_angry,
            R.drawable.emotion_anxious
    };

    private static final int[] FEMALE_PET_EMOTIONS = {
            R.drawable.emotion_neutral_g,
            R.drawable.emotion_happy_g,
            R.drawable.emotion_sad_g,
            R.drawable.emotion_angry_g,
            R.drawable.emotion_anxious_g
    };

    // Emoji resources based on gender
    private static final int[] MALE_EMOJIS = {
            R.drawable.emoji_neutral_b,
            R.drawable.emoji_happy_b,
            R.drawable.emoji_sad_b,
            R.drawable.emoji_angry_b,
            R.drawable.emoji_anxious_b
    };

    private static final int[] FEMALE_EMOJIS = {
            R.drawable.emoji_neutral_g,
            R.drawable.emoji_happy_g,
            R.drawable.emoji_sad_g,
            R.drawable.emoji_angry_g,
            R.drawable.emoji_anxious_g
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mood);

        // Get database instance
        DatabaseManager db = DatabaseManager.get(this);

        // Load gender from database
        currentGender = db.getGender();

        // Submit Button
        MaterialButton submitButton = findViewById(R.id.submitButton);

        // Top Settings Icon
        ImageView settingsIcon = findViewById(R.id.settingsIcon);

        // Pet Image
        mainPetImage = findViewById(R.id.mainPetImage);

        // Emojis
        ImageView emoji1 = findViewById(R.id.emoji1);
        ImageView emoji2 = findViewById(R.id.emoji2);
        ImageView emoji3 = findViewById(R.id.emoji3);
        ImageView emoji4 = findViewById(R.id.emoji4);
        ImageView emoji5 = findViewById(R.id.emoji5);

        // Bottom Navigation
        ImageView navHome = findViewById(R.id.navHome);
        ImageView navQuests = findViewById(R.id.navQuests);
        ImageView navCustomize = findViewById(R.id.navCustomize);

        // Apply gender-specific images to emojis
        applyGenderEmojis(emoji1, emoji2, emoji3, emoji4, emoji5);

        initializePetImage();

        // Set up emoji click listeners
        setupEmojiListeners(emoji1, 0);
        setupEmojiListeners(emoji2, 1);
        setupEmojiListeners(emoji3, 2);
        setupEmojiListeners(emoji4, 3);
        setupEmojiListeners(emoji5, 4);

        // Submit → Mood Result
        submitButton.setOnClickListener(v -> {

            if (selectedMoodIndex == -1) {
                Toast.makeText(this, "Please select a mood first", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseManager.get(this).saveMood(
                    selectedMoodIndex + 1,
                    String.valueOf(System.currentTimeMillis())
            );

            Intent intent = new Intent(
                    MoodActivity.this,
                    MoodResultActivity.class
            );

            intent.putExtra("selected_mood", selectedMoodIndex);

            startActivity(intent);
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

    /**
     * Apply gender-specific emoji images
     */
    private void applyGenderEmojis(ImageView emoji1, ImageView emoji2, ImageView emoji3, 
                                    ImageView emoji4, ImageView emoji5) {
        int[] emojiResources = "male".equalsIgnoreCase(currentGender) ? MALE_EMOJIS : FEMALE_EMOJIS;
        
        emoji1.setImageResource(emojiResources[0]);
        emoji2.setImageResource(emojiResources[1]);
        emoji3.setImageResource(emojiResources[2]);
        emoji4.setImageResource(emojiResources[3]);
        emoji5.setImageResource(emojiResources[4]);
    }

    /**
     * Initialize pet image with gender-appropriate neutral emotion
     */
    private void initializePetImage() {
        if (mainPetImage != null) {
            int[] petEmotions = "male".equalsIgnoreCase(currentGender) ? MALE_PET_EMOTIONS : FEMALE_PET_EMOTIONS;
            mainPetImage.setImageResource(petEmotions[0]); // 0 = neutral emotion
        }
    }

    /**
     * Set up emoji click listener and update pet emotion
     */
    private void setupEmojiListeners(ImageView emojiView, int moodIndex) {
        emojiView.setOnClickListener(v -> {
            selectedMoodIndex = moodIndex;
            updatePetEmotion(moodIndex);
        });
    }

    /**
     * Update pet emotion based on selected mood
     */
    private void updatePetEmotion(int moodIndex) {
        if (mainPetImage != null) {
            int[] petEmotions = "male".equalsIgnoreCase(currentGender) ? MALE_PET_EMOTIONS : FEMALE_PET_EMOTIONS;
            mainPetImage.setImageResource(petEmotions[moodIndex]);
        }
    }
}