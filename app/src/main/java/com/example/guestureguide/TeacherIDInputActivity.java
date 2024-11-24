package com.example.guestureguide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class TeacherIDInputActivity extends AppCompatActivity {

    private EditText editTeacherID;
    private Button submitTeacherID;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_id_input);

        sharedPreferences = getSharedPreferences("MyAppName", MODE_PRIVATE); // Initialize SharedPreferences

        initViews();

        setupSubmitButton();
    }

    private void initViews() {
        editTeacherID = findViewById(R.id.numberInput);
        submitTeacherID = findViewById(R.id.sendTeacherIdBtn);
    }





    private void setupSubmitButton() {
        submitTeacherID.setOnClickListener(v -> {
            String teacherID = editTeacherID.getText().toString().trim();

            if (teacherID.isEmpty()) {
                showToast("Please enter your Teacher ID.");
            } else {
                sendTeacherIDToServer(getUserId(), teacherID);
                logoutUser();
                finish();
            }
        });
    }

    private String getUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppName", MODE_PRIVATE);
        return sharedPreferences.getString("user_id", "").trim();
    }

    private void sendTeacherIDToServer(final String userId, final String teacherID) {
        new Thread(() -> {
            try {
                URL url = new URL("https://gestureguide.com/auth/mobile/createClasses.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                // Create JSON data
                String jsonInputString = "{\"user_id\": \"" + userId + "\", \"teacher_id\": \"" + teacherID + "\"}";
                Log.d("id", userId);
                Log.d("teacher_id", teacherID);

                // Send JSON data to server
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int code = connection.getResponseCode();
                if (code == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    Log.d("ServerResponse", response.toString());

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    String status = jsonResponse.optString("status", "error");
                    String message = jsonResponse.optString("message", "Unexpected error occurred");

                    runOnUiThread(() -> {
                        if ("success".equals(status)) {
                            Toast.makeText(TeacherIDInputActivity.this, "Teacher ID sent successfully", Toast.LENGTH_SHORT).show();
                            // Now check the user's approval status
                            checkUserStatus(userId);
                        } else if ("error".equals(status) && "Teacher ID does not exist".equals(message)) {
                            // Show error message if Teacher ID is invalid
                            Toast.makeText(TeacherIDInputActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TeacherIDInputActivity.this, "Failed to send data: " + message, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(TeacherIDInputActivity.this, "Server error with code: " + code, Toast.LENGTH_SHORT).show());
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(TeacherIDInputActivity.this, "Error occurred", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }


    private HttpURLConnection createPostConnection(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        return connection;
    }

    private void sendJsonData(HttpURLConnection connection, JSONObject jsonData) throws Exception {
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonData.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }
    }

    private void checkUserStatus(final String studentId) {
        new Thread(() -> {
            try {
                HttpURLConnection connection = createGetConnection("https://gestureguide.com/auth/mobile/checkStatus.php?student_id=" + studentId);
                String response = readServerResponse(connection);
                Log.d("RawResponse", response.toString());

                JSONObject jsonResponse = new JSONObject(response);
                handleStatusResponse(jsonResponse);

                connection.disconnect();
            } catch (Exception e) {
                handleError(e, "Error occurred while checking user status.");
            }
        }).start();
    }

    private HttpURLConnection createGetConnection(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        return connection;
    }

    private String readServerResponse(HttpURLConnection connection) throws Exception {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    private void handleStatusResponse(JSONObject jsonResponse) throws JSONException {
        String status = jsonResponse.optString("status", "error");

        runOnUiThread(() -> {
            if ("approved".equals(status)) {
                navigateToMainScreen("Access approved.");
            } else if ("pending".equals(status)) {
                navigateToScreen(WaitingScreen.class);
            } else {
                String message = jsonResponse.optString("message", "Unexpected error occurred");
                showToast("Error: " + message);
            }
        });
    }

    private void navigateToMainScreen(String message) {
        showToast(message);
        navigateToScreen(MainActivity.class);
    }

    private void navigateToScreen(Class<?> targetActivity) {
        Intent intent = new Intent(TeacherIDInputActivity.this, targetActivity);
        startActivity(intent);
        finish();
    }

    private void handleError(Exception e, String errorMessage) {
        Log.e("Error", errorMessage, e);
        runOnUiThread(() -> showToast(errorMessage));
    }

    private void showToastOnUi(String message) {
        runOnUiThread(() -> showToast(message));
    }

    private void showToast(String message) {
        Toast.makeText(TeacherIDInputActivity.this, message, Toast.LENGTH_SHORT).show();
    }
    private void logoutUser() {
        String url_logout = "https://gestureguide.com/auth/mobile/logout.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_logout,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("SharedPreferences", "Email: " + sharedPreferences.getString("email", ""));

                        Log.d("LogoutResponse", response); // Log the response for debugging
                        if (response.trim().equals("1")) {  // Use trim() to remove any whitespace
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.apply();

                            Intent intent = new Intent(TeacherIDInputActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(TeacherIDInputActivity.this, "Logout failed: " + response, Toast.LENGTH_SHORT).show();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", sharedPreferences.getString("email", ""));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
