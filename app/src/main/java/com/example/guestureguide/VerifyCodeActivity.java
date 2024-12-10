package com.example.guestureguide;

import android.app.ProgressDialog;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VerifyCodeActivity extends AppCompatActivity {
    private EditText codeField, newPasswordField, confirmPasswordField;
    private Button resetPasswordButton;
    private String email;
    private ProgressDialog progressDialog;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);

        // Get email from intent
        email = getIntent().getStringExtra("email");

        // Initialize views
        codeField = findViewById(R.id.codeField);
        newPasswordField = findViewById(R.id.newPasswordField);
        confirmPasswordField = findViewById(R.id.confirmPasswordField);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);

        backButton = findViewById(R.id.back_to_forgot_password);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Resetting password...");
        progressDialog.setCancelable(false);

        resetPasswordButton.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        // Disable the reset password button to prevent multiple clicks
        resetPasswordButton.setEnabled(false);
        progressDialog.show();  // Show the progress dialog

        String code = codeField.getText().toString().trim();
        String newPassword = newPasswordField.getText().toString().trim();
        String confirmPassword = confirmPasswordField.getText().toString().trim();

        // Validate input fields
        if (code.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            resetPasswordButton.setEnabled(true);  // Re-enable the button if validation fails
            progressDialog.dismiss();  // Dismiss the progress dialog if validation fails
            return;
        }

        // Check if new password meets the required conditions
        if (!newPassword.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            Toast.makeText(this, "Password must meet required conditions", Toast.LENGTH_SHORT).show();
            resetPasswordButton.setEnabled(true);  // Re-enable the button if validation fails
            progressDialog.dismiss();  // Dismiss the progress dialog if validation fails
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            resetPasswordButton.setEnabled(true);  // Re-enable the button if passwords don't match
            progressDialog.dismiss();  // Dismiss the progress dialog if passwords don't match
            return;
        }

        // Send the reset password request to the backend
        String url = "https://gestureguide.com/auth/mobile/forgot_password.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Log the full response to check what you're getting from the server
                    Log.d("VerifyCodeActivity", "Full Response: " + response);

                    progressDialog.dismiss(); // Dismiss the progress dialog on response
                    try {
                        // If the response is JSON, parse it and check the "status" or "message" fields
                        JSONObject jsonResponse = new JSONObject(response);

                        // Assuming the response is something like: { "status": "success", "message": "Password reset successfully!" }
                        String status = jsonResponse.optString("status");
                        String message = jsonResponse.optString("message");

                        if ("success".equals(status)) {
                            Toast.makeText(this, "Password reset successfully!", Toast.LENGTH_SHORT).show();
                            finish();  // Close the activity after successful reset
                        } else {
                            // Display the full message from the response
                            Toast.makeText(this, "Failed to reset password: " + message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        // Handle the exception if JSON parsing fails
                        Toast.makeText(this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    resetPasswordButton.setEnabled(true);  // Re-enable button after request is done
                },
                error -> {
                    progressDialog.dismiss(); // Dismiss the progress dialog on error
                    if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                        Toast.makeText(this, "Requested resource not found", Toast.LENGTH_SHORT).show();
                    } else {
                        // Log and show the full error message
                        Log.e("VerifyCodeActivity", "Volley error: " + error.getMessage());
                        Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    resetPasswordButton.setEnabled(true);  // Re-enable button on error
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "reset_password");
                params.put("email", email);
                params.put("verification_code", code);  // Send the verification code
                params.put("new_password", newPassword);
                params.put("confirm_password", confirmPassword);
                return params;
            }
        };




        // Add the request to the Volley queue
        Volley.newRequestQueue(this).add(request);
    }

}
