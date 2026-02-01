package com.example.virtualcompanion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CustomBottomActivity extends BaseActivity {

    private ImageView topLayer, bottomLayer, hatLayer, glassesLayer, petDisplay;
    private TextView equipButton;
    private TextView coinDisplay;

    private int selectedPreview = 0; // what user clicked
    private int selectedPrice = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_custom_bottom);


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

        if ("female".equalsIgnoreCase(gender)) {
            petDisplay.setImageResource(R.drawable.emotion_neutral_g);
        } else {
            petDisplay.setImageResource(R.drawable.emotion_neutral);
        }

        // ================= RECYCLER =================

        RecyclerView recycler =
                findViewById(R.id.itemsRecyclerViewBottom);

        recycler.setLayoutManager(
                new LinearLayoutManager(
                        this,
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );


        // ================= SHOP =================

        int[] shopImages = {

                R.drawable.ic_cancel,
                R.drawable.bottom_girl_flaredpants,
                R.drawable.bottom_boy_denimpants,
                R.drawable.bottom_girl_skirt,
                R.drawable.bottom_boy_short,
                R.drawable.bottom_boy_blackpants
        };


        int[] equipImages = {

                0,
                R.drawable.bottom_girl_flaredpants_1,
                R.drawable.bottom_boy_denimpants_1,
                R.drawable.bottom_girl_skirt_1,
                R.drawable.bottom_boy_short_1,
                R.drawable.bottom_boy_blackpants_1
        };


        String[] prices = {
                "", "", "", "200", "200", "250"
        };

        int[] priceValues = {
                0, 0, 0, 200, 200, 250
        };

        // Initialize free items (price = 0)
        InventoryManager.initDefaults(this, new int[]{
                R.drawable.bottom_girl_flaredpants_1,
                R.drawable.bottom_boy_denimpants_1,
                R.drawable.bottom_girl_skirt_1
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
                                bottomLayer.setVisibility(View.GONE);
                            } else {
                                bottomLayer.setImageResource(resId);
                                bottomLayer.setVisibility(View.VISIBLE);
                            }

                            updateEquipText();
                        }
                );

        recycler.setAdapter(adapter);


        // ================= EQUIP BUTTON =================

        equipButton.setOnClickListener(v -> {

            int equipped = OutfitManager.getBottom(this);

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
                OutfitManager.setBottom(this, 0);
                bottomLayer.setVisibility(View.GONE);
                equipButton.setText("Equip");
                return;
            }

            // EQUIP
            OutfitManager.setBottom(this, selectedPreview);
            equipButton.setText("Unequip");
        });


        // ================= RESTORE =================

        restoreAll();

        // Update coin display
        updateCoinDisplay();

        // ================= UI =================

        setupUI();
    }


    // ================= RESTORE ALL =================

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
                OutfitManager.getBottom(this);

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
        
        int equipped = OutfitManager.getBottom(this);

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


    // ================= UI =================

    private void setupUI() {

        // Categories
        LinearLayout c1 = findViewById(R.id.categoryButton1);
        LinearLayout c2 = findViewById(R.id.categoryButton2);
        LinearLayout c3 = findViewById(R.id.categoryButton3);
        LinearLayout c4 = findViewById(R.id.categoryButton4);


        if (c1 != null) {
            c1.setOnClickListener(v -> {
                    startActivity(new Intent(
                            this,
                            CustomTopActivity.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        if (c3 != null) {
            c3.setOnClickListener(v -> {
                    startActivity(new Intent(
                            this,
                            CustomHatActivity.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        if (c4 != null) {
            c4.setOnClickListener(v -> {
                    startActivity(new Intent(
                            this,
                            CustomGlassesActivity.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }


        // Settings
        ImageView s = findViewById(R.id.settingsIcon);

        if (s != null) {
            s.setOnClickListener(v -> {
                    startActivity(new Intent(
                            this,
                            SettingsActivity.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }


        // Bottom Nav
        ImageView h = findViewById(R.id.navHome);
        ImageView q = findViewById(R.id.navQuests);
        ImageView c = findViewById(R.id.navCustomize);


        if (h != null) {
            h.setOnClickListener(v -> {
                    startActivity(new Intent(
                            this,
                            MoodResultActivity.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        if (q != null) {
            q.setOnClickListener(v -> {
                    startActivity(new Intent(
                            this,
                            QuestsActivity.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        if (c != null) {
            c.setOnClickListener(v -> {
                    startActivity(new Intent(
                            this,
                            CustomTopActivity.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
    }
}
