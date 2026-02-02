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

    private final DatabaseHelper helper;

    // Private constructor
    private DatabaseManager(Context context) {
        helper = new DatabaseHelper(context);
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
        } else {
            // No user found, create default user
            c.close();
            SQLiteDatabase writeDb = helper.getWritableDatabase();
            writeDb.execSQL(
                    "INSERT INTO user (name, coins, pet_gender) " +
                            "VALUES ('Iggy',150,'male');"
            );
            coins = 150;
            return coins;
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
        String today = getTodayDate();

        Cursor c = db.rawQuery(
                "SELECT id FROM mood WHERE date = ?",
                new String[]{today}
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
        db.execSQL("DELETE FROM mood WHERE date = ?", new Object[]{getTodayDate()});
    }

    /**
     * Get latest saved mood (0–4)
     */
    public int getLatestMood() {

        SQLiteDatabase db = helper.getReadableDatabase();

        // Try to get today's mood first
        String today = getTodayDate();
        Cursor c = db.rawQuery(
                "SELECT value FROM mood WHERE date = ? ORDER BY id DESC LIMIT 1",
                new String[]{today}
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


    // ================= QUEST =================
    public List<Quest> getAllQuests() {
        List<Quest> questList = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id, title, description, reward, progress, rewarded FROM quest", null);

        if (c.moveToFirst()) {
            do {
                int id = c.getInt(0);
                String title = c.getString(1);
                String desc = c.getString(2);
                int reward = c.getInt(3);
                int progress = c.getInt(4);
                boolean rewarded = c.getInt(5) == 1;

                Quest quest = new Quest(id, title, desc, reward);
                quest.setProgress(progress);
                quest.setRewarded(rewarded);
                questList.add(quest);
            } while (c.moveToNext());
        }
        c.close();
        return questList;
    }

    public int getQuestProgress(int questId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT progress FROM quest WHERE id=?", new String[]{String.valueOf(questId)});
        int progress = 0;
        if (c.moveToFirst()) progress = c.getInt(0);
        c.close();
        return progress;
    }

    public void updateQuestProgress(int questId, int progress) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("UPDATE quest SET progress=? WHERE id=?", new Object[]{progress, questId});
    }

    public boolean isQuestRewarded(int questId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT rewarded FROM quest WHERE id=?", new String[]{String.valueOf(questId)});
        boolean rewarded = false;
        if (c.moveToFirst()) rewarded = c.getInt(0) == 1;
        c.close();
        return rewarded;
    }

    public void markQuestRewarded(int questId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("UPDATE quest SET rewarded=1 WHERE id=?", new Object[]{questId});
    }
}
