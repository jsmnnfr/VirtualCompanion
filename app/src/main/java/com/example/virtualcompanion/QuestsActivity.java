package com.example.virtualcompanion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuestsActivity extends BaseActivity {

    private RecyclerView questsRecyclerView;
    private QuestsAdapter questsAdapter;
    private DatabaseManager db;
    private ImageView navHome, navQuests, navCustomize, settingsIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quests);

        db = DatabaseManager.get(this);

        initializeViews();
        setupRecyclerView();
        setupNavigation();
        updateCoinDisplay();
    }

    // ================= VIEW BINDING =================
    private void initializeViews() {
        questsRecyclerView = findViewById(R.id.questsRecyclerView);

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

        String mood = db.getLatestMoodText();

        if ("happy".equals(mood)) {
            Intent intent = new Intent(this, MoodResultActivity.class);
            intent.putExtra("flow", "HAPPY_MOOD");
            startActivity(intent);
            finish();
            return;
        }

        List<Quest> quests = db.getQuestsForMood(db.getLatestMood());


        if (quests.isEmpty()) {
            Toast.makeText(this, "No quests for your mood today!", Toast.LENGTH_SHORT).show();
            return;
        }

        questsAdapter = new QuestsAdapter(
                quests,
                this::handleQuestMarkDone
        );

        questsRecyclerView.setAdapter(questsAdapter);
    }

    // ================= QUEST LOGIC =================
    private void handleQuestMarkDone(Quest quest, int position) {

        int currentProgress = db.getQuestProgress(quest.getId());

        if (currentProgress >= 100) {
            Toast.makeText(this, "Quest already completed!", Toast.LENGTH_SHORT).show();
            return;
        }

        int newProgress = Math.min(currentProgress + 100, 100);
        db.updateQuestProgress(quest.getId(), newProgress);
        quest.setProgress(newProgress);

        if (newProgress >= 100 && !db.isQuestRewarded(quest.getId())) {
            db.addCoins(quest.getReward());
            db.markQuestRewarded(quest.getId());
            Toast.makeText(
                    this,
                    "Quest Completed! +" + quest.getReward() + " coins",
                    Toast.LENGTH_SHORT
            ).show();

            // ============= MOOD SESSION ===============

            String moodText = quest.getMood();

            int completed = db.getCompletedQuestCountForMood(moodText);
            int total = db.getTotalQuestCountForMood(moodText);

            if (completed == total) {

                Intent intent = new Intent(this, MoodActivity.class);
                intent.putExtra("flow", "QUEST_COMPLETE");

                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                return;
            }

        }

        questsAdapter.notifyItemChanged(position);
        updateCoinDisplay();
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
                startActivity(new Intent(this, MoodResultActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        if (navQuests != null) {
            navQuests.setOnClickListener(v -> {
                // Current screen → do nothing
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

        if (coinAmount != null && db != null) {
            coinAmount.setText(String.valueOf(db.getCoins()));

            // DEV shortcut
            coinAmount.setOnLongClickListener(v -> {
                db.addCoins(100);
                updateCoinDisplay();
                Toast.makeText(this, "[DEV] +100 coins added", Toast.LENGTH_SHORT).show();
                return true;
            });
        }
    }
}
