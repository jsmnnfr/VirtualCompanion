package com.example.virtualcompanion;

import android.content.Context;
import android.content.SharedPreferences;

public class OutfitManager {

    private static final String PREF_NAME = "outfit_data";

    private static final String KEY_TOP = "top";
    private static final String KEY_BOTTOM = "bottom";
    private static final String KEY_HAT = "hat";
    private static final String KEY_GLASSES = "glasses";


    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }


    // ================= RESET (for testing) =================

    public static void resetAll(Context context) {
        getPrefs(context).edit().clear().apply();
    }


    // ================= TOP =================

    public static void setTop(Context c, int resId) {
        getPrefs(c).edit().putInt(KEY_TOP, resId).apply();
    }

    public static int getTop(Context c) {
        return getPrefs(c).getInt(KEY_TOP, 0);
    }


    // ================= BOTTOM =================

    public static void setBottom(Context c, int resId) {
        getPrefs(c).edit().putInt(KEY_BOTTOM, resId).apply();
    }

    public static int getBottom(Context c) {
        return getPrefs(c).getInt(KEY_BOTTOM, 0);
    }


    // ================= HAT =================

    public static void setHat(Context c, int resId) {
        getPrefs(c).edit().putInt(KEY_HAT, resId).apply();
    }

    public static int getHat(Context c) {
        return getPrefs(c).getInt(KEY_HAT, 0);
    }


    // ================= GLASSES =================

    public static void setGlasses(Context c, int resId) {
        getPrefs(c).edit().putInt(KEY_GLASSES, resId).apply();
    }

    public static int getGlasses(Context c) {
        return getPrefs(c).getInt(KEY_GLASSES, 0);
    }
}