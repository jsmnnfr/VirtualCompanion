package com.example.virtualcompanion;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * InventoryManager
 * Tracks which outfit items the user owns
 */
public class InventoryManager {

    private static final String PREF_NAME = "inventory_data";
    private static final String KEY_OWNED_ITEMS = "owned_items";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Check if user owns an item
     */
    public static boolean isOwned(Context c, int resId) {
        if (resId == 0) return true; // Empty slot is always "owned"
        
        Set<String> owned = getPrefs(c).getStringSet(KEY_OWNED_ITEMS, new HashSet<>());
        return owned.contains(String.valueOf(resId));
    }

    /**
     * Mark an item as owned
     */
    public static void addItem(Context c, int resId) {
        Set<String> owned = new HashSet<>(
            getPrefs(c).getStringSet(KEY_OWNED_ITEMS, new HashSet<>())
        );
        owned.add(String.valueOf(resId));
        getPrefs(c).edit().putStringSet(KEY_OWNED_ITEMS, owned).apply();
    }

    /**
     * Initialize default free items
     */
    public static void initDefaults(Context c, int[] freeItems) {
        for (int resId : freeItems) {
            if (resId != 0 && !isOwned(c, resId)) {
                addItem(c, resId);
            }
        }
    }
}
