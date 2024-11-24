package com.example.guestureguide;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class SignupForm extends AppCompatActivity {

    private static final String URL_SIGNUP = "https://gestureguide.com/auth/mobile/signUpForm.php";
    private static final int PROFILE_PICTURE_REQUEST = 101; // Unique request code
    private String profileImageUri = "default"; // Default profile image

    // Declare your UI components
    private EditText lastName, firstName, middleName, ext, contactNumber, address, province, city, barangay, zipcode, lrn, program,nationality, schoolName, schoolAddress;
    private RadioGroup genderGroup, spedGroup, pwdGroup;
    private TextView birthday;
    private EditText fatherLastName, fatherFirstName, fatherMiddleName,fatherOccupation, motherLastName, motherFirstName,motherMiddleName, motherOccupation, guardianName, guardianOccupation;
    private EditText guardianLastName,guardianFirstName,guardianMiddleName,guardianContactNumber;
    private Button signupButton;
    private Spinner civilStatusSpinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_form);
        // Initialize civilStatusSpinner
        civilStatusSpinner = findViewById(R.id.civilStatusSpinner);

        // Define options for the civil status
        String[] civilStatusOptions = {"Single", "Married", "Widowed", "Divorced"};

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, civilStatusOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter to the spinner
        civilStatusSpinner.setAdapter(adapter);


        // Initialize the EditText fields and other views
        lastName = findViewById(R.id.last_name);
        firstName = findViewById(R.id.first_name);
        middleName = findViewById(R.id.middle);
        ext = findViewById(R.id.ext);
        contactNumber = findViewById(R.id.contact_number);
        birthday = findViewById(R.id.birthday);
        birthday.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, month, dayOfMonth) -> {
                String formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                birthday.setText(formattedDate);
            }, 2000, 0, 1); // Default date: January 1, 2000
            datePickerDialog.show();
        });

        address = findViewById(R.id.address);
        province = findViewById(R.id.province);
        city = findViewById(R.id.city);
        barangay = findViewById(R.id.barangay);
        zipcode = findViewById(R.id.zipcode);
        lrn = findViewById(R.id.lrn);
        program = findViewById(R.id.program);
        nationality =findViewById(R.id.nationality);

        schoolName = findViewById(R.id.schoolName);
        schoolAddress = findViewById(R.id.schoolAddress);
        fatherLastName = findViewById(R.id.father_lastname);
        fatherFirstName = findViewById(R.id.father_firstname);
        fatherMiddleName = findViewById(R.id.father_middlename);

        fatherOccupation = findViewById(R.id.father_occupation);
        motherLastName = findViewById(R.id.mother_lastname);
        motherFirstName = findViewById(R.id.mother_firstname);

        motherMiddleName = findViewById(R.id.mother_middlename);
        motherOccupation = findViewById(R.id.mother_occupation);
        guardianLastName = findViewById(R.id.guardian_lastName);
        guardianMiddleName = findViewById(R.id.guardians_middlename);
        guardianFirstName = findViewById(R.id.guardians_firstname);

        guardianContactNumber = findViewById(R.id.guardians_number);




        guardianOccupation = findViewById(R.id.guardian_occupation);
        genderGroup = findViewById(R.id.gender_group);
        spedGroup = findViewById(R.id.sped_group);
        pwdGroup = findViewById(R.id.pwd_group);
        signupButton = findViewById(R.id.signup_button);

        // Set an OnClickListener on the signup button
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        Button selectProfilePictureButton = findViewById(R.id.signup_button);
        selectProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignupForm.this, "Button clicked!", Toast.LENGTH_SHORT).show();



                signup();


            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PROFILE_PICTURE_REQUEST && resultCode == RESULT_OK && data != null) {
            profileImageUri = data.getStringExtra("profile_image_uri");
            Toast.makeText(this, "Profile picture selected: " + profileImageUri, Toast.LENGTH_SHORT).show();
        }
    }

    private void signup() {
        Log.d("SignupDebug", "Entered signup method.");

        try {
            SharedPreferences sharedPreferences = getSharedPreferences("MyAppName", Context.MODE_PRIVATE);
            String userId = sharedPreferences.getString("user_id",""); // Retrieve the user_id
            // Collect data from UI components
            HashMap<String, String> userData = new HashMap<>();
            userData.put("student_id", userId);  // Example static student ID, you may collect this dynamically if needed
            userData.put("first_name", firstName.getText().toString().trim());
            userData.put("middle_name", middleName.getText().toString().trim());
            String middleNameText = middleName.getText().toString().trim();


            userData.put("middle_name", middleNameText);
            if (!middleNameText.isEmpty()) {
                // Get the first letter of the middle name and store it as the middle initial
                userData.put("middle_initial", String.valueOf(middleNameText.charAt(0)).toUpperCase());
            } else {
                // Handle the case where there is no middle name (optional)
                userData.put("middle_initial", ""); // Or whatever default value you prefer
            }

// Store the full middle name
            userData.put("last_name", lastName.getText().toString().trim());
            userData.put("ext", ext.getText().toString().trim());
            userData.put("contact_number", contactNumber.getText().toString().trim());
            userData.put("gender", "Male");  // Example static gender, modify this based on UI if needed
            userData.put("birthday", birthday.getText().toString().trim());


            // Log the HashMap contents before converting it to JSON
            Log.d("SignupDebug", "User Data: " + userData.toString());
            String birthdayText = birthday.getText().toString().trim();

// Validate format before proceeding
            if (birthdayText.isEmpty() || !birthdayText.matches("\\d{4}-\\d{2}-\\d{2}")) {
                Toast.makeText(this, "Please enter the birthday in yyyy-MM-dd format", Toast.LENGTH_LONG).show();
                return; // Stop the signup process
            }
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate birthDate = LocalDate.parse(birthdayText, formatter);
                LocalDate currentDate = LocalDate.now();

                int age = Period.between(birthDate, currentDate).getYears();
                userData.put("age", String.valueOf(age)); // Add dynamically calculated age
                Log.d("SignupDebug", "Calculated Age: " + age);
            } catch (Exception e) {
                Log.e("SignupError", "Error parsing birthday or calculating age: " + e.getMessage());
                userData.put("age", "0"); // Default age in case of error
            }


            // Get Gender
            RadioGroup genderGroup = findViewById(R.id.gender_group);
            int selectedGenderId = genderGroup.getCheckedRadioButtonId();
            if (selectedGenderId != -1) {
                RadioButton selectedGender = findViewById(selectedGenderId);
                userData.put("gender", selectedGender.getText().toString());
            }

// Get SPED
            RadioGroup spedGroup = findViewById(R.id.sped_group);
            int selectedSpedId = spedGroup.getCheckedRadioButtonId();
            if (selectedSpedId != -1) {
                RadioButton selectedSped = findViewById(selectedSpedId);
                userData.put("sped", selectedSped.getText().toString());
            }

// Get PWD
            RadioGroup pwdGroup = findViewById(R.id.pwd_group);
            int selectedPwdId = pwdGroup.getCheckedRadioButtonId();
            if (selectedPwdId != -1) {
                RadioButton selectedPwd = findViewById(selectedPwdId);
                userData.put("pwd", selectedPwd.getText().toString());
            }

            String selectedCivilStatus = civilStatusSpinner.getSelectedItem().toString();
            userData.put("civilStatus", selectedCivilStatus);


            userData.put("address_house", address.getText().toString().trim());
            userData.put("province", province.getText().toString().trim());
            userData.put("city", city.getText().toString().trim());
            userData.put("barangay", barangay.getText().toString().trim());
            userData.put("zipcode", zipcode.getText().toString().trim());
            userData.put("lrn", lrn.getText().toString().trim());
            userData.put("program", program.getText().toString().trim());
            userData.put("nationality", nationality.getText().toString().trim());  // Example static nationality
            userData.put("school_name", schoolName.getText().toString().trim());  // Example static school name
            userData.put("school_address", schoolAddress.getText().toString().trim());   // Example static school address
            userData.put("father_lastname", fatherLastName.getText().toString().trim());
            userData.put("father_firstname", fatherFirstName.getText().toString().trim());
            userData.put("father_middlename", fatherMiddleName.getText().toString().trim());  // Example static father middle name
            userData.put("mother_lastname", motherLastName.getText().toString().trim());
            userData.put("mother_firstname", motherFirstName.getText().toString().trim());
            userData.put("mother_middlename", motherMiddleName.getText().toString().trim());  // Example static father middle name

            userData.put("guardian_lastname", guardianLastName.getText().toString().trim());
            userData.put("guardian_firstname", guardianFirstName.getText().toString().trim());

            userData.put("guardian_middlename", guardianLastName.getText().toString().trim());

          userData.put("guardian_contact_number", guardianContactNumber.getText().toString().trim());  // Example static guardian contact
            userData.put("mother_occupation", motherOccupation.getText().toString().trim());
            userData.put("father_occupation", fatherOccupation.getText().toString().trim());
            userData.put("civil_status",selectedCivilStatus );  // Example static civil status
            userData.put("profile_img", "" +
                    "auth/mobile/uploads/profile_images/profile.png");
            userData.put("created_at", "");  // Example static creation date

            // Convert HashMap to JSON object
            JSONObject jsonData = new JSONObject(userData);

            // Log the data being sent
            Log.d("SignupData", jsonData.toString());


            // Log the JSON data being sent
            Log.d("SignupRequest", "JSON request being sent to server: " + jsonData.toString());
            // Send the JSON data using Volley
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            // Send the JSON data using Volley
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_SIGNUP, jsonData,
                    response -> {
                        try {
                            // Parse the "status" and "message" from the server response
                            String status = response.getString("status");
                            String message = response.getString("message");

                            if ("success".equals(status)) {
                                // If signup is successful
                                Toast.makeText(SignupForm.this, "Signup successful: " + message, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignupForm.this, ProfilePictureActivity.class);
                                startActivityForResult(intent, PROFILE_PICTURE_REQUEST);

                            } else {
                                // If signup fails
                                Toast.makeText(SignupForm.this, "Signup failed: " + message, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            // Handle JSON parsing error
                            Log.e("SignupError", "Error parsing server response: " + e.getMessage());
                            Toast.makeText(SignupForm.this, "Error processing server response", Toast.LENGTH_LONG).show();
                        }
                    },
                    error -> {
                        // Handle Volley error response
                        String errorMessage = "Unknown error";
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            errorMessage = new String(error.networkResponse.data);
                            Log.e("ServerError", errorMessage);
                        } else {
                            Log.e("ServerError", "No response from server or invalid response");
                        }
                        Toast.makeText(SignupForm.this, "Signup failed: " + errorMessage, Toast.LENGTH_LONG).show();
                    }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };


            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    20000,  // Timeout in milliseconds (20 seconds)
                    0,      // No retries
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            Log.e("SignupError", "Error during signup: " + e.getMessage());
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_LONG).show();
        }
    }
}
