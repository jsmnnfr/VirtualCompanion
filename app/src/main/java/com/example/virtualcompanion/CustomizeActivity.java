package com.example.virtualcompanion;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class CustomizeActivity extends BaseActivity {

    private boolean letterToastShown = false;
    private boolean maxToastShown = false;
    private boolean isEditing = false;
    private View lastSelectedGenderButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_customize);

        DatabaseManager db = DatabaseManager.get(this);

        EditText petNameInput = findViewById(R.id.petNameInput);
        ImageView customizablePet = findViewById(R.id.customizablePet);
        ImageView editIcon = findViewById(R.id.editIcon);
        LinearLayout nameContainer = findViewById(R.id.nameContainer);
        View boyButton = findViewById(R.id.boyButton);
        View girlButton = findViewById(R.id.girlButton);
        MaterialButton doneButton = findViewById(R.id.doneButton);


        // ================= LOAD SAVED DATA =================

        String savedName = db.getName();

        if (!savedName.isEmpty()) {
            petNameInput.setText(savedName.toUpperCase());
            petNameInput.setSelection(petNameInput.length());
        }

        applyGenderImage(customizablePet, db.getGender());

        // Set initial selected button based on saved gender
        if ("female".equalsIgnoreCase(db.getGender())) {
            lastSelectedGenderButton = girlButton;
            girlButton.setScaleX(1.1f);
            girlButton.setScaleY(1.1f);
        } else {
            lastSelectedGenderButton = boyButton;
            boyButton.setScaleX(1.1f);
            boyButton.setScaleY(1.1f);
        }


        // ================= ENABLE EDITING METHOD =================

        View.OnClickListener enableEditingListener = v -> {
            if (!isEditing) {
                // Enable editing
                petNameInput.setFocusable(true);
                petNameInput.setFocusableInTouchMode(true);
                petNameInput.setCursorVisible(true);
                petNameInput.setClickable(true);
                petNameInput.requestFocus();

                // Show keyboard
                petNameInput.postDelayed(() -> {
                    android.view.inputmethod.InputMethodManager imm =
                            (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.showSoftInput(petNameInput, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
                }, 100);

                isEditing = true;
            } else {
                // Disable editing
                petNameInput.setFocusable(false);
                petNameInput.setFocusableInTouchMode(false);
                petNameInput.setCursorVisible(false);
                petNameInput.setClickable(true);
                petNameInput.clearFocus();

                // Hide keyboard
                android.view.inputmethod.InputMethodManager imm =
                        (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(petNameInput.getWindowToken(), 0);

                isEditing = false;
            }
        };


        // ================= EDIT ICON CLICK =================

        editIcon.setOnClickListener(enableEditingListener);


        // ================= NAME CONTAINER CLICK =================

        nameContainer.setOnClickListener(enableEditingListener);


        // ================= PET NAME INPUT CLICK =================

        petNameInput.setOnClickListener(enableEditingListener);


        // ================= NAME VALIDATION =================

        petNameInput.addTextChangedListener(new TextWatcher() {

            private String lastValid = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}


            @Override
            public void afterTextChanged(Editable s) {

                petNameInput.removeTextChangedListener(this);

                String input = s.toString();

                // Convert to uppercase
                String upper = input.toUpperCase();

                // Remove non-letters
                String clean = upper.replaceAll("[^A-Z]", "");

                // Limit to 8 chars
                if (clean.length() > 8) {

                    clean = clean.substring(0, 8);

                    if (!maxToastShown) {
                        Toast.makeText(
                                CustomizeActivity.this,
                                "Maximum of 8 letters only",
                                Toast.LENGTH_SHORT
                        ).show();

                        maxToastShown = true;
                    }

                } else {
                    maxToastShown = false;
                }

                // If user typed invalid char
                if (!upper.equals(clean)) {

                    if (!letterToastShown) {

                        Toast.makeText(
                                CustomizeActivity.this,
                                "Only letters are allowed",
                                Toast.LENGTH_SHORT
                        ).show();

                        letterToastShown = true;
                    }

                } else {
                    letterToastShown = false;
                }


                // Update text safely
                petNameInput.setText(clean);
                petNameInput.setSelection(clean.length());

                lastValid = clean;

                petNameInput.addTextChangedListener(this);
            }
        });


        // ================= GENDER WITH SIMPLE ANIMATION =================

        boyButton.setOnClickListener(v -> {
            // Reset previous selection
            if (lastSelectedGenderButton != null) {
                lastSelectedGenderButton.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(200)
                        .start();
            }

            // Scale up current selection
            boyButton.animate()
                    .scaleX(1.1f)
                    .scaleY(1.1f)
                    .setDuration(300)
                    .start();

            // Update gender
            db.setGender("male");
            applyGenderImage(customizablePet, "male");

            // Remember selection
            lastSelectedGenderButton = boyButton;
        });

        girlButton.setOnClickListener(v -> {
            // Reset previous selection
            if (lastSelectedGenderButton != null) {
                lastSelectedGenderButton.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(200)
                        .start();
            }

            // Scale up current selection
            girlButton.animate()
                    .scaleX(1.1f)
                    .scaleY(1.1f)
                    .setDuration(300)
                    .start();

            // Update gender
            db.setGender("female");
            applyGenderImage(customizablePet, "female");

            // Remember selection
            lastSelectedGenderButton = girlButton;
        });


        // ================= DONE =================

        doneButton.setOnClickListener(v -> {

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
            db.setGender(db.getGender());
            db.setHasCustomized(true);

            Intent intent;
            if (db.hasSelectedMoodToday()) {
                // Already selected today -> Go straight to Result screen
                intent = new Intent(this, MoodResultActivity.class);
            } else {
                // New day or first time -> Go to Mood selection
                intent = new Intent(this, MoodActivity.class);
            }

            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
    }


    // ================= GENDER IMAGE =================

    private void applyGenderImage(ImageView petView, String gender) {

        if ("female".equalsIgnoreCase(gender)) {

            petView.setImageResource(R.drawable.emotion_neutral_g);

        } else {

            petView.setImageResource(R.drawable.emotion_neutral);
        }
    }
}