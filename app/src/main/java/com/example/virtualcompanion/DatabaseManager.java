package com.example.virtualcompanion;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * DatabaseManager
 *
 * This class:
 * - Reads from database
 * - Writes to database
 * - Avoids SQL everywhere
 */
public class DatabaseManager {

    // Singleton instance (only one DB manager)
    private static DatabaseManager instance;

    private static final String PREFS_NAME = "virtual_companion_prefs";
    private static final String KEY_HAS_CUSTOMIZED = "has_customized";

    private final DatabaseHelper helper;
    private final Context appContext;

    // Private constructor
    private DatabaseManager(Context context) {
        appContext = context.getApplicationContext();
        helper = new DatabaseHelper(appContext);
    }

    /**
     * Get database instance
     */
    public static synchronized DatabaseManager get(Context c) {

        if (instance == null) {
            instance = new DatabaseManager(c.getApplicationContext());
        }

        return instance;
    }

    // ================= DATE HELPER =================

    /**
     * Get today's date as string (YYYY-MM-DD)
     */
    public String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    // ================= USER =================

    /**
     * Get current user/pet name
     */
    public String getName() {

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT name FROM user WHERE id=1",
                null
        );

        String name = "";

        if (c.moveToFirst()) {
            name = c.getString(0);
        }

        c.close();

