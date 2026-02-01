package com.example.virtualcompanion;

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
    private OnQuestClickListener listener;

    // Interface for click events
    public interface OnQuestClickListener {
        void onQuestClick(Quest quest, int position);
    }

    public QuestsAdapter(List<Quest> questsList) {
        this.questsList = questsList;
    }

    public QuestsAdapter(List<Quest> questsList, OnQuestClickListener listener) {
        this.questsList = questsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
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

        // Progress support
        holder.progressBar.setProgress(quest.getProgress());

        // Button state
        holder.markDoneBtn.setEnabled(quest.getProgress() < 100);

        holder.markDoneBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuestClick(quest, position);
            }
        });
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
