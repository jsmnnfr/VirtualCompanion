package com.example.virtualcompanion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CustomTopActivity extends BaseActivity {

    // Layers
    private ImageView topLayer, bottomLayer, hatLayer, glassesLayer, petDisplay;
    private TextView equipButton;
    private TextView coinDisplay;
    
    // Category icons
    private ImageView categoryIcon1, categoryIcon2, categoryIcon3, categoryIcon4;

    // Selected preview
    private int selectedPreview = 0;
    private int selectedPrice = 0;
    private int moodIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_custom_top);

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
        
        // Category icons
        categoryIcon1 = findViewById(R.id.categoryIcon1);
        categoryIcon2 = findViewById(R.id.categoryIcon2);
        categoryIcon3 = findViewById(R.id.categoryIcon3);
        categoryIcon4 = findViewById(R.id.categoryIcon4);

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
                findViewById(R.id.itemsRecyclerViewTop);

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
                R.drawable.top_boy_flannel,
                R.drawable.top_girl_pink,
                R.drawable.top_boy_quarterzip,
                R.drawable.top_boy_floral,
                R.drawable.top_girl_plaid,
                R.drawable.top_girl_cardigan,
                R.drawable.top_boy_leather,
                R.drawable.top_girl_dress,
                R.drawable.top_boy_tuxedo
        };


        int[] equipImages = {

                0,
                R.drawable.top_boy_flannel_1,
                R.drawable.top_girl_pink_1,
                R.drawable.top_boy_quarterzip_1,
                R.drawable.top_boy_floral_1,
                R.drawable.top_girl_plaid_1,
                R.drawable.top_girl_cardigan_1,
                R.drawable.top_boy_leather_1,
                R.drawable.top_girl_dress_1,
                R.drawable.top_boy_tuxedo_1
        };


        String[] prices = {
                " ", "0", "0", "150", "0", "0", "150", "200", "250", "250"
        };

        int[] priceValues = {
                0, 0, 0, 150, 0, 0, 150, 200, 250, 250
        };

        // Initialize free items (price = 0)
        InventoryManager.initDefaults(this, new int[]{
                R.drawable.top_boy_flannel_1,
                R.drawable.top_girl_pink_1,
                R.drawable.top_boy_floral_1,
                R.drawable.top_girl_plaid_1
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

                                topLayer.setVisibility(View.GONE);

                            } else {

                                topLayer.setImageResource(resId);
                                topLayer.setVisibility(View.VISIBLE);
                            }

                            updateEquipText();
                        }
                );

        recyclerView.setAdapter(adapter);
        
        // Lock in height after first layout so purchases don't shrink the list
        recyclerView.post(() -> {
            adapter.notifyDataSetChanged();
            recyclerView.post(() -> {
                int h = recyclerView.getHeight();
                if (h > 0) recyclerView.setMinimumHeight(h);
            });
        });


        // ================= EQUIP BUTTON =================

        equipButton.setOnClickListener(v -> {

            int equipped = OutfitManager.getTop(this);

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
                OutfitManager.setTop(this, 0);
                topLayer.setVisibility(View.GONE);
                equipButton.setText("Equip");
                updateCategoryIcons();
                return;
            }

            // EQUIP
            OutfitManager.setTop(this, selectedPreview);
            equipButton.setText("Unequip");
            updateCategoryIcons();
        });


        // ================= RESTORE =================

        restoreAll();

        // Update coin display
        updateCoinDisplay();

        // ================= UI =================

        setupCategories();
        setupSettings();
        setupBottomNav();
        updateCategoryIcons();
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
                OutfitManager.getTop(this);

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

        int equipped = OutfitManager.getTop(this);

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


    // ================= CATEGORY ICON UPDATES =================
    
    private void updateCategoryIcons() {
        updateCategoryIcon(categoryIcon1, OutfitManager.getTop(this));
        updateCategoryIcon(categoryIcon2, OutfitManager.getBottom(this));
        updateCategoryIcon(categoryIcon3, OutfitManager.getHat(this));
        updateCategoryIcon(categoryIcon4, OutfitManager.getGlasses(this));
    }
    
    private void updateCategoryIcon(ImageView icon, int equippedResId) {
        if (icon == null) return;
        if (equippedResId == 0) return;

        int shopIcon = getShopIconForEquipped(equippedResId);
        if (shopIcon != 0) {
            icon.setImageResource(shopIcon);
        }
    }
    
    private int getShopIconForEquipped(int equippedResId) {
        // Tops
        if (equippedResId == R.drawable.top_boy_flannel_1) return R.drawable.top_boy_flannel;
        if (equippedResId == R.drawable.top_girl_pink_1) return R.drawable.top_girl_pink;
        if (equippedResId == R.drawable.top_boy_floral_1) return R.drawable.top_boy_floral;
        if (equippedResId == R.drawable.top_girl_plaid_1) return R.drawable.top_girl_plaid;
        if (equippedResId == R.drawable.top_boy_quarterzip_1) return R.drawable.top_boy_quarterzip;
        if (equippedResId == R.drawable.top_girl_cardigan_1) return R.drawable.top_girl_cardigan;
        if (equippedResId == R.drawable.top_boy_leather_1) return R.drawable.top_boy_leather;
        if (equippedResId == R.drawable.top_girl_dress_1) return R.drawable.top_girl_dress;
        if (equippedResId == R.drawable.top_boy_tuxedo_1) return R.drawable.top_boy_tuxedo;
        
        // Bottoms
        if (equippedResId == R.drawable.bottom_girl_flaredpants_1) return R.drawable.bottom_girl_flaredpants;
        if (equippedResId == R.drawable.bottom_boy_denimpants_1) return R.drawable.bottom_boy_denimpants;
        if (equippedResId == R.drawable.bottom_girl_skirt_1) return R.drawable.bottom_girl_skirt;
        if (equippedResId == R.drawable.bottom_boy_blackpants_1) return R.drawable.bottom_boy_blackpants;
        if (equippedResId == R.drawable.bottom_boy_short_1) return R.drawable.bottom_boy_short;
        
        // Hats
        if (equippedResId == R.drawable.hat_gang_1) return R.drawable.hat_gang;
        if (equippedResId == R.drawable.hat_flower_1) return R.drawable.hat_flower;
        if (equippedResId == R.drawable.hat_cowboy_1) return R.drawable.hat_cowboy;
        if (equippedResId == R.drawable.hat_beach_1) return R.drawable.hat_beach;
        
        // Glasses
        if (equippedResId == R.drawable.glasses_normal_1) return R.drawable.glasses_normal;
        if (equippedResId == R.drawable.glasses_shades_1) return R.drawable.glasses_shades;
        if (equippedResId == R.drawable.glasses_maloi_1) return R.drawable.glasses_maloi;
        if (equippedResId == R.drawable.glasses_heart_1) return R.drawable.glasses_heart;
        
        return 0;
    }


    // ================= CATEGORIES =================

    private void setupCategories() {
        LinearLayout cat2 = findViewById(R.id.categoryButton2);
        LinearLayout cat3 = findViewById(R.id.categoryButton3);
        LinearLayout cat4 = findViewById(R.id.categoryButton4);

        if (cat2 != null) {
            cat2.setOnClickListener(v -> {
                Intent intent = new Intent(this, CustomBottomActivity.class);
                intent.putExtra("selected_mood", moodIndex);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        if (cat3 != null) {
            cat3.setOnClickListener(v -> {
                Intent intent = new Intent(this, CustomHatActivity.class);
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
