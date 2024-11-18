package com.example.guestureguide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class WaitingScreen extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_screen);

        sharedPreferences = getSharedPreferences("MyAppName", MODE_PRIVATE); // Initialize SharedPreferences

        // Find the button by its ID
        Button waitGotoLoginButton = findViewById(R.id.wait_goto_login);

        // Set the OnClickListener for the button
        waitGotoLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to MainActivity
                Intent intent = new Intent(WaitingScreen.this, MainActivity.class);

                // Set the isLoggedIn flag to false (log the user out)
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false); // Log the user out
                editor.apply();

                // Start MainActivity
                startActivity(intent);

                // Optionally, finish the current activity if you don't want the user to return to it
                finish();
            }
        });
    }
}
