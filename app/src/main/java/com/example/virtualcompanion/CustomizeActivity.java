package com.example.virtualcompanion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;

public class CustomizeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_customize);

        DatabaseManager db = DatabaseManager.get(this);

        EditText petNameInput = findViewById(R.id.petNameInput);
        ImageView customizablePet = findViewById(R.id.customizablePet);
        View boyButton = findViewById(R.id.boyButton);
        View girlButton = findViewById(R.id.girlButton);


        // ================= LOAD SAVED =================

        String savedName = db.getName();

        if (!savedName.isEmpty()) {
            petNameInput.setText(savedName);
            petNameInput.setSelection(savedName.length());
        }

        applyGenderImage(customizablePet, db.getGender());


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

        MaterialButton doneButton = findViewById(R.id.doneButton);

        doneButton.setOnClickListener(v -> {

            String name =
                    petNameInput
                            .getText()
                            .toString()
                            .trim()
                            .toUpperCase(); // FORCE UPPERCASE

            if (!name.isEmpty()) {
                db.setName(name);
            }

            Intent intent = new Intent(
                    CustomizeActivity.this,
                    MoodActivity.class
            );

            startActivity(intent);
            finish();
        });

    }


    private void applyGenderImage(ImageView petView, String gender) {

        if ("female".equalsIgnoreCase(gender)) {

            petView.setImageResource(R.drawable.emotion_neutral_g);

        } else {

            petView.setImageResource(R.drawable.emotion_neutral);
        }
    }
}
