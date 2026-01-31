package com.example.virtualcompanion;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

    // ================= USER =================

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

        String gender = "neutral";

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
}
