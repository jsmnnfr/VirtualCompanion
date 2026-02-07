package com.example.virtualcompanion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuestsActivity extends BaseActivity {

    private RecyclerView questsRecyclerView;
    private QuestsAdapter questsAdapter;
    private DatabaseManager db;
    private ImageView navHome, navQuests, navCustomize, settingsIcon;
    private TextView emptyStateMessage;
    private int moodIndex;

    private static final int REQUEST_CODE_QUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quests);

        db = DatabaseManager.get(this);

        // Get mood index from intent or fallback to latest mood
        moodIndex = getIntent().getIntExtra("selected_mood", -1);
        if (moodIndex == -1) {
            moodIndex = db.getLatestMood();
        }

        initializeViews();
        setupRecyclerView();
        setupNavigation();
        updateCoinDisplay();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_QUEST) {
            if (resultCode == QuestSessionActivity.RESULT_QUEST_COMPLETED) {
                refreshQuestList();
                updateCoinDisplay();
                Toast.makeText(this, "Quest completed!", Toast.LENGTH_SHORT).show();
            } else {
                refreshQuestList();
                updateCoinDisplay();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshQuestList();
        updateCoinDisplay();
    }

    // ================= VIEW BINDING =================
    private void initializeViews() {
        questsRecyclerView = findViewById(R.id.questsRecyclerView);
        emptyStateMessage = findViewById(R.id.emptyStateMessage);
        navHome = findViewById(R.id.navHome);
        navQuests = findViewById(R.id.navQuests);
        navCustomize = findViewById(R.id.navCustomize);
        settingsIcon = findViewById(R.id.settingsIcon);
    }

    // ================= RECYCLER VIEW =================
    private void setupRecyclerView() {
        if (questsRecyclerView == null) {
            Toast.makeText(this, "Quest list unavailable", Toast.LENGTH_LONG).show();
            return;
        }

        questsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Check if Happy mood AND first quest already completed today
        if (moodIndex == 1 && db.hasCompletedFirstQuestToday()) {
            // Show empty state for Happy mood (after first quest completion today)
            showEmptyState("You’re all done for today! Keep this happiness close!");
            return;
        }

        // Get quests for current mood (including happy on first time today)
        List<Quest> quests = db.getQuestsForMood(moodIndex);

        if (quests.isEmpty()) {
            showEmptyState("No quests available for your mood today!");
            return;
        }

        // Hide empty state and show quest list
        if (emptyStateMessage != null) {
            emptyStateMessage.setVisibility(View.GONE);
        }
        questsRecyclerView.setVisibility(View.VISIBLE);

        questsAdapter = new QuestsAdapter(quests);
        questsRecyclerView.setAdapter(questsAdapter);
    }

    private void showEmptyState(String message) {
        questsRecyclerView.setVisibility(View.GONE);

        if (emptyStateMessage != null) {
            emptyStateMessage.setText(message);
            emptyStateMessage.setVisibility(View.VISIBLE);
        } else {
            // Fallback if TextView not in layout
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    private void refreshQuestList() {
        if (db != null) {
            // Check if Happy mood AND first quest already completed today
            if (moodIndex == 1 && db.hasCompletedFirstQuestToday()) {
                showEmptyState("No quests available today!  Cherish your mood! \n Enjoy your day and check back tomorrow!");
                return;
            }


            List<Quest> updatedQuests = db.getQuestsForMood(moodIndex);

            if (updatedQuests.isEmpty()) {
                showEmptyState("No quests available for your mood today!");
            } else {
                // Hide empty state and show quest list
                if (emptyStateMessage != null) {
                    emptyStateMessage.setVisibility(View.GONE);
                }
                questsRecyclerView.setVisibility(View.VISIBLE);

                if (questsAdapter != null) {
                    questsAdapter.updateQuests(updatedQuests);
                }
            }
        }
    }

    // ================= NAVIGATION =================
    private void setupNavigation() {
        if (settingsIcon != null) {
            settingsIcon.setOnClickListener(v -> {
                startActivity(new Intent(this, SettingsActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Intent intent = new Intent(this, MoodResultActivity.class);
                intent.putExtra("selected_mood", moodIndex);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        if (navQuests != null) {
            navQuests.setOnClickListener(v -> {
                // Current screen - do nothing
            });
        }

        if (navCustomize != null) {
            navCustomize.setOnClickListener(v -> {
                startActivity(new Intent(this, CustomTopActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
    }

    // ================= COINS =================
    private void updateCoinDisplay() {
        android.widget.TextView coinAmount = findViewById(R.id.coinAmount);
        if (coinAmount != null) {
            try {
                int coins = db.getCoins();
                coinAmount.setText(String.valueOf(coins));
            } catch (Exception e) {
                coinAmount.setText("0");
            }

            // DEV cheat mode
            coinAmount.setOnLongClickListener(v -> {
                db.addCoins(100);
                updateCoinDisplay();
                Toast.makeText(this, "[DEV] +100 coins added", Toast.LENGTH_SHORT).show();
                return true;
            });
        }
    }
}