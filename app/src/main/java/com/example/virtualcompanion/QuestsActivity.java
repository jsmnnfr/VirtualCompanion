package com.example.virtualcompanion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        questsAdapter = new QuestsAdapter(
                db.getAllQuests(),
                this::handleQuestMarkDone
        );

        questsRecyclerView.setAdapter(questsAdapter);
    }

    // ================= QUEST LOGIC =================
    private void handleQuestMarkDone(Quest quest, int position) {
        DatabaseManager db = DatabaseManager.get(this);

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
        }

        questsAdapter.notifyItemChanged(position);
        updateCoinDisplay();
    }

    // ================= NAVIGATION =================
    private void setupNavigation() {

        if (settingsIcon != null) {
            settingsIcon.setOnClickListener(v ->
                    startActivity(new Intent(this, SettingsActivity.class))
            );
        }

        if (navHome != null) {
            navHome.setOnClickListener(v ->
                    startActivity(new Intent(this, MoodResultActivity.class))
            );
        }

        if (navQuests != null) {
            navQuests.setOnClickListener(v -> {
                // Current screen → do nothing
            });
        }

        if (navCustomize != null) {
            navCustomize.setOnClickListener(v ->
                    startActivity(new Intent(this, CustomTopActivity.class))
            );
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
