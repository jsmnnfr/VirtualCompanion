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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;

public class QuestSessionActivity extends BaseActivity {

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

        initializeViews();
        setupBackButton();
        loadQuestData();
        setupBackground();
        setupMoodAnimations();
        startQuestSession();

        // Initialize vibrator
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
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
                        finish();
                    })
                    .setNegativeButton("Continue Quest", null)
                    .show();
        });
    }

    // ================= LOAD QUEST =================
    private void loadQuestData() {
        Intent intent = getIntent();

        int questId = intent.getIntExtra("quest_id", -1);
        String questTitle = intent.getStringExtra("quest_title");
        String questDescription = intent.getStringExtra("quest_description");
        int questReward = intent.getIntExtra("quest_reward", 0);
        int questTimer = intent.getIntExtra("quest_timer", 5);
        questMood = intent.getStringExtra("quest_mood");
        questPosition = intent.getIntExtra("quest_position", 0);

        if (questId == -1) {
            Toast.makeText(this, "Error loading quest", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentQuest = new Quest(questId, questTitle, questDescription, questReward, questMood, questTimer);

        questTitleText.setText(questTitle);
        questDescriptionText.setText(questDescription);
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

    // ================= STOP ALL ALERTS (VIBRATION + FLASH) =================
    private void stopAllAlerts() {
        // Stop vibration
        if (vibrator != null) {
            vibrator.cancel();
        }

        // Stop flash animations
        if (timerFlashAnimator != null && timerFlashAnimator.isRunning()) {
            timerFlashAnimator.cancel();
        }
        if (petFlashAnimator != null && petFlashAnimator.isRunning()) {
            petFlashAnimator.cancel();
        }

        // Clear any remaining animations
        timerText.clearAnimation();
        petDisplay.clearAnimation();

        // Reset alpha to fully visible
        timerText.setAlpha(1f);
        petDisplay.setAlpha(1f);
    }

    // ================= FLASH SCREEN =================
    private void flashScreen() {
        // Flash timer text
        timerFlashAnimator = ObjectAnimator.ofFloat(timerText, "alpha", 1f, 0.2f, 1f);
        timerFlashAnimator.setDuration(500);
        timerFlashAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        timerFlashAnimator.start();

        // Flash pet display
        petFlashAnimator = ObjectAnimator.ofFloat(petDisplay, "alpha", 1f, 0.5f, 1f);
        petFlashAnimator.setDuration(500);
        petFlashAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        petFlashAnimator.start();
    }

    // ================= COMPLETION DIALOG =================
    private void showCompletionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Quest Check")
                .setMessage("Have you completed this task?\n\n\"" + currentQuest.getTitle() + "\"")
                .setPositiveButton("Yes, Done!", (dialog, which) -> {
                    completeQuest();
                })
                .setNegativeButton("Not Yet", (dialog, which) -> {
                    restartTimer();
                })
                .setCancelable(false)
                .show();
    }

    // ================= RESTART TIMER =================
    private void restartTimer() {
        // STOP ALL ALERTS (VIBRATION + FLASHING)
        stopAllAlerts();

        Toast.makeText(this, "Take your time. Timer restarted.", Toast.LENGTH_SHORT).show();
        actionButton.setEnabled(false);
        actionButton.setText("In Progress...");
        instructionText.setText("Keep going, you can do it!");

        long timerDuration = currentQuest.getTimerMinutes() * 60 * 1000;
        startCountdown(timerDuration);
    }

    // ================= COMPLETE QUEST =================
    private void completeQuest() {
        DatabaseManager db = DatabaseManager.get(this);

        int currentProgress = db.getQuestProgress(currentQuest.getId());

        if (currentProgress >= 100) {
            Toast.makeText(this, "Quest already completed!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_QUEST_COMPLETED);
            finish();
            return;
        }

        // Mark quest as complete
        db.updateQuestProgress(currentQuest.getId(), 100);

        if (!db.isQuestRewarded(currentQuest.getId())) {
            db.addCoins(currentQuest.getReward());
            db.markQuestRewarded(currentQuest.getId());

            Toast.makeText(
                    this,
                    "Quest Completed! +" + currentQuest.getReward() + " coins",
                    Toast.LENGTH_LONG
            ).show();
        }

        // Check if all quests complete
        if (db.areAllCurrentQuestsComplete()) {
            // Clear session so new 5 quests generated next time
            db.clearCurrentQuestSession();

            // Reset all quest progress for next cycle
            db.resetAllQuestProgressForTesting();

            // MARK FIRST QUEST COMPLETION FOR TODAY (ANY MOOD)
            db.markFirstQuestCompleted();
            android.util.Log.d("QuestSession", "Marked first quest set completed for today");

            // Go to mood selection
            Intent intent = new Intent(this, MoodActivity.class);
            intent.putExtra("flow", "QUEST_COMPLETE");
            startActivity(intent);
            finish();
        } else {
            // Return to quest list
            setResult(RESULT_QUEST_COMPLETED);
            finish();
        }
    }

    // ================= CLEANUP =================
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        stopAllAlerts();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Leave Quest?")
                .setMessage("Are you sure you want to leave this quest session?")
                .setPositiveButton("Yes, Leave", (dialog, which) -> {
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    stopAllAlerts();
                    super.onBackPressed();
                })
                .setNegativeButton("Stay", null)
                .show();
    }
}