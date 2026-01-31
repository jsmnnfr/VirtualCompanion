package com.example.virtualcompanion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class QuestsActivity extends BaseActivity {

    private RecyclerView questsRecyclerView;
    private QuestsAdapter questsAdapter;
    private List<Quest> questsList;

    private ImageView navHome, navQuests, navCustomize, settingsIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_quests);

        // Initialize views
        initializeViews();

        // Safety check
        if (questsRecyclerView == null) {
            Toast.makeText(this,
                    "RecyclerView not found in layout",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Setup RecyclerView
        setupRecyclerView();

        // Load quests
        loadQuests();

        // Setup navigation
        setupBottomNavigation();
        setupSettingsButton();
        
        // Update coin display
        updateCoinDisplay();
    }

    // Find all views
    private void initializeViews() {

        questsRecyclerView = findViewById(R.id.questsRecyclerView);

        navHome = findViewById(R.id.navHome);
        navQuests = findViewById(R.id.navQuests);
        navCustomize = findViewById(R.id.navCustomize);

        settingsIcon = findViewById(R.id.settingsIcon);
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

    // Setup RecyclerView + Adapter
    private void setupRecyclerView() {

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this);

        questsRecyclerView.setLayoutManager(layoutManager);

        questsList = new ArrayList<>();

        questsAdapter = new QuestsAdapter(
                questsList,
                (quest, position) -> {

                    Toast.makeText(
                            QuestsActivity.this,
                            "Clicked: " + quest.getTitle(),
                            Toast.LENGTH_SHORT
                    ).show();

                }
        );

        questsRecyclerView.setAdapter(questsAdapter);
    }

    // Load sample quests
    private void loadQuests() {

        questsList.clear();

        questsList.add(new Quest(
                "Breathing Exercise",
                "Take 5 slow deep breaths and relax your shoulders.",
                50,
                R.drawable.ic_quests
        ));

        questsList.add(new Quest(
                "Drink Water",
                "Drink one full glass of water.",
                30,
                R.drawable.ic_quests
        ));

        questsList.add(new Quest(
                "Stretch Break",
                "Stand up and stretch your arms for 2 minutes.",
                40,
                R.drawable.ic_quests
        ));

        questsList.add(new Quest(
                "Gratitude Note",
                "Write one thing you are thankful for today.",
                60,
                R.drawable.ic_quests
        ));

        questsList.add(new Quest(
                "Mindful Pause",
                "Close your eyes and breathe slowly for 1 minute.",
                25,
                R.drawable.ic_quests
        ));

        questsAdapter.notifyDataSetChanged();
    }

    // Bottom Navigation
    private void setupBottomNavigation() {

        if (navHome == null || navQuests == null || navCustomize == null) {
            return;
        }

        // Home → Mood
        navHome.setOnClickListener(v -> {

            Intent intent = new Intent(
                    QuestsActivity.this,
                    MoodResultActivity.class
            );

            startActivity(intent);
            // ❌ No finish()
        });

        // Quests (Current Screen)
        navQuests.setOnClickListener(v -> {
            // Stay here
        });

        // Customize → CustomTopActivity
        navCustomize.setOnClickListener(v -> {

            Intent intent = new Intent(
                    QuestsActivity.this,
                    CustomTopActivity.class
            );

            startActivity(intent);
            // ❌ No finish()
        });
    }

    // Settings Button
    private void setupSettingsButton() {

        if (settingsIcon == null) {
            return;
        }

        settingsIcon.setOnClickListener(v -> {

            Intent intent = new Intent(
                    QuestsActivity.this,
                    SettingsActivity.class
            );

            startActivity(intent);
        });
    }
}
