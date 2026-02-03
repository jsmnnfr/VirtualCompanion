package com.example.virtualcompanion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
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

        DatabaseManager db = DatabaseManager.get(this); // Initialize database

        // ================= TESTING ONLY =================
        // Uncomment the line below to reset today's mood and quests every time you open the app
        // db.deleteMoodForToday();
        // db.resetAllQuestProgressForTesting();
        // ================================================

        // ================= TESTING ONLY =================
        // Uncomment the line below to reset customization and re-test name and gender input
        // db.setHasCustomized(false);
        // ================================================

        setContentView(R.layout.activity_opening);

        MusicManager.startMusic(this);


        // Get main layout
        ConstraintLayout mainLayout = findViewById(R.id.main);

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
