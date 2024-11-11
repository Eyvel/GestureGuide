package com.example.guestureguide;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class SignupForm extends AppCompatActivity {

    private static final String URL_SIGNUP = "http://192.168.8.20/gesture/signUpForm.php";

    // Declare your UI components
    private EditText lastName, firstName, middleName, ext, contactNumber, birthday, address, province, city, barangay, zipcode, lrn, program;
    private RadioGroup genderGroup, spedGroup, pwdGroup;
    private EditText fatherLastName, fatherFirstName, fatherOccupation, motherLastName,motherFirstName, motherOccupation, guardianName, guardianOccupation;
    private Button signupButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_form);

        // Initialize the EditText fields and other views
        lastName = findViewById(R.id.last_name);
        firstName = findViewById(R.id.first_name);
        middleName = findViewById(R.id.middle);
        ext = findViewById(R.id.ext);
        contactNumber = findViewById(R.id.contact_number);
        birthday = findViewById(R.id.birthday);
        address = findViewById(R.id.address);
        province = findViewById(R.id.province);
        city = findViewById(R.id.city);
        barangay = findViewById(R.id.barangay);
        zipcode = findViewById(R.id.zipcode);
        lrn = findViewById(R.id.lrn);
        program = findViewById(R.id.program);
        fatherLastName = findViewById(R.id.father_lastname);
        fatherFirstName = findViewById(R.id.father_firstname);
        fatherOccupation = findViewById(R.id.father_occupation);
        motherLastName = findViewById(R.id.mother_lastname);
        motherFirstName = findViewById(R.id.mother_firstname);
        motherOccupation = findViewById(R.id.mother_occupation);
        guardianName = findViewById(R.id.guardian_name);
        guardianOccupation = findViewById(R.id.guardian_occupation);
        genderGroup = findViewById(R.id.gender_group);
        spedGroup = findViewById(R.id.sped_group);
        pwdGroup = findViewById(R.id.pwd_group);
        signupButton = findViewById(R.id.signup_button);

        // Set an OnClickListener on the signup button
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
            }
        });
    }

    private void signup() {
        try {
            // Collect data from UI components
            HashMap<String, String> userData = new HashMap<>();
            userData.put("student_id", "108");  // Example static student ID, you may collect this dynamically if needed
            userData.put("user_name", firstName.getText().toString().trim() + "update");  // Example dynamic user name
            userData.put("first_name", firstName.getText().toString().trim());
            userData.put("middle_name", middleName.getText().toString().trim());
            userData.put("last_name", lastName.getText().toString().trim());
            userData.put("middle_initial", "P");  // Example static middle initial
            userData.put("ext", ext.getText().toString().trim());
            userData.put("contact_number", contactNumber.getText().toString().trim());
            userData.put("gender", "Male");  // Example static gender, modify this based on UI if needed
            userData.put("birthday", birthday.getText().toString().trim());
            userData.put("age", "24");  // Example static age, calculate dynamically if needed
            userData.put("sped", "No");  // Example static value
            userData.put("pwd", "No");  // Example static value
            userData.put("address", address.getText().toString().trim());
            userData.put("province", province.getText().toString().trim());
            userData.put("city", city.getText().toString().trim());
            userData.put("barangay", barangay.getText().toString().trim());
            userData.put("zipcode", zipcode.getText().toString().trim());
            userData.put("lrn", lrn.getText().toString().trim());
            userData.put("program", program.getText().toString().trim());
            userData.put("nationality", "Filipino");  // Example static nationality
            userData.put("school_name", "BSU");  // Example static school name
            userData.put("school_address", "Hagonoy");  // Example static school address
            userData.put("father_lastname", fatherLastName.getText().toString().trim());
            userData.put("father_firstname", fatherFirstName.getText().toString().trim());
            userData.put("father_middlename", "William");  // Example static father middle name
            userData.put("mother_lastname", motherLastName.getText().toString().trim());
            userData.put("mother_firstname", motherFirstName.getText().toString().trim());
            userData.put("mother_middlename", "Mary");  // Example static mother middle name
            userData.put("guardian_lastname", guardianName.getText().toString().trim());
            userData.put("guardian_firstname", "Sarah");  // Example static guardian first name
            userData.put("guardian_middlename", "Lee");  // Example static guardian middle name
            userData.put("guardian_contact_number", "098-765-4321");  // Example static guardian contact
            userData.put("mother_occupation", motherOccupation.getText().toString().trim());
            userData.put("father_occupation", fatherOccupation.getText().toString().trim());
            userData.put("civil_status", "Single");  // Example static civil status
            userData.put("profile_img", "profile_image.jpg");  // Example static profile image file name
            userData.put("created_at", "2024-11-10 14:25:00");  // Example static creation date

            // Convert HashMap to JSON object
            JSONObject jsonData = new JSONObject(userData);

            // Send the JSON data using Volley
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_SIGNUP, jsonData,
                    response -> {
                        Toast.makeText(SignupForm.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupForm.this, MainActivity.class);
                        startActivity(intent);
                    },
                    error -> {
                        // Log error response as plain text for debugging
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String errorMessage = new String(error.networkResponse.data);
                            Log.e("ServerError", errorMessage);
                            Toast.makeText(SignupForm.this, "Signup failed: " + errorMessage, Toast.LENGTH_LONG).show();
                        } else {
                            Log.e("ServerError", "No response from server or invalid response");
                            Toast.makeText(SignupForm.this, "Signup failed: No response from server", Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            // Set custom retry policy (timeout = 10 seconds, max retries = 1)
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000, // Timeout in milliseconds
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Max retries
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT // Backoff multiplier
            ));

            // Add the request to the RequestQueue
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            Log.e("SignupError", "Error during signup: " + e.getMessage());
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_LONG).show();
        }
    }




}
