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
    private ImageView topLayer, bottomLayer, hatLayer, glassesLayer;

    private TextView equipButton;

    // Selected preview
    private int selectedPreview = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_custom_top);


        // ================= LAYERS =================

        topLayer = findViewById(R.id.topLayer);
        bottomLayer = findViewById(R.id.bottomLayer);
        hatLayer = findViewById(R.id.hatLayer);
        glassesLayer = findViewById(R.id.glassesLayer);

        equipButton = findViewById(R.id.equipButton);


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
                R.drawable.top_boy_floral,
                R.drawable.top_girl_plaid,
                R.drawable.top_boy_quarterzip,
                R.drawable.top_girl_cardigan,
                R.drawable.top_boy_leather,
                R.drawable.top_girl_dress,
                R.drawable.top_boy_tuxedo
        };


        int[] equipImages = {

                0,
                R.drawable.top_boy_flannel_1,
                R.drawable.top_girl_pink_1,
                R.drawable.top_boy_floral_1,
                R.drawable.top_girl_plaid_1,
                R.drawable.top_boy_quarterzip_1,
                R.drawable.top_girl_cardigan_1,
                R.drawable.top_boy_leather_1,
                R.drawable.top_girl_dress_1,
                R.drawable.top_boy_tuxedo_1
        };


        String[] prices = {

                "",
                "0", "0", "0", "0",
                "150", "150", "200",
                "250", "250"
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

                                topLayer.setVisibility(View.GONE);

                            } else {

                                topLayer.setImageResource(resId);
                                topLayer.setVisibility(View.VISIBLE);
                            }

                            updateEquipText();
                        }
                );

        recyclerView.setAdapter(adapter);


        // ================= EQUIP BUTTON =================

        equipButton.setOnClickListener(v -> {

            int equipped =
                    OutfitManager.getTop(this);


            // UNEQUIP
            if (selectedPreview == equipped) {

                OutfitManager.setTop(this, 0);

                topLayer.setVisibility(View.GONE);

                equipButton.setText("Equip");

                return;
            }


            // EQUIP
            OutfitManager.setTop(this, selectedPreview);

            equipButton.setText("Unequip");
        });


        // ================= RESTORE =================

        restoreAll();


        // ================= UI =================

        setupCategories();
        setupSettings();
        setupBottomNav();
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

        int equipped =
                OutfitManager.getTop(this);

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


        if (cat2 != null) {
            cat2.setOnClickListener(v ->
                    startActivity(new Intent(
                            this,
                            CustomBottomActivity.class)));
        }

        if (cat3 != null) {
            cat3.setOnClickListener(v ->
                    startActivity(new Intent(
                            this,
                            CustomHatActivity.class)));
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
        ImageView navCustomize = findViewById(R.id.navCustomize);


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
