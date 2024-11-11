package com.example.guestureguide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.net.URLEncoder;

public class EmailVerificationActivity extends AppCompatActivity {

    private EditText verificationCodeEditText;
    private String emailAddress;
    private Button submitVerificationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppName", MODE_PRIVATE);
        emailAddress = sharedPreferences.getString("email", "").trim();

        // Initialize the views
        verificationCodeEditText = findViewById(R.id.verifCodeInput);
        submitVerificationButton = findViewById(R.id.submitVerificationButton);

        submitVerificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user inputs
                String verificationCode = verificationCodeEditText.getText().toString().trim();

                // Validate inputs
                if (TextUtils.isEmpty(verificationCode)) {
                    Toast.makeText(EmailVerificationActivity.this, "Please enter the verification code", Toast.LENGTH_SHORT).show();
                } else {
                    // Send the email and verification code to the server
                    sendVerificationDataToServer(verificationCode);
                }
            }
        });
    }

    private void sendVerificationDataToServer(final String verificationCode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Construct the URL for the PHP script
                    URL url = new URL("http://192.168.8.20/gesture/verification_code.php");

                    // Prepare the data to send
                    String data = "email=" + URLEncoder.encode(emailAddress, "UTF-8") +
                            "&verification_code=" + URLEncoder.encode(verificationCode, "UTF-8");

                    // Open a connection to the PHP server
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    // Send the data to the server
                    connection.getOutputStream().write(data.getBytes("UTF-8"));

                    // Get the response from the server
                    int responseCode = connection.getResponseCode();
                    Log.d("Email", "email "+emailAddress);
                    Log.d("VerificationRequest", "Response Code: " + responseCode);

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder responseBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            responseBuilder.append(line);
                        }

                        String responseBody = responseBuilder.toString();
                        Log.d("VerificationResponse", responseBody);

                        // Parse JSON response
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        // Handle success or failure based on response
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if ("success".equals(status)) {
                                    Toast.makeText(EmailVerificationActivity.this, message, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(EmailVerificationActivity.this, TeacherIDInputActivity.class);
                                    startActivity(intent);
                                    finish();  // Finish current activity to prevent going back
                                } else {
                                    Toast.makeText(EmailVerificationActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Log.d("VerificationResponse", "Server responded with code: " + responseCode);
                    }

                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EmailVerificationActivity.this, "Error occurred during verification.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}
