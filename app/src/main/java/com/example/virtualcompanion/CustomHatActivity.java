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
    private ImageView topLayer, bottomLayer, hatLayer, glassesLayer;

    private TextView equipButton;

    // Preview
    private int selectedPreview = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_custom_hat);


        // ================= LAYERS =================

        topLayer = findViewById(R.id.topLayer);
        bottomLayer = findViewById(R.id.bottomLayer);
        hatLayer = findViewById(R.id.hatLayer);
        glassesLayer = findViewById(R.id.glassesLayer);

        equipButton = findViewById(R.id.equipButton);


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

                "",
                "0",
                "0",
                "150",
                "180"
        };


        // ================= ADAPTER =================

        ShopItemAdapter adapter =
                new ShopItemAdapter(

                        shopImages,
                        equipImages,
                        prices,

                        resId -> {

                            // PREVIEW
                            selectedPreview = resId;

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

            int equipped =
                    OutfitManager.getHat(this);


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

        int equipped =
                OutfitManager.getHat(this);

        if (selectedPreview == equipped &&
                equipped != 0) {

            equipButton.setText("Unequip");

        } else {

            equipButton.setText("Equip");
        }
    }


    // ================= CATEGORIES =================

    private void setupCategories() {

        LinearLayout cat1 = findViewById(R.id.categoryButton1);
        LinearLayout cat2 = findViewById(R.id.categoryButton2);
        LinearLayout cat3 = findViewById(R.id.categoryButton3);
        LinearLayout cat4 = findViewById(R.id.categoryButton4);


        if (cat1 != null) {
            cat1.setOnClickListener(v ->
                    startActivity(new Intent(
                            this,
                            CustomTopActivity.class)));
        }

        if (cat2 != null) {
            cat2.setOnClickListener(v ->
                    startActivity(new Intent(
                            this,
                            CustomBottomActivity.class)));
        }

        if (cat4 != null) {
            cat4.setOnClickListener(v ->
                    startActivity(new Intent(
                            this,
                            CustomGlassesActivity.class)));
        }
    }


    // ================= SETTINGS =================

    private void setupSettings() {

        ImageView settings =
                findViewById(R.id.settingsIcon);

        if (settings != null) {
            settings.setOnClickListener(v ->
                    startActivity(new Intent(
                            this,
                            SettingsActivity.class)));
        }
    }


    // ================= BOTTOM NAV =================

    private void setupBottomNav() {

        ImageView navHome = findViewById(R.id.navHome);
        ImageView navQuests = findViewById(R.id.navQuests);


        if (navHome != null) {
            navHome.setOnClickListener(v ->
                    startActivity(new Intent(
                            this,
                            MoodResultActivity.class)));
        }

        if (navQuests != null) {
            navQuests.setOnClickListener(v ->
                    startActivity(new Intent(
                            this,
                            QuestsActivity.class)));
        }
    }
}
