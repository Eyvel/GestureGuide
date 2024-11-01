package com.example.guestureguide;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ForgetPassword extends AppCompatActivity {

    private EditText forgetPasswordEmail;
    private EditText forgetPasswordNew;
    private EditText forgetPasswordConfirm;
    private Button forgetPasswordButton; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        // Initialize the views
        forgetPasswordEmail = findViewById(R.id.forget_password_email);
        forgetPasswordNew = findViewById(R.id.forget_password_new);
        forgetPasswordConfirm = findViewById(R.id.forget_password_confirm);
        forgetPasswordButton = findViewById(R.id.forget_password_button);

        // Set an OnClickListener for the update button
        forgetPasswordButton.setOnClickListener(v -> handleUpdate());
    }

    private void handleUpdate() {
        // Handle the update logic here
        String email = forgetPasswordEmail.getText().toString();
        String newPassword = forgetPasswordNew.getText().toString();
        String confirmPassword = forgetPasswordConfirm.getText().toString();

        // Perform validation and update logic
        if (newPassword.equals(confirmPassword)) {
            // Proceed with updating the password
            // Show success message or handle further logic
        } else {
            // Show error message for password mismatch
        }
    }
}
