package com.example.virtualcompanion;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DatabaseHelper
 *
 * This class:
 * - Creates the database
 * - Creates all tables
 * - Handles upgrades
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Database file name
    private static final String DB_NAME = "virtual_companion.db";

    // Change this if you modify tables later
    private static final int DB_VERSION = 3;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Called automatically when DB is created first time
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // ================= USER TABLE =================
        // Stores main player data
        db.execSQL(
                "CREATE TABLE user (" +

                        // Unique ID (auto-generated)
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        // User / pet name (cannot be empty)
                        "name TEXT NOT NULL, " +
                        // User coins (default 0)
                        "coins INTEGER NOT NULL DEFAULT 0, " +
                        // Pet gender (only allowed values)
                        "pet_gender TEXT NOT NULL CHECK " +
                        "(pet_gender IN ('male','female'))" +
                        ");"
        );

        // ================= ACCESSORY TABLE =================
        // Stores shop & equipped items
        db.execSQL(
                "CREATE TABLE accessory (" +

                        // Unique ID
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        // Drawable resource ID
                        "image INTEGER NOT NULL, " +
                        // Item price
                        "price INTEGER NOT NULL, " +
                        // Category
                        "type TEXT NOT NULL CHECK " +
                        "(type IN ('top','bottom','hat','glasses')), " +
                        // 0 = not owned, 1 = owned
                        "owned INTEGER NOT NULL DEFAULT 0 CHECK (owned IN (0,1)), " +
                        // 0 = not equipped, 1 = equipped
                        "equipped INTEGER NOT NULL DEFAULT 0 CHECK (equipped IN (0,1))" +
                        ");"
        );

        // ================= QUEST TABLE =================
        // Stores quest progress
        db.execSQL(
                "CREATE TABLE quest (" +

                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        // Quest title
                        "title TEXT NOT NULL, " +
                        // Quest description
                        "description TEXT, " +
                        // Coins reward
                        "reward INTEGER NOT NULL DEFAULT 0, " +
                        "progress INTEGER NOT NULL DEFAULT 0, " +
                        // 0 = not done, 1 = done
                        "rewarded INTEGER NOT NULL DEFAULT 0 CHECK (rewarded IN (0,1))" +
                        ");"
        );

        // ================= MOOD TABLE =================
        // Stores mood history
        db.execSQL(
                "CREATE TABLE mood (" +

                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        // Mood value (1 to 5 only)
                        "value INTEGER NOT NULL CHECK (value BETWEEN 1 AND 5), " +
                        // Date string
                        "date TEXT NOT NULL" +
                        ");"
        );

        // Insert default values
        insertDefaults(db);
    }

    /**
     * Insert starting data (runs once)
     */
    private void insertDefaults(SQLiteDatabase db) {
        db.execSQL("INSERT INTO user (name, coins, pet_gender) VALUES ('Iggy',150,'male');");
        db.execSQL(
                "INSERT INTO quest (title, description, reward, progress, rewarded) VALUES " +
                        "('Breathing Exercise','Guided deep breathing for stress relief',50,0,0)," +
                        "('Hydration Reminder','Encouraging water intake for physical wellness',30,0,0)," +
                        "('Movement Break','Gentle stretching to release tension',40,0,0)," +
                        "('Positive Reflection','Write one thing you are grateful for or proud of',60,0,0)," +
                        "('Grounding Technique','Simple mindfulness exercises',25,0,0);"
        );
    }

    /**
     * Called when DB version changes
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {

        // Delete old tables
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS accessory");
        db.execSQL("DROP TABLE IF EXISTS quest");
        db.execSQL("DROP TABLE IF EXISTS mood");

        // Recreate
        onCreate(db);
    }
}
