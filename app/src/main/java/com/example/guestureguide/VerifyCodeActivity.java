package com.example.guestureguide;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class VerifyCodeActivity extends AppCompatActivity {
    private EditText codeField, newPasswordField, confirmPasswordField;
    private Button resetPasswordButton;
    private String email;
    private ProgressDialog progressDialog;

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

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Resetting password...");
        progressDialog.setCancelable(false);

        resetPasswordButton.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        // Disable the reset password button to prevent multiple clicks
        resetPasswordButton.setEnabled(false);

        String code = codeField.getText().toString().trim();
        String newPassword = newPasswordField.getText().toString().trim();
        String confirmPassword = confirmPasswordField.getText().toString().trim();

        // Validate input fields
        if (code.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            resetPasswordButton.setEnabled(true);  // Re-enable the button if validation fails
            return;
        }

        // Check if new password is at least 8 characters long
        if (!newPassword.matches(".{8,}")) {
            Toast.makeText(this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
            resetPasswordButton.setEnabled(true);  // Re-enable the button if validation fails
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            resetPasswordButton.setEnabled(true);  // Re-enable the button if passwords don't match
            return;
        }

        // Send the reset password request
        String url = "https://gestureguide.com/auth/forgot_password.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    // On success
                    Toast.makeText(this, "Password reset successfully!", Toast.LENGTH_SHORT).show();
                    finish();  // Close the activity after successful reset
                },
                error -> {
                    // On error
                    if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                        // Handle 404 error
                        Toast.makeText(this, "Requested resource not found", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle other errors
                        Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    resetPasswordButton.setEnabled(true);  // Re-enable button if there's an error
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "reset_password");
                params.put("email", email);
                params.put("verification_code", code);
                params.put("new_password", newPassword);
                params.put("confirm_password", confirmPassword);
                return params;
            }
        };

        // Add the request to the queue
        Volley.newRequestQueue(this).add(request);
    }



}
