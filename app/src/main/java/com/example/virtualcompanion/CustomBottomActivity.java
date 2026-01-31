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

    private ImageView topLayer, bottomLayer, hatLayer, glassesLayer;
    private TextView equipButton;

    private int selectedPreview = 0; // what user clicked


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

            int equipped =
                    OutfitManager.getBottom(this);


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

        int equipped =
                OutfitManager.getBottom(this);

        if (selectedPreview == equipped &&
                equipped != 0) {

            equipButton.setText("Unequip");

        } else {

            equipButton.setText("Equip");
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
            c1.setOnClickListener(v ->
                    startActivity(new Intent(
                            this,
                            CustomTopActivity.class)));
        }

        if (c3 != null) {
            c3.setOnClickListener(v ->
                    startActivity(new Intent(
                            this,
                            CustomHatActivity.class)));
        }

        if (c4 != null) {
            c4.setOnClickListener(v ->
                    startActivity(new Intent(
                            this,
                            CustomGlassesActivity.class)));
        }


        // Settings
        ImageView s = findViewById(R.id.settingsIcon);

        if (s != null) {
            s.setOnClickListener(v ->
                    startActivity(new Intent(
                            this,
                            SettingsActivity.class)));
        }


        // Bottom Nav
        ImageView h = findViewById(R.id.navHome);
        ImageView q = findViewById(R.id.navQuests);
        ImageView c = findViewById(R.id.navCustomize);


        if (h != null) {
            h.setOnClickListener(v ->
                    startActivity(new Intent(
                            this,
                            MoodResultActivity.class)));
        }

        if (q != null) {
            q.setOnClickListener(v ->
                    startActivity(new Intent(
                            this,
                            QuestsActivity.class)));
        }

        if (c != null) {
            c.setOnClickListener(v ->
                    startActivity(new Intent(
                            this,
                            CustomTopActivity.class)));
        }
    }
}
