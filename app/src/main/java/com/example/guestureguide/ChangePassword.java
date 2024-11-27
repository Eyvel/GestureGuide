package com.example.guestureguide;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class ChangePassword extends AppCompatActivity {

    private EditText changePasswordNew, changePasswordOld, changePasswordConfirm;
    private Button changePasswordButton;
    private SharedPreferences sharedPreferences;
    private ImageButton backbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Initialize the views
        changePasswordOld = findViewById(R.id.change_password_old);
        changePasswordNew = findViewById(R.id.change_password_new);
        changePasswordConfirm = findViewById(R.id.change_password_confirm);
        changePasswordButton = findViewById(R.id.update_button);
        backbutton = findViewById(R.id.back_to_profile_button);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyAppName", MODE_PRIVATE);

        // Set an OnClickListener for the update button
        changePasswordButton.setOnClickListener(v -> handleUpdate());
    }

    private void handleUpdate() {
        // Get input values
        String oldPassword = changePasswordOld.getText().toString();
        String newPassword = changePasswordNew.getText().toString();
        String confirmPassword = changePasswordConfirm.getText().toString();

        // Fetch email from SharedPreferences
        String email = sharedPreferences.getString("email", "");

        // Validate input fields
        if (email.isEmpty()) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Send the data to the backend using Volley
        String url = "https://gestureguide.com/auth/mobile/change_password.php"; // Replace with your actual URL

        // Create a POST request to send the data to the backend
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Handle the response from the server
                    if (response.contains("success")) {
                        Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Error: Incorrect old password", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Handle errors (e.g., network issues, server errors)
                    Toast.makeText(this, "Failed to update password: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected java.util.Map<String, String> getParams() {
                // Pass parameters to the backend
                java.util.Map<String, String> params = new java.util.HashMap<>();
                params.put("email", email);
                params.put("old_password", oldPassword);
                params.put("new_password", newPassword);
                params.put("confirm_password", confirmPassword);
                return params;
            }
        };

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(stringRequest);
    }
}
