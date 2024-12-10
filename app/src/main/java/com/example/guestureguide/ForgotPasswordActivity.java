package com.example.guestureguide;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText emailField;
    private Button sendCodeButton;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailField = findViewById(R.id.emailField);
        sendCodeButton = findViewById(R.id.sendVerificationCodeButton);

        backButton = findViewById(R.id.back_to_login_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sendCodeButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Disable the button immediately to prevent multiple clicks
            sendCodeButton.setEnabled(false);

            sendVerificationCode(email);
        });
    }

    private void sendVerificationCode(String email) {
        String url = "https://gestureguide.com/auth/mobile/forgot_password.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Parse JSON response and navigate to the next activity
                    Toast.makeText(this, "Verification code sent!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ForgotPasswordActivity.this, VerifyCodeActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();  // Optional: Close the current activity if needed
                },
                error -> {
                    // Handle error response
                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    sendCodeButton.setEnabled(true);  // Re-enable the button in case of error
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "request_code");
                params.put("email", email);
                return params;
            }
        };

        // Set custom retry policy to prevent multiple retries
        request.setRetryPolicy(new DefaultRetryPolicy(
                5000, // Timeout in milliseconds
                0, // No retries
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        // Add the request to the Volley queue
        Volley.newRequestQueue(this).add(request);
    }

}
