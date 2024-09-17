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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

public class StudentInformation extends AppCompatActivity {

    private EditText etUsername,etFirstName, etLastName, etMiddleName, etMiddleInitial, etSuffix, etBirthday, etNumber, etAddress, etEmail;
    private Button btnUpdate, btnBack;
    private SharedPreferences sharedPreferences;

    private static final String URL_UPDATE = "http://192.168.8.6/capstone_test/studentInfo.php";  // update php file

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
        btnUpdate = findViewById(R.id.stud_update_btn);btnBack = findViewById(R.id.back_btn);

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
                Intent intent = new Intent(StudentInformation.this, NavigationActivity.class);//will go to log in fragment
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
        etEmail.setText(sharedPreferences.getString("email",""));

    }

    private void updateStudentInformation() {
        final String email = sharedPreferences.getString("email", "").trim();
        final String apiKey = sharedPreferences.getString("apiKey", "").trim();
        final String username = etUsername.getText().toString().trim();
        final String firstName = etFirstName.getText().toString().trim();
        final String lastName = etLastName.getText().toString().trim();
        final String middleName = etMiddleName.getText().toString().trim();
        final String middleInitial = etMiddleInitial.getText().toString().trim();
        final String suffix = etSuffix.getText().toString().trim();
        final String birthday = etBirthday.getText().toString().trim();
        final String number = etNumber.getText().toString().trim();
        final String address = etAddress.getText().toString().trim();
        final String userId = sharedPreferences.getString("userId", ""); // Assuming userId is saved in SharedPreferences

        Log.d("UpdateData", "firstName: " + firstName);
        Log.d("UpdateData", "lastName: " + lastName);
        Log.d("UpdateData", "middleName: " + middleName);
        Log.d("UpdateData", "middleInitial: " + middleInitial);
        Log.d("UpdateData", "suffix: " + suffix);
        Log.d("UpdateData", "birthday: " + birthday);
        Log.d("UpdateData", "number: " + number);
        Log.d("UpdateData", "address: " + address);
        Log.d("UpdateData", "userId: " + userId);
        Log.d("UpdateData", "email: " + email);
        Log.d("UpdateData", "api: " + apiKey);
        Log.d("UpdateData", "usernamE: " + username);



        if (firstName.isEmpty() || lastName.isEmpty() || birthday.isEmpty() || number.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();
                        Toast.makeText(StudentInformation.this, "Update Successful", Toast.LENGTH_LONG).show();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("firstName", firstName);
                        editor.putString("lastName", lastName);
                        editor.putString("middleName", middleName);
                        editor.putString("middleInitial", middleInitial);
                        editor.putString("suffix", suffix);
                        editor.putString("birthday", birthday);
                        editor.putString("number", number);
                        editor.putString("address", address);
                        editor.putString("email", email);
                        editor.putString("username", username);
                        editor.apply();

                        loadStudentInformation();//load again to show the updated data on the app
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(StudentInformation.this, "Update Failed: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userId", userId);
                params.put("firstName", firstName);
                params.put("lastName", lastName);
                params.put("middleName", middleName);
                params.put("middleInitial", middleInitial);
                params.put("suffix", suffix);
                params.put("birthday", birthday);
                params.put("number", number);
                params.put("address", address);
                params.put("email", email);
                params.put("apiKey", apiKey);
                params.put("username", username);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);




    }private void logSharedPreferences() {
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("SharedPreferences", entry.getKey() + ": " + entry.getValue().toString());
            Log.d("SharedPreferences", "First Name: " + sharedPreferences.getString("firstName", ""));
            Log.d("SharedPreferences", "Last Name: " + sharedPreferences.getString("lastName", ""));
            Log.d("SharedPreferences", "Middle Name: " + sharedPreferences.getString("middleName", ""));
            Log.d("SharedPreferences", "email: " + sharedPreferences.getString("email", ""));


        }
    }
}