        return name;
    }

    /**
     * Update user/pet name
     */
    public void setName(String name) {

        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL(
                "UPDATE user SET name=? WHERE id=1",
                new Object[]{name}
        );
    }

    /**
     * Get current coins
     */
    public int getCoins() {

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT coins FROM user WHERE id=1",
                null
        );

        int coins = 0;

        if (c.moveToFirst()) {
            coins = c.getInt(0);
        }

        c.close();

        return coins;
    }

    /**
     * Add / subtract coins
     */
    public void addCoins(int amount) {

        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL(
                "UPDATE user SET coins = coins + ? WHERE id=1",
                new Object[]{amount}
        );
    }

    /**
     * Get pet gender
     */
    public String getGender() {

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT pet_gender FROM user WHERE id=1",
                null
        );

        String gender = "male";

        if (c.moveToFirst()) {
            gender = c.getString(0);
        }

        c.close();

        return gender;
    }

    /**
     * Update pet gender
     */
    public void setGender(String gender) {

        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL(
                "UPDATE user SET pet_gender=? WHERE id=1",
                new Object[]{gender}
        );
    }

    // ================= MOOD =================

    /**
     * Save mood entry
     */
    public void saveMood(int value, String date) {

        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL(
                "INSERT INTO mood(value,date) VALUES(?,?)",
                new Object[]{value, date}
        );
    }

    /**
     * Check if mood was already selected today
     */
    public boolean hasSelectedMoodToday() {

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT id FROM mood WHERE date = ?",
                new String[]{getTodayDate()}
        );

        boolean exists = c.getCount() > 0;
        c.close();
        return exists;
    }

    /**
     * [TESTING ONLY] Delete today's mood selection
     */
    public void deleteMoodForToday() {

        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL(
                "DELETE FROM mood WHERE date = ?",
                new Object[]{getTodayDate()}
        );
    }

    /**
     * [TESTING ONLY]
     * Reset all quest progress
     */
    public void resetAllQuestProgressForTesting() {

        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL(
                "UPDATE quest SET progress = 0, rewarded = 0"
        );
    }

    /**
     * Get latest saved mood (0–4)
     */
    public int getLatestMood() {

        SQLiteDatabase db = helper.getReadableDatabase();

        // Try to get today's mood first
        Cursor c = db.rawQuery(
                "SELECT value FROM mood WHERE date = ? ORDER BY id DESC LIMIT 1",
                new String[]{getTodayDate()}
        );

        if (!c.moveToFirst()) {
            // If no mood today, get the absolute latest one
            c.close();
            c = db.rawQuery(
                    "SELECT value FROM mood ORDER BY id DESC LIMIT 1",
                    null
            );
        }

        int moodIndex = 0; // default = Neutral

        if (c.moveToFirst()) {
            moodIndex = c.getInt(0) - 1; // convert 1–5 → 0–4
        }

        c.close();
        return Math.max(0, Math.min(moodIndex, 4));
    }

    /**
     * Get latest saved mood as text
     */
    public String getLatestMoodText() {
        return moodIndexToText(getLatestMood());
    }

    /**
     * Convert mood index (0–4) to mood text
     */
    private String moodIndexToText(int moodIndex) {
        switch (moodIndex) {
            case 0: return "neutral";
            case 1: return "happy";
            case 2: return "sad";
            case 3: return "angry";
            case 4: return "anxious";
            default: return "neutral";
        }
    }

    // ================= QUEST =================

    /**
     * Get all quests
     */
    public List<Quest> getAllQuests() {

        List<Quest> questList = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT id, title, description, reward, progress, rewarded, mood FROM quest",
                null
        );

        if (c.moveToFirst()) {
            do {
                int id = c.getInt(0);
                String title = c.getString(1);
                String desc = c.getString(2);
                int reward = c.getInt(3);
                int progress = c.getInt(4);
                boolean rewarded = c.getInt(5) == 1;
                String mood = c.getString(6);

                Quest quest = new Quest(id, title, desc, reward, mood);
                quest.setProgress(progress);
                quest.setRewarded(rewarded);
                questList.add(quest);

            } while (c.moveToNext());
        }

        c.close();
        return questList;
    }

    /**
     * Get quests filtered by mood
     */
    public List<Quest> getQuestsForMood(int selectedMood) {

        String moodText = moodIndexToText(selectedMood);

        List<Quest> quests = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT id, title, description, reward, progress, rewarded, mood FROM quest WHERE mood=?",
                new String[]{moodText}
        );

        if (c.moveToFirst()) {
            do {
                int id = c.getInt(0);
                String title = c.getString(1);
                String desc = c.getString(2);
                int reward = c.getInt(3);
                int progress = c.getInt(4);
                boolean rewarded = c.getInt(5) == 1;
                String mood = c.getString(6);

                Quest q = new Quest(id, title, desc, reward, mood);
                q.setProgress(progress);
                q.setRewarded(rewarded);
                quests.add(q);

            } while (c.moveToNext());
        }

        c.close();
        return quests;
    }


    /**
     * Get quest progress
     */
    public int getQuestProgress(int questId) {

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT progress FROM quest WHERE id=?",
                new String[]{String.valueOf(questId)}
        );

        int progress = 0;

        if (c.moveToFirst()) {
            progress = c.getInt(0);
        }

        c.close();
        return progress;
    }

    /**
     * Update quest progress
     */
    public void updateQuestProgress(int questId, int progress) {

        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL(
                "UPDATE quest SET progress=? WHERE id=?",
                new Object[]{progress, questId}
        );
    }

    /**
     * Check if quest is rewarded
     */
    public boolean isQuestRewarded(int questId) {

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT rewarded FROM quest WHERE id=?",
                new String[]{String.valueOf(questId)}
        );

        boolean rewarded = false;

        if (c.moveToFirst()) {
            rewarded = c.getInt(0) == 1;
        }

        c.close();
        return rewarded;
    }

    /**
     * Mark quest as rewarded
     */
    public void markQuestRewarded(int questId) {

        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL(
                "UPDATE quest SET rewarded=1 WHERE id=?",
                new Object[]{questId}
        );
    }

    public int getCompletedQuestCountForMood(String mood) {
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM quest WHERE mood=? AND progress>=100",
                new String[]{mood}
        );

        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    public int getTotalQuestCountForMood(String mood) {
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM quest WHERE mood=?",
                new String[]{mood}
        );

        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    // ================= PREFS =================

    public boolean hasCustomized() {
        return appContext
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_HAS_CUSTOMIZED, false);
    }

    public void setHasCustomized(boolean customized) {
        appContext
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_HAS_CUSTOMIZED, customized)
                .apply();
    }

}
