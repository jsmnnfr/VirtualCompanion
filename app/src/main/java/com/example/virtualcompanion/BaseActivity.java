package com.example.virtualcompanion;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    // ===============================
    // GLOBAL EQUIPPED ITEMS
    // ===============================

    protected static int equippedTop = 0;
    protected static int equippedBottom = 0;
    protected static int equippedHat = 0;
    protected static int equippedGlasses = 0;


    // ===============================
    // MUSIC
    // ===============================

    @Override
    protected void onResume() {
        super.onResume();

        // Always ensure music is running
        MusicManager.startMusic(this);
    }
}