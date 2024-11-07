package com.example.guestureguide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TeacherIDInputActivity extends AppCompatActivity {

    // Declare the EditText, Button, and Spinner
    EditText editTeacherID;
    Button submitTeacherID;
    Spinner userTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_id_input);

        // Initialize the views
        editTeacherID = findViewById(R.id.numberInput);
        submitTeacherID = findViewById(R.id.sendTeacherIdBtn);
        userTypeSpinner = findViewById(R.id.userTypeSpinner);

        // Set an OnItemSelectedListener for the spinner
        userTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedUserType = userTypeSpinner.getSelectedItem().toString();

                if (selectedUserType.equals("User")) {
                    editTeacherID.setVisibility(View.GONE); // Hide EditText
                } else if (selectedUserType.equals("Student")) {
                    editTeacherID.setVisibility(View.VISIBLE); // Show EditText
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Set an OnClickListener for the button
        submitTeacherID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String teacherID = editTeacherID.getText().toString().trim();
                String selectedUserType = userTypeSpinner.getSelectedItem().toString();

                if (selectedUserType.equals("Student")) {
                    if (teacherID.isEmpty()) {
                        Toast.makeText(TeacherIDInputActivity.this, "Please enter your Teacher ID.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Send user_id and teacher_id to PHP
                        SharedPreferences sharedPreferences = getSharedPreferences("MyAppName", MODE_PRIVATE);
                        String userId = sharedPreferences.getString("user_id", "").trim(); // Replace this with the actual user ID (from session or login)
                        sendTeacherIDToServer(userId, teacherID);
                    }
                } else if (selectedUserType.equals("User")) {
                    // For "User", navigate back to the login screen
                    Toast.makeText(TeacherIDInputActivity.this, "Signed in as User.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(TeacherIDInputActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Finish this activity
                }
            }
        });
    }

    private void sendTeacherIDToServer(final String userId, final String teacherID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Create URL connection to the PHP script
                    URL url = new URL("http://192.168.8.20/gesture/createClasses.php");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/json");

                    // Create JSON data
                    String jsonInputString = "{\"user_id\": \"" + userId + "\", \"teacher_id\": \"" + teacherID + "\"}";
                    Log.d("id",userId);
                    Log.d("teacher_id",teacherID);

                    // Send JSON data to server
                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    // Get response from server
                    int code = connection.getResponseCode();
                    if (code == HttpURLConnection.HTTP_OK) {
                        // Response is successful, handle success
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(TeacherIDInputActivity.this, "Teacher ID sent successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // Handle server error
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(TeacherIDInputActivity.this, "Failed to send data", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TeacherIDInputActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}
