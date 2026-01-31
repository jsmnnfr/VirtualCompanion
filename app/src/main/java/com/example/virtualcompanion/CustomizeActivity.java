package com.example.virtualcompanion;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class CustomizeActivity extends BaseActivity {

    private boolean letterToastShown = false;
    private boolean maxToastShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_customize);

        DatabaseManager db = DatabaseManager.get(this);

        EditText petNameInput = findViewById(R.id.petNameInput);
        ImageView customizablePet = findViewById(R.id.customizablePet);
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


        // ================= GENDER =================

        boyButton.setOnClickListener(v -> {
            db.setGender("male");
            applyGenderImage(customizablePet, "male");
        });

        girlButton.setOnClickListener(v -> {
            db.setGender("female");
            applyGenderImage(customizablePet, "female");
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

            startActivity(new Intent(
                    this,
                    MoodActivity.class
            ));

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
