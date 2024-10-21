package com.example.guestureguide;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StudentInformation extends AppCompatActivity {

    private EditText etUsername, etFirstName, etLastName, etMiddleName, etMiddleInitial, etSuffix, etBirthday, etNumber, etAddress;
    private Button btnUpdate, btnBack;
    private SharedPreferences sharedPreferences;

    private static final String URL_UPDATE = "http://192.168.100.72/gesture/studentInfo.php";  // Update PHP file URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_information);

        // Initialize views
        etUsername = findViewById(R.id.stud_username);
        etFirstName = findViewById(R.id.stud_firstname);
        etLastName = findViewById(R.id.stud_lastname);
        etMiddleName = findViewById(R.id.stud_middle);
        etMiddleInitial = findViewById(R.id.stud_initial);
        etSuffix = findViewById(R.id.stud_ext);
        etBirthday = findViewById(R.id.stud_birthday);
        etNumber = findViewById(R.id.stud_number);
        etAddress = findViewById(R.id.stud_address);
        btnUpdate = findViewById(R.id.stud_update_btn);
        btnBack = findViewById(R.id.back_btn);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyAppName", MODE_PRIVATE);

        // Load existing data from SharedPreferences
        loadStudentInformation();
        logSharedPreferences();

        // Set onClick listener for update button
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStudentInformation();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentInformation.this, NavigationActivity.class);  // Navigate to previous activity
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadStudentInformation() {
        // Populate fields with data from SharedPreferences
        etUsername.setText(sharedPreferences.getString("username", ""));
        etFirstName.setText(sharedPreferences.getString("firstName", ""));
        etLastName.setText(sharedPreferences.getString("lastName", ""));
        etMiddleName.setText(sharedPreferences.getString("middleName", ""));
        etMiddleInitial.setText(sharedPreferences.getString("middleInitial", ""));
        etSuffix.setText(sharedPreferences.getString("suffix", ""));
        etBirthday.setText(sharedPreferences.getString("birthday", ""));
        etNumber.setText(sharedPreferences.getString("number", ""));
        etAddress.setText(sharedPreferences.getString("address", ""));
    }

    private void updateStudentInformation() {
        // Get data from SharedPreferences
        final String user_id = sharedPreferences.getString("user_id", "").trim();
        final String apiKey = sharedPreferences.getString("apiKey", "").trim();

        // Get user input from EditText fields
        final String username = etUsername.getText().toString().trim();
        final String firstName = etFirstName.getText().toString().trim();
        final String lastName = etLastName.getText().toString().trim();
        final String middleName = etMiddleName.getText().toString().trim();
        final String middleInitial = etMiddleInitial.getText().toString().trim();
        final String suffix = etSuffix.getText().toString().trim();
        final String birthday = etBirthday.getText().toString().trim();
        final String number = etNumber.getText().toString().trim();
        final String address = etAddress.getText().toString().trim();

        // Validate input
        if (firstName.isEmpty() || lastName.isEmpty() || birthday.isEmpty() || number.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress dialog
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating...");
        progressDialog.show();

        // Send request to update student information
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            // Parse JSON response
                            JSONObject jsonResponse = new JSONObject(response);
                            String success = jsonResponse.getString("success");
                            String message = jsonResponse.getString("message");

                            if (success.equals("1")) {
                                Toast.makeText(StudentInformation.this, "Update Successful", Toast.LENGTH_LONG).show();

                                // Save updated data to SharedPreferences in a background thread
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("firstName", firstName);
                                        editor.putString("lastName", lastName);
                                        editor.putString("middleName", middleName);
                                        editor.putString("middleInitial", middleInitial);
                                        editor.putString("suffix", suffix);
                                        editor.putString("birthday", birthday);
                                        editor.putString("number", number);
                                        editor.putString("address", address);
                                        editor.putString("username", username);
                                        editor.apply();

                                        // Load data back on the UI thread
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                loadStudentInformation();
                                            }
                                        });
                                    }
                                }).start();
                            } else {
                                Toast.makeText(StudentInformation.this, "Update Failed: " + message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            // Handle JSON parsing error
                            Toast.makeText(StudentInformation.this, "Update Failed: Invalid JSON response", Toast.LENGTH_LONG).show();
                            Log.e("JSONException", "Error parsing JSON response: " + e.toString());
                            Log.d("ServerResponse", "Response: " + response);  // Log the raw response
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(StudentInformation.this, "Update Failed: " + error.toString(), Toast.LENGTH_LONG).show();
                        Log.e("VolleyError", "Error: " + error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Parameters to send to the server
                Map<String, String> params = new HashMap<>();
                params.put("firstName", firstName);
                params.put("lastName", lastName);
                params.put("middleName", middleName);
                params.put("middleInitial", middleInitial);
                params.put("suffix", suffix);
                params.put("birthday", birthday);
                params.put("number", number);
                params.put("address", address);
                params.put("username", username);
                params.put("user_id", user_id);  // Assuming you're sending user_id
                params.put("apiKey", apiKey);  // Assuming you're sending apiKey
                return params;
            }
        };

        // Set retry policy for the request
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add request to the request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void logSharedPreferences() {
        // Log all SharedPreferences data for debugging
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("SharedPreferences", entry.getKey() + ": " + entry.getValue().toString());
        }
    }
}
