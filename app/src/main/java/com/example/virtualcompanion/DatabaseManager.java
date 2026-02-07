package com.example.virtualcompanion;

import android.content.Context;
import android.content.SharedPreferences;
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
    private static final String KEY_CURRENT_QUEST_IDS = "current_quest_ids";
    private static final String KEY_CURRENT_MOOD = "current_mood";
    private static final String KEY_USED_QUEST_IDS = "used_quest_ids";
    private static final String KEY_QUEST_DATE = "quest_date";
    private static final String KEY_HAPPY_QUEST_DATE = "last_happy_quest_date";
    private static final String KEY_FIRST_QUEST_COMPLETED = "first_quest_completed_today"; // NEW

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

        String today = getTodayDate();

        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM mood WHERE date=?",
                new String[]{today}
        );

        boolean result = false;

        if (c.moveToFirst()) {
            result = c.getInt(0) > 0;
        }

        c.close();

        return result;
    }

    /**
     * Get latest mood value (1-5)
     */
    public int getLatestMood() {

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT value FROM mood ORDER BY id DESC LIMIT 1",
                null
        );

        int mood = 0; // 0 = neutral

        if (c.moveToFirst()) {
            mood = c.getInt(0) - 1; // Convert to 0-based index
        }

        c.close();

        return mood;
    }

    /**
     * Get latest mood as text
     */
    public String getLatestMoodText() {

        int moodIndex = getLatestMood();

        switch (moodIndex) {
            case 0: return "neutral";
            case 1: return "happy";
            case 2: return "sad";
            case 3: return "angry";
            case 4: return "anxious";
            default: return "neutral";
        }
    }

    /**
     * Delete mood for today (for testing)
     */
    public void deleteMoodForToday() {

        SQLiteDatabase db = helper.getWritableDatabase();

        String today = getTodayDate();

        db.execSQL(
                "DELETE FROM mood WHERE date=?",
                new String[]{today}
        );
    }

    // ================= FIRST QUEST COMPLETION TRACKING (PER DAY) =================

    /**
     * Check if user completed ANY quest set today (first use done)
     */
    public boolean hasCompletedFirstQuestToday() {
        SharedPreferences prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String lastCompletionDate = prefs.getString(KEY_FIRST_QUEST_COMPLETED, "");
        String today = getTodayDate();

        boolean completed = lastCompletionDate.equals(today);
        android.util.Log.d("DatabaseManager", "hasCompletedFirstQuestToday: " + completed + " (lastDate=" + lastCompletionDate + ", today=" + today + ")");
        return completed;
    }

    /**
     * Mark that first quest set was completed today (any mood)
     */
    public void markFirstQuestCompleted() {
        SharedPreferences prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String today = getTodayDate();
        prefs.edit().putString(KEY_FIRST_QUEST_COMPLETED, today).apply();
        android.util.Log.d("DatabaseManager", "Marked first quest completed for today: " + today);
    }

    // ================= HAPPY MOOD TRACKING (LEGACY - DEPRECATED) =================

    /**
     * Check if user already completed Happy mood quests today
     * @deprecated Use hasCompletedFirstQuestToday() instead
     */
    @Deprecated
    public boolean hasCompletedHappyQuestsToday() {
        // Now just checks if first quest was completed today
        return hasCompletedFirstQuestToday();
    }

    /**
     * Mark that Happy quests were completed today
     * @deprecated Use markFirstQuestCompleted() instead
     */
    @Deprecated
    public void markHappyQuestsCompleted() {
        // Now just marks first quest as completed
        markFirstQuestCompleted();
    }

    // ================= QUEST SESSION MANAGEMENT WITH DAILY RESET =================

    /**
     * Get quests for current session (persists until all complete)
     * Excludes used quests TODAY ONLY - resets tomorrow
     */
    public List<Quest> getQuestsForMood(int moodIndex) {
        String moodText = getMoodTextFromIndex(moodIndex);
        String today = getTodayDate();

        SharedPreferences prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedMood = prefs.getString(KEY_CURRENT_MOOD, "");
        String savedIds = prefs.getString(KEY_CURRENT_QUEST_IDS, "");
        String savedDate = prefs.getString(KEY_QUEST_DATE, "");

        // Check if date changed - if so, reset everything
        if (!savedDate.equals(today)) {
            android.util.Log.d("DatabaseManager", "New day detected! Resetting quest history.");
            clearAllQuestHistory();
            savedMood = "";
            savedIds = "";
        }

        // If mood changed or no saved quests, generate new random 5
        if (!savedMood.equals(moodText) || savedIds.isEmpty()) {
            // Get previously used quest IDs for this mood TODAY
            String usedIds = prefs.getString(KEY_USED_QUEST_IDS + "_" + moodText, "");

            List<Quest> newQuests = generateRandomQuestsExcluding(moodText, 5, usedIds);
            saveCurrentQuestSession(moodText, newQuests, today);

            // Add these quest IDs to used history
            addToUsedQuests(moodText, newQuests);

            return newQuests;
        }

        // Load saved quests by IDs
        return loadQuestsByIds(savedIds, moodText);
    }

    /**
     * Generate new random quests, excluding previously used ones TODAY
     */
    private List<Quest> generateRandomQuestsExcluding(String mood, int count, String excludeIds) {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<Quest> quests = new ArrayList<>();

        String excludeClause = "";
        if (!excludeIds.isEmpty()) {
            excludeClause = " AND id NOT IN (" + excludeIds + ")";
        }

        Cursor c = db.rawQuery(
                "SELECT id, title, description, reward, timer_minutes, progress, rewarded " +
                        "FROM quest WHERE mood=?" + excludeClause + " ORDER BY RANDOM() LIMIT ?",
                new String[]{mood, String.valueOf(count)}
        );

        while (c.moveToNext()) {
            int id = c.getInt(0);
            String title = c.getString(1);
            String description = c.getString(2);
            int reward = c.getInt(3);
            int timerMinutes = c.getInt(4);
            int progress = c.getInt(5);
            int rewarded = c.getInt(6);

            Quest quest = new Quest(id, title, description, reward, mood, timerMinutes);
            quest.setProgress(progress);
            quest.setRewarded(rewarded == 1);

            quests.add(quest);
        }

        c.close();

        // If not enough quests available (all have been used today), reset today's history
        if (quests.size() < count && !excludeIds.isEmpty()) {
            android.util.Log.d("DatabaseManager", "All quests used today. Resetting history for " + mood);
            clearUsedQuestsForMood(mood);
            return generateRandomQuestsExcluding(mood, count, "");
        }

        return quests;
    }

    /**
     * Load quests by their IDs
     */
    private List<Quest> loadQuestsByIds(String ids, String mood) {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<Quest> quests = new ArrayList<>();

        Cursor c = db.rawQuery(
                "SELECT id, title, description, reward, timer_minutes, progress, rewarded " +
                        "FROM quest WHERE id IN (" + ids + ") ORDER BY id",
                null
        );

        while (c.moveToNext()) {
            int id = c.getInt(0);
            String title = c.getString(1);
            String description = c.getString(2);
            int reward = c.getInt(3);
            int timerMinutes = c.getInt(4);
            int progress = c.getInt(5);
            int rewarded = c.getInt(6);

            Quest quest = new Quest(id, title, description, reward, mood, timerMinutes);
            quest.setProgress(progress);
            quest.setRewarded(rewarded == 1);

            quests.add(quest);
        }

        c.close();
        return quests;
    }

    /**
     * Save current quest session WITH DATE
     */
    private void saveCurrentQuestSession(String mood, List<Quest> quests, String date) {
        StringBuilder ids = new StringBuilder();
        for (int i = 0; i < quests.size(); i++) {
            ids.append(quests.get(i).getId());
            if (i < quests.size() - 1) {
                ids.append(",");
            }
        }

        SharedPreferences prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_CURRENT_MOOD, mood)
                .putString(KEY_CURRENT_QUEST_IDS, ids.toString())
                .putString(KEY_QUEST_DATE, date)
                .apply();

        android.util.Log.d("DatabaseManager", "Saved quest session: mood=" + mood + ", ids=" + ids.toString() + ", date=" + date);
    }

    /**
     * Add quest IDs to used history for a mood TODAY
     */
    private void addToUsedQuests(String mood, List<Quest> quests) {
        SharedPreferences prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String key = KEY_USED_QUEST_IDS + "_" + mood;
        String existingIds = prefs.getString(key, "");

        StringBuilder newIds = new StringBuilder(existingIds);
        for (Quest quest : quests) {
            if (newIds.length() > 0) {
                newIds.append(",");
            }
            newIds.append(quest.getId());
        }

        prefs.edit().putString(key, newIds.toString()).apply();
        android.util.Log.d("DatabaseManager", "Added to used quests for " + mood + " today: " + newIds.toString());
    }

    /**
     * Clear used quests for a specific mood
     */
    private void clearUsedQuestsForMood(String mood) {
        SharedPreferences prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String key = KEY_USED_QUEST_IDS + "_" + mood;
        prefs.edit().remove(key).apply();
        android.util.Log.d("DatabaseManager", "Cleared used quest history for " + mood);
    }

    /**
     * Clear current quest session (call after completing all 5)
     */
    public void clearCurrentQuestSession() {
        SharedPreferences prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .remove(KEY_CURRENT_MOOD)
                .remove(KEY_CURRENT_QUEST_IDS)
                .apply();

        android.util.Log.d("DatabaseManager", "Cleared quest session");
    }

    /**
     * Clear ALL quest history (called on new day or reset)
     */
    public void clearAllQuestHistory() {
        SharedPreferences prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Clear used quest IDs for all moods
        editor.remove(KEY_USED_QUEST_IDS + "_neutral");
        editor.remove(KEY_USED_QUEST_IDS + "_happy");
        editor.remove(KEY_USED_QUEST_IDS + "_sad");
        editor.remove(KEY_USED_QUEST_IDS + "_angry");
        editor.remove(KEY_USED_QUEST_IDS + "_anxious");

        // Clear current session
        editor.remove(KEY_CURRENT_MOOD);
        editor.remove(KEY_CURRENT_QUEST_IDS);
        editor.remove(KEY_QUEST_DATE);

        // Clear happy quest date (reset daily) - LEGACY
        editor.remove(KEY_HAPPY_QUEST_DATE);

        // Clear first quest completion flag (reset daily)
        editor.remove(KEY_FIRST_QUEST_COMPLETED);

        editor.apply();
        android.util.Log.d("DatabaseManager", "Cleared all quest history");
    }

    /**
     * Check if all current quests are complete
     */
    public boolean areAllCurrentQuestsComplete() {
        SharedPreferences prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedIds = prefs.getString(KEY_CURRENT_QUEST_IDS, "");

        if (savedIds.isEmpty()) {
            return false;
        }

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM quest WHERE id IN (" + savedIds + ") AND progress >= 100",
                null
        );

        int completedCount = 0;
        if (c.moveToFirst()) {
            completedCount = c.getInt(0);
        }
        c.close();

        // Count total quests in session
        String[] idArray = savedIds.split(",");
        int totalCount = idArray.length;

        return completedCount == totalCount;
    }

    /**
     * Convert mood index to text
     */
    private String getMoodTextFromIndex(int index) {
        switch (index) {
            case 0: return "neutral";
            case 1: return "happy";
            case 2: return "sad";
            case 3: return "angry";
            case 4: return "anxious";
            default: return "neutral";
        }
    }

    /**
     * Get progress of a specific quest
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

    /**
     * Get completed quest count for a mood
     */
    public int getCompletedQuestCountForMood(String mood) {

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM quest WHERE mood=? AND progress>=100",
                new String[]{mood}
        );

        int count = 0;

        if (c.moveToFirst()) {
            count = c.getInt(0);
        }

        c.close();

        return count;
    }

    /**
     * Get total quest count for a mood (currently selected 5)
     */
    public int getTotalQuestCountForMood(String mood) {
        // Return 5 because we always select exactly 5 quests per cycle
        return 5;
    }

    /**
     * Reset all quest progress (for testing)
     */
    public void resetAllQuestProgressForTesting() {

        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL("UPDATE quest SET progress=0, rewarded=0");
    }

    // ================= CUSTOMIZATION =================

    /**
     * Check if user has customized their pet
     */
    public boolean hasCustomized() {

        SharedPreferences prefs = appContext.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
        );

        return prefs.getBoolean(KEY_HAS_CUSTOMIZED, false);
    }

    /**
     * Mark user as having customized
     */
    public void setHasCustomized(boolean hasCustomized) {

        SharedPreferences prefs = appContext.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
        );

        prefs.edit()
                .putBoolean(KEY_HAS_CUSTOMIZED, hasCustomized)
                .apply();
    }

    // ================= ACCESSORY RESET =================

    /**
     * Reset all accessories to unowned and unequipped (for testing)
     */
    public void resetAllAccessories() {
        try {
            SQLiteDatabase db = helper.getWritableDatabase();

            // Reset all accessories to not owned and not equipped
            db.execSQL("UPDATE accessory SET owned=0, equipped=0");
        } catch (Exception e) {
            // Table doesn't exist yet, ignore
            android.util.Log.d("DatabaseManager", "Accessory table not found: " + e.getMessage());
        }
    }

    /**
     * Reset specific accessory category (top, bottom, hat, glasses)
     */
    public void resetAccessoryCategory(String category) {
        try {
            SQLiteDatabase db = helper.getWritableDatabase();

            db.execSQL(
                    "UPDATE accessory SET owned=0, equipped=0 WHERE type=?",
                    new String[]{category}
            );
        } catch (Exception e) {
            // Table doesn't exist yet, ignore
            android.util.Log.d("DatabaseManager", "Accessory table not found: " + e.getMessage());
        }
    }

    // ================= RESET INVENTORY & OUTFIT (SharedPreferences) =================

    /**
     * Reset all inventory and outfit data stored in SharedPreferences (for testing)
     * This clears InventoryManager and OutfitManager data
     */
    public void resetInventoryAndOutfit() {
        android.util.Log.d("DatabaseManager", "========== STARTING RESET ==========");

        // Reset InventoryManager (items owned) - "inventory_data"
        try {
            SharedPreferences invPrefs = appContext.getSharedPreferences("inventory_data", Context.MODE_PRIVATE);
            boolean cleared = invPrefs.edit().clear().commit(); // Use commit() to ensure it's immediate
            android.util.Log.d("DatabaseManager", "InventoryManager cleared: " + cleared);

            // Verify it's empty
            int remaining = invPrefs.getAll().size();
            android.util.Log.d("DatabaseManager", "Inventory items remaining: " + remaining);
        } catch (Exception e) {
            android.util.Log.e("DatabaseManager", "Error clearing InventoryManager: " + e.getMessage());
        }

        // Reset OutfitManager (items equipped) - "outfit_data"
        try {
            SharedPreferences outfitPrefs = appContext.getSharedPreferences("outfit_data", Context.MODE_PRIVATE);
            boolean cleared = outfitPrefs.edit().clear().commit(); // Use commit() to ensure it's immediate
            android.util.Log.d("DatabaseManager", "OutfitManager cleared: " + cleared);

            // Verify it's empty
            int remaining = outfitPrefs.getAll().size();
            android.util.Log.d("DatabaseManager", "Outfit items remaining: " + remaining);
        } catch (Exception e) {
            android.util.Log.e("DatabaseManager", "Error clearing OutfitManager: " + e.getMessage());
        }

        android.util.Log.d("DatabaseManager", "========== RESET COMPLETE ==========");
    }

    /**
     * Nuclear option: Clear ALL SharedPreferences (for testing)
     */
    public void resetAllSharedPreferences() {
        android.util.Log.d("DatabaseManager", "========== CLEARING ALL SHAREDPREFS ==========");

        String[] prefsNames = {
                "inventory_data",
                "outfit_data",
                "virtual_companion_prefs",
                "inventory_prefs",
                "outfit_prefs"
        };

        for (String name : prefsNames) {
            try {
                SharedPreferences prefs = appContext.getSharedPreferences(name, Context.MODE_PRIVATE);
                boolean cleared = prefs.edit().clear().commit();
                android.util.Log.d("DatabaseManager", "Cleared " + name + ": " + cleared);
            } catch (Exception e) {
                android.util.Log.e("DatabaseManager", "Error clearing " + name + ": " + e.getMessage());
            }
        }

        // Also clear quest session and history
        clearCurrentQuestSession();
        clearAllQuestHistory();

        android.util.Log.d("DatabaseManager", "========== ALL PREFS CLEARED ==========");
    }
}