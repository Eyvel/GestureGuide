package com.example.guestureguide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class FailedScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_screen); // Make sure this XML file is the correct one

        // Find the "Go Back" button by its ID
        Button failedGoBackButton = findViewById(R.id.failed_go_back); // Correct button ID

        // Set the OnClickListener for the button
        failedGoBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to MainActivity
                Intent intent = new Intent(FailedScreen.this, MainActivity.class);

                // Start MainActivity
                startActivity(intent);

                // Optionally, finish the current activity if you don't want the user to return to it
                finish();
            }
        });
    }
}
