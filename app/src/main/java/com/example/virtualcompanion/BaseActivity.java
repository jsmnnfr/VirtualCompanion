package com.example.virtualcompanion;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();

        // IMPORTANT: Only resume music if NOT in a quest session
        // QuestSessionActivity will handle its own music
        if (!(this instanceof QuestSessionActivity)) {
            // Just resume - don't start fresh
            MusicManager.resumeMusic();
        }

        // Apply pet name to title
        applyPetNameToTitle();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Don't pause here - let MainApplication handle it globally
        // This prevents double-pausing
    }

    private void applyPetNameToTitle() {

        // Keep ECHO in Opening & Settings
        if (this instanceof OpeningActivity ||
                this instanceof SettingsActivity) {
            return;
        }

        TextView title = findViewById(R.id.appTitle);
        if (title == null) return;

        DatabaseManager db = DatabaseManager.get(this);
        String name = db.getName();

        if (name != null && !name.trim().isEmpty()) {

            // FORCE UPPERCASE
            title.setText(name.toUpperCase());

        } else {

            title.setText("ECHO");
        }
    }
}