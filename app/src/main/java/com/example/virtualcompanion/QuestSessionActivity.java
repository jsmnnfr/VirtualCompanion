package com.example.virtualcompanion;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;

public class QuestSessionActivity extends BaseActivity {

    private static final String TAG = "QuestSessionActivity";

    private TextView questTitleText;
    private TextView questDescriptionText;
    private TextView timerText;
    private TextView instructionText;
    private TextView moodBadge;
    private MaterialButton actionButton;
    private ImageView backgroundImage;
    private ImageView petDisplay;
    private ImageView backButton;
    private View moodIndicator;
    private View gradientOverlay;
    private ConstraintLayout rootLayout;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    private Quest currentQuest;
    private int questPosition;
    private String questMood;

    private Vibrator vibrator;

    // Store animator references to stop them properly
    private ObjectAnimator timerFlashAnimator;
    private ObjectAnimator petFlashAnimator;

    public static final int RESULT_QUEST_COMPLETED = 2001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_session);

        Log.d(TAG, "onCreate started");

        // IMPORTANT: Load quest data FIRST to get the mood
        loadQuestDataEarly();


        // Get the appropriate music track for this mood
        int questTrack = MusicManager.getQuestTrackForMood(questMood);


        // Switch to mood-specific quest music (this saves the previous track)
        MusicManager.startQuestMusic(this, questMood);

        initializeViews();
        setupBackButton();
        setupBackground();
        setupMoodAnimations();
        startQuestSession();

        // Initialize vibrator
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    // Helper method to get track name for debugging
    private String getTrackName(int trackId) {
        if (trackId == MusicManager.TRACK_BACKGROUND) return "background_music";
        if (trackId == MusicManager.TRACK_QUEST_HAPPY) return "quest_happy";
        if (trackId == MusicManager.TRACK_QUEST_SAD) return "quest_sad";
        if (trackId == MusicManager.TRACK_QUEST_ANGRY) return "quest_angry";
        if (trackId == MusicManager.TRACK_QUEST_ANXIOUS) return "quest_anxious";
        if (trackId == MusicManager.TRACK_QUEST_NEUTRAL) return "quest_neutral";
        return "unknown";
    }

    // ================= LOAD QUEST DATA EARLY (to get mood) =================
    private void loadQuestDataEarly() {
        Intent intent = getIntent();

        int questId = intent.getIntExtra("quest_id", -1);
        String questTitle = intent.getStringExtra("quest_title");
        String questDescription = intent.getStringExtra("quest_description");
        int questReward = intent.getIntExtra("quest_reward", 0);
        int questTimer = intent.getIntExtra("quest_timer", 5);
        questMood = intent.getStringExtra("quest_mood");
        questPosition = intent.getIntExtra("quest_position", 0);

        Log.d(TAG, "Quest loaded - ID: " + questId + ", Mood: " + questMood + ", Title: " + questTitle);

        if (questId == -1) {
            Toast.makeText(this, "Error loading quest", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentQuest = new Quest(questId, questTitle, questDescription, questReward, questMood, questTimer);
    }

    // ================= INITIALIZE =================
    private void initializeViews() {
        questTitleText = findViewById(R.id.questTitleText);
        questDescriptionText = findViewById(R.id.questDescriptionText);
        timerText = findViewById(R.id.timerText);
        instructionText = findViewById(R.id.instructionText);
        moodBadge = findViewById(R.id.moodBadge);
        actionButton = findViewById(R.id.actionButton);
        backgroundImage = findViewById(R.id.backgroundImage);
        petDisplay = findViewById(R.id.petDisplay);
        backButton = findViewById(R.id.backButton);
        moodIndicator = findViewById(R.id.moodIndicator);
        gradientOverlay = findViewById(R.id.gradientOverlay);
        rootLayout = findViewById(R.id.rootLayout);

        // Set quest title and description
        if (currentQuest != null) {
            questTitleText.setText(currentQuest.getTitle());
            questDescriptionText.setText(currentQuest.getDescription());
        }
    }

    // ================= SETUP BACK BUTTON =================
    private void setupBackButton() {
        backButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Leave Quest?")
                    .setMessage("Are you sure you want to stop this quest? Your progress will not be saved.")
                    .setPositiveButton("Yes, Leave", (dialog, which) -> {
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                        }
                        stopAllAlerts();

                        // Restore the music that was playing before the quest
                        Log.d(TAG, "User cancelled quest, restoring previous music");
                        MusicManager.restorePreQuestMusic(QuestSessionActivity.this);

                        finish();
                    })
                    .setNegativeButton("Continue Quest", null)
                    .show();
        });
    }

    // ================= SETUP BACKGROUND =================
    private void setupBackground() {
        int backgroundColor;
        int badgeColor;
        String badgeText;
        int petEmotion;

        String gender = DatabaseManager.get(this).getGender();
        boolean isFemale = "female".equalsIgnoreCase(gender);

        switch (questMood.toLowerCase()) {
            case "happy":
                backgroundColor = Color.parseColor("#FFF9E6");
                badgeColor = Color.parseColor("#FFA726");
                badgeText = "JOYFUL";
                petEmotion = isFemale ? R.drawable.emotion_happy_g : R.drawable.emotion_happy;
                break;
            case "sad":
                backgroundColor = Color.parseColor("#E8F4F8");
                badgeColor = Color.parseColor("#5DADE2");
                badgeText = "COMFORTING";
                petEmotion = isFemale ? R.drawable.emotion_sad_g : R.drawable.emotion_sad;
                break;
            case "angry":
                backgroundColor = Color.parseColor("#FFE8E8");
                badgeColor = Color.parseColor("#EF5350");
                badgeText = "RELEASING";
                petEmotion = isFemale ? R.drawable.emotion_angry_g : R.drawable.emotion_angry;
                break;
            case "anxious":
                backgroundColor = Color.parseColor("#F0E8FF");
                badgeColor = Color.parseColor("#AB47BC");
                badgeText = "CALMING";
                petEmotion = isFemale ? R.drawable.emotion_anxious_g : R.drawable.emotion_anxious;
                break;
            default:
                backgroundColor = Color.parseColor("#F0F4F8");
                badgeColor = Color.parseColor("#78909C");
                badgeText = "BALANCED";
                petEmotion = isFemale ? R.drawable.emotion_neutral_g : R.drawable.emotion_neutral;
                break;
        }

        rootLayout.setBackgroundColor(backgroundColor);

        GradientDrawable badgeBg = (GradientDrawable) moodBadge.getBackground();
        badgeBg.setColor(badgeColor);
        moodBadge.setText(badgeText);

        GradientDrawable indicatorBg = (GradientDrawable) moodIndicator.getBackground();
        indicatorBg.setColor(badgeColor);

        petDisplay.setImageResource(petEmotion);
    }

    // ================= MOOD ANIMATIONS =================
    private void setupMoodAnimations() {
        Animation breathe = AnimationUtils.loadAnimation(this, R.anim.breathe_in_out);
        petDisplay.startAnimation(breathe);

        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse_glow);
        moodIndicator.startAnimation(pulse);

        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        findViewById(R.id.questInfoCard).startAnimation(slideUp);

        switch (questMood.toLowerCase()) {
            case "happy":
                breathe.setDuration(1500);
                pulse.setDuration(1000);
                break;
            case "sad":
                breathe.setDuration(3000);
                pulse.setDuration(2500);
                break;
            case "angry":
                breathe.setDuration(1000);
                pulse.setDuration(800);
                break;
            case "anxious":
                breathe.setDuration(2000);
                pulse.setDuration(1800);
                break;
            default:
                breathe.setDuration(2000);
                pulse.setDuration(1500);
                break;
        }
    }

    // ================= START SESSION =================
    private void startQuestSession() {
        instructionText.setText("Focus on your task...");
        actionButton.setEnabled(false);
        actionButton.setText("In Progress...");

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        instructionText.startAnimation(fadeIn);

        long timerDuration = currentQuest.getTimerMinutes() * 60 * 1000;
        startCountdown(timerDuration);
    }

    // ================= COUNTDOWN =================
    private void startCountdown(long durationMillis) {
        timeLeftInMillis = durationMillis;

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(durationMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerDisplay();
                animateTimerPulse();
            }

            @Override
            public void onFinish() {
                timerText.setText("00:00");
                onTimerComplete();
            }
        }.start();
    }

    // ================= UPDATE TIMER =================
    private void updateTimerDisplay() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeFormatted = String.format("%02d:%02d", minutes, seconds);
        timerText.setText(timeFormatted);

        if (timeLeftInMillis < 60000) {
            timerText.setTextColor(Color.parseColor("#FF6B6B"));
        } else if (timeLeftInMillis < 180000) {
            timerText.setTextColor(Color.parseColor("#FFB84D"));
        } else {
            timerText.setTextColor(Color.parseColor("#4CAF50"));
        }
    }

    // ================= TIMER PULSE ANIMATION =================
    private void animateTimerPulse() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(timerText, "scaleX", 1f, 1.05f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(timerText, "scaleY", 1f, 1.05f, 1f);
        scaleX.setDuration(300);
        scaleY.setDuration(300);
        scaleX.start();
        scaleY.start();
    }

    // ================= TIMER COMPLETE WITH VIBRATION ONLY =================
    private void onTimerComplete() {
        // Vibrate device (NO SOUND)
        vibrateDevice();

        // Flash screen
        flashScreen();

        instructionText.setText("TIME'S UP!");
        actionButton.setEnabled(true);
        actionButton.setText("Check Completion");

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(actionButton, "scaleX", 0.9f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(actionButton, "scaleY", 0.9f, 1f);
        scaleX.setDuration(300);
        scaleY.setDuration(300);
        scaleX.start();
        scaleY.start();

        actionButton.setOnClickListener(v -> {
            stopAllAlerts();
            Animation buttonPress = AnimationUtils.loadAnimation(this, R.anim.button_press);
            v.startAnimation(buttonPress);
            showCompletionDialog();
        });
    }

    // ================= VIBRATE DEVICE =================
    private void vibrateDevice() {
        if (vibrator != null && vibrator.hasVibrator()) {
            // Vibrate pattern: wait 0ms, vibrate 500ms, wait 200ms, vibrate 500ms
            long[] pattern = {0, 500, 200, 500, 200, 500};

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0)); // 0 = repeat
            } else {
                vibrator.vibrate(pattern, 0); // 0 = repeat
            }
        }
    }

    // ================= FLASH SCREEN =================
    private void flashScreen() {
        // Flash timer
        timerFlashAnimator = ObjectAnimator.ofFloat(timerText, "alpha", 1f, 0.3f);
        timerFlashAnimator.setDuration(500);
        timerFlashAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        timerFlashAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        timerFlashAnimator.start();

        // Flash pet
        petFlashAnimator = ObjectAnimator.ofFloat(petDisplay, "alpha", 1f, 0.7f);
        petFlashAnimator.setDuration(500);
        petFlashAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        petFlashAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        petFlashAnimator.start();
    }

    // ================= STOP ALL ALERTS =================
    private void stopAllAlerts() {
        // Stop vibration
        if (vibrator != null) {
            vibrator.cancel();
        }

        // Stop flash animations
        if (timerFlashAnimator != null) {
            timerFlashAnimator.cancel();
            timerText.setAlpha(1f);
        }

        if (petFlashAnimator != null) {
            petFlashAnimator.cancel();
            petDisplay.setAlpha(1f);
        }
    }

    // ================= SHOW COMPLETION DIALOG =================
    private void showCompletionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Quest Complete?")
                .setMessage("Did you complete this quest successfully?")
                .setPositiveButton("Yes, I did it!", (dialog, which) -> {
                    markQuestAsCompleted();

                    // ========== CHECK IF ALL QUESTS ARE COMPLETE ==========
                    DatabaseManager db = DatabaseManager.get(this);
                    if (db.areAllCurrentQuestsComplete()) {
                        android.util.Log.d(TAG, "ALL QUESTS COMPLETE! Redirecting to MoodActivity.");

                        // Mark that first quest set was completed today
                        db.markFirstQuestCompleted();

                        // Clear the quest session
                        db.clearCurrentQuestSession();

                        // Restore music
                        MusicManager.restorePreQuestMusic(this);

                        // DIRECTLY GO TO MOOD ACTIVITY
                        Intent intent = new Intent(this, MoodActivity.class);
                        intent.putExtra("flow", "QUEST_COMPLETE");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    } else {
                        // Not all quests done, just go back normally
                        android.util.Log.d(TAG, "Quest complete, but more quests remaining.");
                        finish();
                    }
                })
                .setNegativeButton("Not yet", (dialog, which) -> {
                    Toast.makeText(this, "Keep going! You can do it!", Toast.LENGTH_SHORT).show();
                })
                .setCancelable(false)
                .show();
    }

    // ================= MARK QUEST AS COMPLETED =================
    private void markQuestAsCompleted() {
        DatabaseManager db = DatabaseManager.get(this);

        // Mark quest as done (set progress to 100%)
        db.updateQuestProgress(currentQuest.getId(), 100);

        // Mark quest as rewarded
        db.markQuestRewarded(currentQuest.getId());

        // Add coins
        db.addCoins(currentQuest.getReward());

        Toast.makeText(this, "+" + currentQuest.getReward() + " coins earned!", Toast.LENGTH_SHORT).show();

        // Return result
        Intent resultIntent = new Intent();
        resultIntent.putExtra("quest_completed", true);
        resultIntent.putExtra("quest_position", questPosition);
        setResult(RESULT_QUEST_COMPLETED, resultIntent);

        // Restore the music that was playing before the quest (only if not all complete)
        // If all complete, music will be restored in showCompletionDialog()
        if (!db.areAllCurrentQuestsComplete()) {
            Log.d(TAG, "Quest completed, restoring previous music");
            MusicManager.restorePreQuestMusic(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Don't pause music here - let MusicManager handle it
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Don't call super.onResume() to avoid BaseActivity resuming background music
        // Quest music should continue playing

        // Just apply title
        applyPetNameToTitle();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy called");

        // Stop timer
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Stop all animations
        stopAllAlerts();

        // Only restore if quest was not completed (user pressed back)
        DatabaseManager db = DatabaseManager.get(this);
        if (!db.areAllCurrentQuestsComplete()) {
            Log.d(TAG, "Activity destroyed without completing all quests, restoring previous music");
            MusicManager.restorePreQuestMusic(this);
        }
    }

    private void applyPetNameToTitle() {
        TextView title = findViewById(R.id.appTitle);
        if (title == null) return;

        DatabaseManager db = DatabaseManager.get(this);
        String name = db.getName();

        if (name != null && !name.trim().isEmpty()) {
            title.setText(name.toUpperCase());
        } else {
            title.setText("ECHO");
        }
    }}