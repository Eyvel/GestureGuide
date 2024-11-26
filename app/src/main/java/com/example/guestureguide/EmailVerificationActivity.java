package com.example.guestureguide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private Button submitVerificationButton,resendVerificationButton;
    private CountDownTimer countDownTimer;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppName", MODE_PRIVATE);
        emailAddress = sharedPreferences.getString("email", "").trim();

        // Initialize the views
        verificationCodeEditText = findViewById(R.id.verifCodeInput);
        submitVerificationButton = findViewById(R.id.submitVerificationButton);
        resendVerificationButton = findViewById(R.id.resendVerificationButton);

        backButton = findViewById(R.id.back_to_sign_up_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

        resendVerificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Disable the button and start countdown
                resendVerificationButton.setEnabled(false);

                // Start a 60 second countdown
                startResendCountdown();
                // Resend the verification code to the server
                resendVerificationCodeToServer();
            }
        });
    }


    private void startResendCountdown() {
        countDownTimer = new CountDownTimer(60000, 1000) { // 60 seconds countdown
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the button text with the remaining time
                resendVerificationButton.setText("Resend in " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                // Re-enable the button and reset the text
                resendVerificationButton.setText("Resend Code");
                resendVerificationButton.setEnabled(true);
            }
        };

        countDownTimer.start();
    }

    private void sendVerificationDataToServer(final String verificationCode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Construct the URL for the PHP script
                    URL url = new URL("https://gestureguide.com/auth/mobile/verification_code.php");

                    // Prepare the data to send
                    String data = "email=" + URLEncoder.encode(emailAddress, "UTF-8") +
                            "&verification_code=" + URLEncoder.encode(verificationCode, "UTF-8");

                    // Open a connection to the PHP server
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    // Send the data to the server
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(data.getBytes("UTF-8"));
                    outputStream.close();

                    // Get the response from the server
                    int responseCode = connection.getResponseCode();
                    Log.d("Email", "email " + emailAddress);
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

                        // Retrieve user type
                        String userType = jsonResponse.optString("user_type", "unknown");

                        // Handle success or failure based on response
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if ("success".equals(status)) {
                                    Toast.makeText(EmailVerificationActivity.this, message, Toast.LENGTH_SHORT).show();

                                    // Navigate based on user type
                                    if ("user".equalsIgnoreCase(userType)) {
                                        Intent intent = new Intent(EmailVerificationActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    } else if ("student".equalsIgnoreCase(userType)) {
                                        Intent intent = new Intent(EmailVerificationActivity.this, SignupForm.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(EmailVerificationActivity.this, "Invalid user type", Toast.LENGTH_SHORT).show();
                                    }

                                    finish(); // Finish current activity to prevent going back
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
    private void resendVerificationCodeToServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Construct the URL for the PHP script to resend verification code
                    URL url = new URL("https://gestureguide.com/auth/mobile/resend_verification_code.php");

                    // Prepare the data to send (email address)
                    String data = "email=" + URLEncoder.encode(emailAddress, "UTF-8")+ "&action=resend";

                    // Open a connection to the PHP server
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    // Send the data to the server
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(data.getBytes("UTF-8"));
                    outputStream.close();

                    // Get the response from the server
                    int responseCode = connection.getResponseCode();
                    Log.d("ResendVerification", "Response Code: " + responseCode);

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder responseBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            responseBuilder.append(line);
                        }

                        String responseBody = responseBuilder.toString();
                        Log.d("ResendVerificationResponse", responseBody);

                        // Parse the JSON response
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        // Handle success or failure based on response
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if ("success".equals(status)) {
                                    Toast.makeText(EmailVerificationActivity.this, message, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(EmailVerificationActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Log.d("ResendVerificationResponse", "Server responded with code: " + responseCode);
                    }

                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EmailVerificationActivity.this, "Error occurred while resending the verification code.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

}
