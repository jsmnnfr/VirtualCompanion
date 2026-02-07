package com.example.virtualcompanion;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class OpeningActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable fullscreen
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        android.util.Log.d("OpeningActivity", "========== APP RESTART - TESTING MODE ==========");

        // ================= TESTING MODE - FORCE COMPLETE RESET =================
        // Step 1: Delete the entire database
        boolean dbDeleted = this.deleteDatabase("virtual_companion.db");
        android.util.Log.d("OpeningActivity", "Database deleted: " + dbDeleted);

        // Step 2: Initialize database (creates fresh tables)
        DatabaseManager db = DatabaseManager.get(this);
        android.util.Log.d("OpeningActivity", "Database recreated");

        // Step 3: Reset database data
        db.deleteMoodForToday();
        db.resetAllQuestProgressForTesting();
        db.setHasCustomized(false);
        db.resetAllAccessories();
        android.util.Log.d("OpeningActivity", "Database data reset");

        // Step 4: NUCLEAR OPTION - Clear ALL SharedPreferences
        db.resetAllSharedPreferences(); // NEW: Use nuclear option
        android.util.Log.d("OpeningActivity", "All SharedPreferences cleared");

        android.util.Log.d("OpeningActivity", "========== RESET COMPLETE ==========");
        // =======================================================================

        setContentView(R.layout.activity_opening);

        MusicManager.startMusic(this);

        // uncomment this if testing phase done
        // =======================================================================
        // DatabaseManager db = DatabaseManager.get(this);
        // =======================================================================

        // Get main layout
        ConstraintLayout mainLayout = findViewById(R.id.main);

        // ================= BLINKING "TAP ANYWHERE" TEXT =================
        TextView tapAnywhereText = findViewById(R.id.tapAnywhereText);

        if (tapAnywhereText != null) {
            // Create blink animation
            Animation blinkAnimation = new AlphaAnimation(0.4f, 1.0f); // From 40% to 100% opacity
            blinkAnimation.setDuration(1000); // 1 second per blink
            blinkAnimation.setRepeatMode(Animation.REVERSE); // Fade in, then fade out
            blinkAnimation.setRepeatCount(Animation.INFINITE); // Blink forever

            // Start animation
            tapAnywhereText.startAnimation(blinkAnimation);
        }
        // =================================================================

        // Click anywhere to continue
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Go to Customize screen (new users only)
                Intent intent;
                if (!db.hasCustomized()) {
                    intent = new Intent(
                            OpeningActivity.this,
                            CustomizeActivity.class
                    );
                } else if (db.hasSelectedMoodToday()) {
                    intent = new Intent(
                            OpeningActivity.this,
                            MoodResultActivity.class
                    );
                } else {
                    intent = new Intent(
                            OpeningActivity.this,
                            MoodActivity.class
                    );
                }

                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish(); // prevent going back
            }
        });
    }
}