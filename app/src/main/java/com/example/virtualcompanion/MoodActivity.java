package com.example.virtualcompanion;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class MoodActivity extends BaseActivity {

    private String currentGender = "male";
    private int selectedMoodIndex = -1;
    private ImageView mainPetImage;
    private ImageView lastSelectedEmoji = null;

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

        // Top Settings Icon - ALWAYS DISABLED
        ImageView settingsIcon = findViewById(R.id.settingsIcon);

        // Pet Image
        mainPetImage = findViewById(R.id.mainPetImage);

        // Emojis
        ImageView emoji1 = findViewById(R.id.emoji1);
        ImageView emoji2 = findViewById(R.id.emoji2);
        ImageView emoji3 = findViewById(R.id.emoji3);
        ImageView emoji4 = findViewById(R.id.emoji4);
        ImageView emoji5 = findViewById(R.id.emoji5);

        android.widget.TextView moodPrompt = findViewById(R.id.moodPrompt);
        android.widget.TextView moodInfoMessage = findViewById(R.id.moodInfoMessage);

        // Bottom Navigation
        ImageView navHome = findViewById(R.id.navHome);
        ImageView navQuests = findViewById(R.id.navQuests);
        ImageView navCustomize = findViewById(R.id.navCustomize);

        // Apply gender-specific images to emojis
        applyGenderEmojis(emoji1, emoji2, emoji3, emoji4, emoji5);

        initializePetImage();

        // Set up emoji click listeners with animation
        setupEmojiListeners(emoji1, 0);
        setupEmojiListeners(emoji2, 1);
        setupEmojiListeners(emoji3, 2);
        setupEmojiListeners(emoji4, 3);
        setupEmojiListeners(emoji5, 4);

        // Get the flow to determine context
        String flow = getIntent().getStringExtra("flow");

        // Mood message prompt
        if ("QUEST_COMPLETE".equals(flow)) {
            if (moodInfoMessage != null) {
                moodInfoMessage.setVisibility(View.VISIBLE);
                moodInfoMessage.setText("You've accomplished so much today! You did it! how are you feeling now?");
            }
            if (moodPrompt != null) {
                moodPrompt.setVisibility(View.GONE);
            }
        }

        // Submit → Mood Result
        submitButton.setOnClickListener(v -> {

            if (selectedMoodIndex == -1) {
                Toast.makeText(this, "Please select a mood first", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save mood with current date
            DatabaseManager dbManager = DatabaseManager.get(this);
            dbManager.saveMood(
                    selectedMoodIndex + 1,
                    dbManager.getTodayDate()
            );

            Intent intent = new Intent(
                    MoodActivity.this,
                    MoodResultActivity.class
            );

            intent.putExtra("selected_mood", selectedMoodIndex);

            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish(); // Finish this activity so they can't go back to selection today
        });

        // Settings - ALWAYS DISABLED (NO OPACITY CHANGE)
        if (settingsIcon != null) {
            settingsIcon.setOnClickListener(v -> {
                Toast.makeText(MoodActivity.this, "Please set your mood first", Toast.LENGTH_SHORT).show();
            });
        }

        // Home - ALWAYS DISABLED (NO OPACITY CHANGE)
        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Toast.makeText(MoodActivity.this, "Please set your mood first", Toast.LENGTH_SHORT).show();
            });
        }

        // Quests - ALWAYS DISABLED (NO OPACITY CHANGE)
        if (navQuests != null) {
            navQuests.setOnClickListener(v -> {
                Toast.makeText(MoodActivity.this, "Please set your mood first", Toast.LENGTH_SHORT).show();
            });
        }

        // Customize - ALWAYS DISABLED (NO OPACITY CHANGE)
        if (navCustomize != null) {
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
     * Set up emoji click listener with pop-up animation
     */
    private void setupEmojiListeners(ImageView emojiView, int moodIndex) {
        emojiView.setOnClickListener(v -> {
            selectedMoodIndex = moodIndex;

            // Reset previous selection
            if (lastSelectedEmoji != null && lastSelectedEmoji != emojiView) {
                resetEmojiScale(lastSelectedEmoji);
            }

            // Animate current selection
            animateEmojiPopUp(emojiView);

            // Update pet emotion
            updatePetEmotion(moodIndex);

            // Remember last selected
            lastSelectedEmoji = emojiView;
        });
    }

    /**
     * Animate emoji with pop-up effect when clicked
     */
    private void animateEmojiPopUp(ImageView emojiView) {
        // Scale up animation with bounce effect
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(emojiView, "scaleX", 1f, 1.3f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(emojiView, "scaleY", 1f, 1.3f);

        // Slight elevation effect
        ObjectAnimator elevate = ObjectAnimator.ofFloat(emojiView, "translationZ", 0f, 16f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, elevate);
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new OvershootInterpolator(2f)); // Bounce effect
        animatorSet.start();
    }

    /**
     * Reset emoji scale to normal
     */
    private void resetEmojiScale(ImageView emojiView) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(emojiView, "scaleX", 1.3f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(emojiView, "scaleY", 1.3f, 1f);
        ObjectAnimator lower = ObjectAnimator.ofFloat(emojiView, "translationZ", 16f, 0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, lower);
        animatorSet.setDuration(200);
        animatorSet.start();
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