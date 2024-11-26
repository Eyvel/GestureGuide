package com.example.guestureguide;

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

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPassword extends AppCompatActivity {

    private EditText forgetPasswordEmail;
    private EditText forgetPasswordNew;
    private EditText forgetPasswordConfirm;
    private Button forgetPasswordButton;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        // Initialize the views
        forgetPasswordEmail = findViewById(R.id.forget_password_email);
        forgetPasswordNew = findViewById(R.id.forget_password_new);
        forgetPasswordConfirm = findViewById(R.id.forget_password_confirm);
        forgetPasswordButton = findViewById(R.id.forget_password_button);

        backButton = findViewById(R.id.back_to_login_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set an OnClickListener for the update button
        forgetPasswordButton.setOnClickListener(v -> handleUpdate());
    }

    private void handleUpdate() {
        // Get input values
        String email = forgetPasswordEmail.getText().toString();
        String newPassword = forgetPasswordNew.getText().toString();
        String confirmPassword = forgetPasswordConfirm.getText().toString();

        // Validate input fields
        if (email.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Send the data to the backend using Volley
        String url = "https://gestureguide.com/auth/mobile/forgot_password.php"; // Replace with your actual URL

        // Create a POST request to send the data to the backend
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    JSONObject jsonResponse = null;
                    String message = "Unknown error"; // Initialize with a default value

                    try {
                        jsonResponse = new JSONObject(response);
                        message = jsonResponse.getString("message"); // Extract the message from the JSON response
                    } catch (JSONException e) {
                        e.printStackTrace();
                        message = "Error parsing response"; // In case of a parsing error
                    }

                    if (response.contains("success")) {
                        Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Error: " + message, Toast.LENGTH_SHORT).show();
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
                params.put("new_password", newPassword);
                params.put("confirm_password", confirmPassword);
                return params;
            }
        };

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(stringRequest);
    }
}
