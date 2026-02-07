package com.example.virtualcompanion;

import android.app.Application;
import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MainApplication extends Application {

    private int activityReferences = 0;
    private boolean isActivityChangingConfigurations = false;
    private Activity currentActivity; // Track current activity

    @Override
    public void onCreate() {
        super.onCreate();

        // Register activity lifecycle callbacks
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                if (++activityReferences == 1 && !isActivityChangingConfigurations) {
                    // App enters foreground
                    android.util.Log.d("MusicManager", "App in foreground - Resuming music");

                    // Resume from where we left off
                    MusicManager.resumeCurrentTrack(MainApplication.this);
                }
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                currentActivity = activity;
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                isActivityChangingConfigurations = activity.isChangingConfigurations();
                if (--activityReferences == 0 && !isActivityChangingConfigurations) {
                    // App enters background (user pressed home or switched apps)
                    android.util.Log.d("MusicManager", "App in background - Pausing music");

                    // Just pause, don't stop - this preserves the track and position
                    MusicManager.pauseMusic();
                }
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                if (currentActivity == activity) {
                    currentActivity = null;
                }
            }
        });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        // Fully reset music when app is terminated
        android.util.Log.d("MusicManager", "App terminated - Resetting music");
        MusicManager.resetMusic();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        // Pause music on low memory
        MusicManager.pauseMusic();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        // Only stop if system is critically low on memory
        if (level >= TRIM_MEMORY_COMPLETE) {
            MusicManager.stopMusic();
        } else if (level >= TRIM_MEMORY_MODERATE) {
            MusicManager.pauseMusic();
        }
    }
}