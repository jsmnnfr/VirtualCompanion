package com.example.virtualcompanion;

public class Quest {

    private int id;
    private String title;
    private String description;
    private int reward;
    private int progress;
    private boolean rewarded;
    private int iconResId;
    private String mood;
    private int timerMinutes; // NEW: Timer duration in minutes

    // Full constructor with timer
    public Quest(int id, String title, String description, int reward, String mood, int iconResId, int timerMinutes) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.reward = reward;
        this.iconResId = iconResId;
        this.mood = mood;
        this.timerMinutes = timerMinutes;
        this.progress = 0;
        this.rewarded = false;
    }

    // Constructor with timer, default icon
    public Quest(int id, String title, String description, int reward, String mood, int timerMinutes) {
        this(id, title, description, reward, mood, R.drawable.ic_quests, timerMinutes);
    }

    // Constructor with default icon and timer
    public Quest(int id, String title, String description, int reward, String mood) {
        this(id, title, description, reward, mood, R.drawable.ic_quests, 5); // Default 5 minutes
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

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public int getTimerMinutes() {
        return timerMinutes;
    }

    public void setTimerMinutes(int timerMinutes) {
        this.timerMinutes = timerMinutes;
    }
}