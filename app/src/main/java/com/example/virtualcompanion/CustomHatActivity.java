package com.example.virtualcompanion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CustomHatActivity extends BaseActivity {

    // Layers
    private ImageView topLayer, bottomLayer, hatLayer, glassesLayer, petDisplay;

    private TextView equipButton;
    private TextView coinDisplay;

    // Preview
    private int selectedPreview = 0;
    private int selectedPrice = 0;
    private int moodIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_custom_hat);

        moodIndex = getIntent().getIntExtra("selected_mood", -1);

        if (moodIndex == -1) {
            moodIndex = DatabaseManager.get(this).getLatestMood();
        }

        // ================= LAYERS =================

        topLayer = findViewById(R.id.topLayer);
        bottomLayer = findViewById(R.id.bottomLayer);
        hatLayer = findViewById(R.id.hatLayer);
        glassesLayer = findViewById(R.id.glassesLayer);

        equipButton = findViewById(R.id.equipButton);
        coinDisplay = findViewById(R.id.coinAmount);
        petDisplay = findViewById(R.id.petDisplay);

        // ================= GENDER =================

        String gender = DatabaseManager.get(this).getGender();

        int[] petEmotions = "female".equalsIgnoreCase(gender)
                ? new int[]{
                R.drawable.emote_neutral_g_moodresult,
                R.drawable.emote_happy_g_moodresult,
                R.drawable.emote_sad_g_moodresult,
                R.drawable.emote_angry_g_moodresult,
                R.drawable.emote_anxious_g_moodresult
        }
                : new int[]{
                R.drawable.emote_neutral_b_moodresult,
                R.drawable.emote_happy_b_moodresult,
                R.drawable.emote_sad_b_moodresult,
                R.drawable.emote_angry_b_moodresult,
                R.drawable.emote_anxious_b_moodresult
        };

        petDisplay.setImageResource(petEmotions[moodIndex]);

        // ================= RECYCLER =================

        RecyclerView recyclerView =
                findViewById(R.id.itemsRecyclerViewHat);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        this,
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );


        // ================= SHOP =================

        int[] shopImages = {

                R.drawable.ic_cancel,
                R.drawable.hat_gang,
                R.drawable.hat_flower,
                R.drawable.hat_cowboy,
                R.drawable.hat_beach
        };


        int[] equipImages = {

                0,
                R.drawable.hat_gang_1,
                R.drawable.hat_flower_1,
                R.drawable.hat_cowboy_1,
                R.drawable.hat_beach_1
        };


        String[] prices = {

                " ",
                "0",
                "0",
                "150",
                "180"
        };

        int[] priceValues = {
                0, 0, 0, 150, 180
        };

        // Initialize free items (price = 0)
        InventoryManager.initDefaults(this, new int[]{
                R.drawable.hat_gang_1,
                R.drawable.hat_flower_1
        });


        // ================= ADAPTER =================

        ShopItemAdapter adapter =
                new ShopItemAdapter(

                        shopImages,
                        equipImages,
                        prices,

                        (resId, position) -> {

                            // PREVIEW
                            selectedPreview = resId;
                            selectedPrice = priceValues[position];

                            if (resId == 0) {

                                hatLayer.setVisibility(View.GONE);

                            } else {

                                hatLayer.setImageResource(resId);
                                hatLayer.setVisibility(View.VISIBLE);
                            }

                            updateEquipText();
                        }
                );

        recyclerView.setAdapter(adapter);


        // ================= EQUIP =================

        equipButton.setOnClickListener(v -> {

            int equipped = OutfitManager.getHat(this);

            // Check if owned
            if (!InventoryManager.isOwned(this, selectedPreview)) {
                // PURCHASE
                try {
                    int coins = DatabaseManager.get(this).getCoins();

                    if (coins >= selectedPrice) {
                        DatabaseManager.get(this).addCoins(-selectedPrice);
                        InventoryManager.addItem(this, selectedPreview);
                        adapter.notifyDataSetChanged();
                        updateCoinDisplay();
                        updateEquipText();
                        android.widget.Toast.makeText(this, "Purchased!", android.widget.Toast.LENGTH_SHORT).show();
                    } else {
                        android.widget.Toast.makeText(this, "Not enough coins!", android.widget.Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    android.widget.Toast.makeText(this, "Error: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // UNEQUIP
            if (selectedPreview == equipped) {
                OutfitManager.setHat(this, 0);
                hatLayer.setVisibility(View.GONE);
                equipButton.setText("Equip");
                return;
            }

            // EQUIP
            OutfitManager.setHat(this, selectedPreview);
            equipButton.setText("Unequip");
        });


        // ================= RESTORE =================

        restoreAll();

        // Update coin display
        updateCoinDisplay();

        // ================= UI =================

        setupCategories();
        setupSettings();
        setupBottomNav();
    }


    // ================= RESTORE =================

    private void restoreAll() {

        restoreLayer(topLayer,
                OutfitManager.getTop(this));

        restoreLayer(bottomLayer,
                OutfitManager.getBottom(this));

        restoreLayer(hatLayer,
                OutfitManager.getHat(this));

        restoreLayer(glassesLayer,
                OutfitManager.getGlasses(this));


        selectedPreview =
                OutfitManager.getHat(this);

        updateEquipText();
    }


    private void restoreLayer(ImageView layer, int resId) {

        if (layer == null) return;

        if (resId == 0) {

            layer.setVisibility(View.GONE);

        } else {

            layer.setImageResource(resId);
            layer.setVisibility(View.VISIBLE);
        }
    }


    // ================= EQUIP TEXT =================

    private void updateEquipText() {
        if (equipButton == null) return;

        int equipped = OutfitManager.getHat(this);

        // Check if item is owned
        if (!InventoryManager.isOwned(this, selectedPreview)) {
            equipButton.setText("Buy - " + selectedPrice + " coins");
            return;
        }

        // If owned, check if equipped
        if (selectedPreview == equipped && equipped != 0) {
            equipButton.setText("Unequip");
        } else {
            equipButton.setText("Equip");
        }
    }

    private void updateCoinDisplay() {
        if (coinDisplay != null) {
            try {
                int coins = DatabaseManager.get(this).getCoins();
                coinDisplay.setText(String.valueOf(coins));
            } catch (Exception e) {
                coinDisplay.setText("150");
            }

            // CHEAT MODE: Long press to add 100 coins
            coinDisplay.setOnLongClickListener(v -> {
                DatabaseManager.get(this).addCoins(100);
                updateCoinDisplay();
                android.widget.Toast.makeText(this, "[DEV] +100 coins added", android.widget.Toast.LENGTH_SHORT).show();
                return true;
            });
        }
    }


    // ================= CATEGORIES =================

    private void setupCategories() {

        LinearLayout cat1 = findViewById(R.id.categoryButton1);
        LinearLayout cat2 = findViewById(R.id.categoryButton2);
        LinearLayout cat3 = findViewById(R.id.categoryButton3);
        LinearLayout cat4 = findViewById(R.id.categoryButton4);


        if (cat1 != null) {
            cat1.setOnClickListener(v -> {
                Intent intent = new Intent(this, CustomTopActivity.class);
                intent.putExtra("selected_mood", moodIndex);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        if (cat2 != null) {
            cat2.setOnClickListener(v -> {
                Intent intent = new Intent(this, CustomBottomActivity.class);
                intent.putExtra("selected_mood", moodIndex);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        if (cat4 != null) {
            cat4.setOnClickListener(v -> {
                Intent intent = new Intent(this, CustomGlassesActivity.class);
                intent.putExtra("selected_mood", moodIndex);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
    }


    // ================= SETTINGS =================

    private void setupSettings() {

        ImageView settings =
                findViewById(R.id.settingsIcon);

        if (settings != null) {
            settings.setOnClickListener(v -> {
                startActivity(new Intent(
                        this,
                        SettingsActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
    }


    // ================= BOTTOM NAV =================

    private void setupBottomNav() {

        ImageView navHome = findViewById(R.id.navHome);
        ImageView navQuests = findViewById(R.id.navQuests);


        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Intent intent = new Intent(this, MoodResultActivity.class);
                intent.putExtra("selected_mood", moodIndex);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });

        }

        if (navQuests != null) {
            navQuests.setOnClickListener(v -> {
                Intent intent = new Intent(this, QuestsActivity.class);
                intent.putExtra("selected_mood", moodIndex);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });

        }
    }
}