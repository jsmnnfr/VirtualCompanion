package com.example.virtualcompanion;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.button.MaterialButton;

public class SettingsActivity extends BaseActivity {

    private boolean letterToastShown = false;
    private boolean maxToastShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        DatabaseManager db = DatabaseManager.get(this);


        // ================= UI =================

        EditText petNameInput = findViewById(R.id.petNameInput);
        View boyButton = findViewById(R.id.boyButton);
        View girlButton = findViewById(R.id.girlButton);
        MaterialButton saveButton = findViewById(R.id.saveButton);


        // ================= LOAD =================

        String savedName = db.getName();

        if (!savedName.isEmpty()) {

            petNameInput.setText(savedName.toUpperCase());
            petNameInput.setSelection(petNameInput.length());
        }

        String[] currentGender = {db.getGender()};

        updateGenderButtonStates(boyButton, girlButton, currentGender[0]);


        // ================= NAME VALIDATION =================

        petNameInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}


            @Override
            public void afterTextChanged(Editable s) {

                petNameInput.removeTextChangedListener(this);

                String input = s.toString();

                String upper = input.toUpperCase();

                String clean = upper.replaceAll("[^A-Z]", "");


                if (clean.length() > 8) {

                    clean = clean.substring(0, 8);

                    if (!maxToastShown) {

                        Toast.makeText(
                                SettingsActivity.this,
                                "Maximum of 8 letters only",
                                Toast.LENGTH_SHORT
                        ).show();

                        maxToastShown = true;
                    }

                } else {
                    maxToastShown = false;
                }


                if (!upper.equals(clean)) {

                    if (!letterToastShown) {

                        Toast.makeText(
                                SettingsActivity.this,
                                "Only letters are allowed",
                                Toast.LENGTH_SHORT
                        ).show();

                        letterToastShown = true;
                    }

                } else {
                    letterToastShown = false;
                }


                petNameInput.setText(clean);
                petNameInput.setSelection(clean.length());

                petNameInput.addTextChangedListener(this);
            }
        });


        // ================= GENDER =================

        boyButton.setOnClickListener(v -> {

            currentGender[0] = "male";
            updateGenderButtonStates(boyButton, girlButton, "male");
        });

        girlButton.setOnClickListener(v -> {

            currentGender[0] = "female";
            updateGenderButtonStates(boyButton, girlButton, "female");
        });


        // ================= SAVE =================

        saveButton.setOnClickListener(v -> {

            String name = petNameInput.getText().toString().trim();

            if (name.isEmpty()) {

                Toast.makeText(
                        this,
                        "Please enter a name",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            db.setName(name);
            db.setGender(currentGender[0]);

            finish();
        });


        // ================= SOUND =================

        SwitchCompat soundEffectsToggle =
                findViewById(R.id.soundEffectsToggle);

        if (soundEffectsToggle != null) {

            soundEffectsToggle.setChecked(
                    MusicManager.isMusicEnabled()
            );

            soundEffectsToggle.setOnCheckedChangeListener(
                    (btn, checked) -> {

                        MusicManager.setMusicEnabled(checked);

                        if (checked) {
                            MusicManager.startMusic(this);
                        } else {
                            MusicManager.pauseMusic();
                        }
                    }
            );
        }


        // ================= BACK =================

        ImageView backButton = findViewById(R.id.backButton);

        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }


        // ================= NAV =================

        ImageView navHome = findViewById(R.id.navHome);
        ImageView navQuests = findViewById(R.id.navQuests);
        ImageView navCustomize = findViewById(R.id.navCustomize);

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                startActivity(new Intent(
                        this,
                        MoodResultActivity.class
                ));
                finish();
            });
        }

        if (navQuests != null) {
            navQuests.setOnClickListener(v -> {
                startActivity(new Intent(
                        this,
                        QuestsActivity.class
                ));
                finish();
            });
        }

        if (navCustomize != null) {
            navCustomize.setOnClickListener(v -> {
                startActivity(new Intent(
                        this,
                        CustomTopActivity.class
                ));
                finish();
            });
        }
    }


    // ================= GENDER UI =================

    private void updateGenderButtonStates(
            View boyButton,
            View girlButton,
            String gender
    ) {

        if ("male".equalsIgnoreCase(gender)) {

            boyButton.setAlpha(1f);
            girlButton.setAlpha(0.5f);

        } else {

            boyButton.setAlpha(0.5f);
            girlButton.setAlpha(1f);
        }
    }
}
