package com.example.virtualcompanion;

public class Quest {

    private int id;
    private String title;
    private String description;
    private int reward;
    private int progress;
    private boolean rewarded;
    private int iconResId;

    // Full constructor
    public Quest(int id, String title, String description, int reward, int iconResId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.reward = reward;
        this.iconResId = iconResId;
        this.progress = 0;
        this.rewarded = false;
    }

    // Constructor with default icon
    public Quest(int id, String title, String description, int reward) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.reward = reward;
        this.iconResId = R.drawable.ic_quests;
        this.progress = 0;
        this.rewarded = false;
    }

    // ================= GETTERS & SETTERS =================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isRewarded() {
        return rewarded;
    }

    public void setRewarded(boolean rewarded) {
        this.rewarded = rewarded;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }
}
