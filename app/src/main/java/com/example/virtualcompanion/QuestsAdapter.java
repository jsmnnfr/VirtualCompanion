package com.example.virtualcompanion;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class QuestsAdapter extends RecyclerView.Adapter<QuestsAdapter.QuestViewHolder> {

    private List<Quest> questsList;
    private Context context;
    private static final int REQUEST_CODE_QUEST = 1001;

    public QuestsAdapter(List<Quest> questsList) {
        this.questsList = questsList;
    }

    @NonNull
    @Override
    public QuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_quest, parent, false);
        return new QuestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestViewHolder holder, int position) {
        Quest quest = questsList.get(position);

        // Bind basic data
        holder.questIcon.setImageResource(quest.getIconResId());
        holder.questTitle.setText(quest.getTitle());
        holder.questDescription.setText(quest.getDescription());
        holder.questReward.setText("+" + quest.getReward());

        // Progress bar
        holder.progressBar.setProgress(quest.getProgress());

        // Check if quest is completed
        boolean isCompleted = quest.getProgress() >= 100;

        if (isCompleted) {
            // GRAY OUT COMPLETED QUEST
            holder.markDoneBtn.setText("Done");
            holder.markDoneBtn.setEnabled(false);
            holder.markDoneBtn.setBackgroundColor(Color.GRAY);
            holder.markDoneBtn.setTextColor(Color.WHITE);
            holder.markDoneBtn.setAlpha(0.5f);
        } else {
            // ACTIVE QUEST
            holder.markDoneBtn.setText("Start Quest");
            holder.markDoneBtn.setEnabled(true);
            holder.markDoneBtn.setBackgroundColor(Color.parseColor("#4CAF50")); // Green
            holder.markDoneBtn.setTextColor(Color.WHITE);
            holder.markDoneBtn.setAlpha(1.0f);

            holder.markDoneBtn.setOnClickListener(v -> {
                showStartQuestDialog(quest, position);
            });
        }
    }

    // ================= START QUEST DIALOG WITH ACCURATE TIMER =================
    private void showStartQuestDialog(Quest quest, int position) {
        // Format timer text based on minutes (can be fractional)
        double minutes = quest.getTimerMinutes();
        String timerText;

        if (minutes == 0.5) {
            timerText = "30 seconds";
        } else if (minutes == 1.0) {
            timerText = "1 minute";
        } else if (minutes == 1.5) {
            timerText = "1 minute 30 seconds";
        } else if (minutes == 2.0) {
            timerText = "2 minutes";
        } else if (minutes < 1.0) {
            // For any other fractional minute under 1
            int seconds = (int) (minutes * 60);
            timerText = seconds + " seconds";
        } else {
            // For 2+ minutes
            int wholeMinutes = (int) minutes;
            int remainingSeconds = (int) ((minutes - wholeMinutes) * 60);
            if (remainingSeconds == 0) {
                timerText = wholeMinutes + " minutes";
            } else {
                timerText = wholeMinutes + " minute" + (wholeMinutes > 1 ? "s" : "") + " " + remainingSeconds + " seconds";
            }
        }

        new AlertDialog.Builder(context)
                .setTitle("Start Quest")
                .setMessage("Are you ready to start your quest?\n\n" +
                        "" + quest.getTitle() + "\n\n" +
                        "Time: " + timerText + "\n" +
                        "Reward: " + quest.getReward() + " coins")
                .setPositiveButton("Yes, Start!", (dialog, which) -> {
                    // Launch QuestSessionActivity with startActivityForResult
                    Intent intent = new Intent(context, QuestSessionActivity.class);
                    intent.putExtra("quest_id", quest.getId());
                    intent.putExtra("quest_title", quest.getTitle());
                    intent.putExtra("quest_description", quest.getDescription());
                    intent.putExtra("quest_reward", quest.getReward());
                    intent.putExtra("quest_timer", quest.getTimerMinutes());
                    intent.putExtra("quest_mood", quest.getMood());
                    intent.putExtra("quest_position", position);

                    // Use startActivityForResult so we know when to refresh
                    if (context instanceof Activity) {
                        ((Activity) context).startActivityForResult(intent, REQUEST_CODE_QUEST);
                    }
                })
                .setNegativeButton("Not Yet", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return questsList.size();
    }

    // Method to update the list
    public void updateQuests(List<Quest> newQuests) {
        this.questsList = newQuests;
        notifyDataSetChanged();
    }

    // ViewHolder class
    public static class QuestViewHolder extends RecyclerView.ViewHolder {
        ImageView questIcon;
        TextView questTitle;
        TextView questDescription;
        TextView questReward;
        ProgressBar progressBar;
        MaterialButton markDoneBtn;

        public QuestViewHolder(@NonNull View itemView) {
            super(itemView);

            questIcon = itemView.findViewById(R.id.questIcon);
            questTitle = itemView.findViewById(R.id.questTitle);
            questDescription = itemView.findViewById(R.id.questDescription);
            questReward = itemView.findViewById(R.id.questReward);
            progressBar = itemView.findViewById(R.id.questProgressBar);
            markDoneBtn = itemView.findViewById(R.id.markDoneBtn);
        }
    }
}