package com.example.guestureguide;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StudentInformation extends AppCompatActivity {
    private ImageButton backButton;

    private EditText etLastName, etFirstName, etMiddleName, etSuffix, etContactNumber,  etAddress,etAddressBarangay;
    private EditText etProvince,etMunicipality,etBarangay,etZipCode,etLrn,etProgram,etNationality,etSchoolAddress,etSchoolName;
    private EditText etFatherLastName, etFatherFirstName,etFatherMiddleName,etFatherOccupation,etMotherLastName,etMotherFirstName,etMotherMiddleName,etMotherOccupation;
    private EditText etGuardianLastName,etGuardianFirstName,etGuardianMiddleName,etGuardianContactNumber;
    private RadioGroup rgGender, rgSped, rgPwd;
    private RadioButton rbMale, rbFemale, rbOther, rb_yes_sped,rb_no_sped,rb_yes_pwd,rb_no_pwd;
    private Button btnUpdate, btnBack;
    private SharedPreferences sharedPreferences;
    private TextView tvBirthdate;
    private String user_id;

    private static final String URL_FETCH = "https://gestureguide.com/auth/mobile/fetch_student_profile.php";
    private static final String URL_UPDATE = "https://gestureguide.com/auth/mobile/updateStudentInfo.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_information);

        backButton = findViewById(R.id.back_to_profile_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sharedPreferences = getSharedPreferences("MyAppName", MODE_PRIVATE);
        user_id = sharedPreferences.getString("user_id", "").trim();

        // Initialize UI components
        etLastName = findViewById(R.id.et_last_name);
        etFirstName = findViewById(R.id.et_first_name);
        etMiddleName = findViewById(R.id.et_middle_name);
        etSuffix = findViewById(R.id.et_extension);
        etProvince = findViewById(R.id.et_province);
        etMunicipality = findViewById(R.id.et_municipality);
        etZipCode = findViewById(R.id.et_zip_code);

        etProgram = findViewById(R.id.et_program);
        etLrn = findViewById(R.id.et_lrn);
        etNationality = findViewById(R.id.et_nationality);
        etSchoolAddress = findViewById(R.id.et_school_address);
        etSchoolName = findViewById(R.id.et_school_name);

        
        etFatherLastName = findViewById(R.id.et_father_last_name);
        etFatherFirstName = findViewById(R.id.et_father_first_name);
        etFatherMiddleName = findViewById(R.id.et_father_middle_name);
        etFatherOccupation = findViewById(R.id.et_father_occupation);

        etMotherLastName = findViewById(R.id.et_mother_last_name);
        etMotherFirstName = findViewById(R.id.et_mother_first_name);
        etMotherMiddleName = findViewById(R.id.et_mother_middle_name);
        etMotherOccupation = findViewById(R.id.et_mother_occupation);


        etGuardianLastName = findViewById(R.id.et_guardian_last_name);
        etGuardianFirstName = findViewById(R.id.et_guardian_first_name);
        etGuardianMiddleName = findViewById(R.id.et_guardian_middle_name);
        etGuardianContactNumber = findViewById(R.id.et_guardian_contact_number);


        etContactNumber = findViewById(R.id.et_contact_number);
        tvBirthdate = findViewById(R.id.tv_birth_date);
        etAddress = findViewById(R.id.et_address);

        etProvince = findViewById(R.id.et_province);
        etMunicipality = findViewById(R.id.et_municipality);
        etAddressBarangay = findViewById(R.id.et_address_barangay);

        rgGender = findViewById(R.id.rg_gender);
        rbMale = findViewById(R.id.rb_male);
        rbFemale = findViewById(R.id.rb_female);
        rbOther = findViewById(R.id.rb_other);

        rgSped = findViewById(R.id.rg_sped);
//        rb_yes_sped =findViewById(R.id.rb_sped_yes);
//        rb_no_sped =findViewById(R.id.rb_sped_no);
        rgPwd = findViewById(R.id.rg_pwd);

//        rb_yes_pwd =findViewById(R.id.rb_pwd_yes);
//        rb_no_pwd = findViewById(R.id.rb_pwd_no);
        btnUpdate = findViewById(R.id.btn_update);
        btnBack = findViewById(R.id.btn_back);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Fetch and display student information
        fetchStudentInfo();

        // Update student information on button click
        btnUpdate.setOnClickListener(v -> updateStudentInfo());

        // Back button functionality
        btnBack.setOnClickListener(v -> finish());


        tvBirthdate = findViewById(R.id.tv_birth_date);
        tvBirthdate.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, month, dayOfMonth) -> {
                String formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                tvBirthdate.setText(formattedDate);
            }, 2000, 0, 1); // Default date: January 1, 2000
            datePickerDialog.show();
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Always fetch student info whenever the activity resumes
        fetchStudentInfo();
    }


    private void fetchStudentInfo() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching student information...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_FETCH + "?student_id=" + user_id,
                response -> {
                    progressDialog.dismiss();
                    try {
                        // Log the response for debugging
                        Log.d("ServerResponse", response);

                        // Parse the JSON object
                        JSONObject jsonObject = new JSONObject(response);

                        // Populate fields if keys exist
                        etLastName.setText(jsonObject.optString("last_name", ""));
                        etFirstName.setText(jsonObject.optString("first_name", ""));
                        etMiddleName.setText(jsonObject.optString("middle_name", ""));
                        etSuffix.setText(jsonObject.optString("suffix", ""));
                        etContactNumber.setText(jsonObject.optString("number", ""));
                        tvBirthdate.setText(jsonObject.optString("birthday", ""));
                        etAddress.setText(jsonObject.optString("address_house", ""));
                                etAddressBarangay.setText(jsonObject.optString("address_barangay", ""));
                        etMunicipality.setText(jsonObject.optString("address_municipality", ""));
                        etProvince.setText(jsonObject.optString("address_province", ""));
                        etZipCode.setText(jsonObject.optString("zip_code",""));
                        etProgram.setText(jsonObject.optString("program",""));
                        etNationality.setText(jsonObject.optString("nationality",""));

                        etLrn.setText(jsonObject.optString("lrn",""));
                        etSchoolName.setText(jsonObject.optString("school_name",""));
                        etSchoolAddress.setText(jsonObject.optString("school_address",""));

                        etFatherLastName.setText(jsonObject.optString("fathers_last_name",""));
                        etFatherFirstName.setText(jsonObject.optString("fathers_first_name",""));
                        etFatherMiddleName.setText(jsonObject.optString("fathers_middle_name",""));
                        etFatherOccupation.setText(jsonObject.optString("fathers_occupation",""));

                        etMotherLastName.setText(jsonObject.optString("mothers_last_name",""));
                        etMotherFirstName.setText(jsonObject.optString("mothers_first_name",""));
                        etMotherMiddleName.setText(jsonObject.optString("mothers_middle_name",""));
                        etMotherOccupation.setText(jsonObject.optString("mothers_occupation",""));
                        
                        etGuardianLastName.setText(jsonObject.optString("guardians_last_name",""));
                        etGuardianFirstName.setText(jsonObject.optString("guardians_first_name",""));
                        etGuardianMiddleName.setText(jsonObject.optString("guardians_middle_name",""));
                        etGuardianContactNumber.setText(jsonObject.optString("guardians_contact_number",""));


                        jsonObject.optString("address_municipality", "");
                                jsonObject.optString("address_province", "");

                        // Handle gender RadioButton
                        String gender = jsonObject.optString("gender", "Other");
                        if (gender.equalsIgnoreCase("Male")) rbMale.setChecked(true);
                        else if (gender.equalsIgnoreCase("Female")) rbFemale.setChecked(true);
                        else rbOther.setChecked(true);

                        // Handle SPED RadioGroup
                        String sped = jsonObject.optString("sped", "No");
                        if (sped.equalsIgnoreCase("Yes")) rgSped.check(R.id.rb_sped_yes);
                        else rgSped.check(R.id.rb_sped_no);

                        // Handle PWD RadioGroup
                        String pwd = jsonObject.optString("pwd", "No");
                        if (pwd.equalsIgnoreCase("Yes")) rgPwd.check(R.id.rb_pwd_yes);
                        else rgPwd.check(R.id.rb_pwd_no);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing JSON: " + response, Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("student_id", user_id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void updateStudentInfo() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating student information...");
        progressDialog.show();

        // Create a JSON object to hold the data
        JSONObject params = new JSONObject();
        try {
            params.put("student_id", user_id);  // Assuming user_id is already defined
            params.put("last_name", etLastName.getText().toString());
            params.put("first_name", etFirstName.getText().toString());
            params.put("middle_name", etMiddleName.getText().toString());
            params.put("suffix", etSuffix.getText().toString());
            params.put("number", etContactNumber.getText().toString());


            params.put("birthday", tvBirthdate.getText().toString());

            params.put("address_house", etAddress.getText().toString());
            params.put("address_barangay", etAddressBarangay.getText().toString());
            params.put("address_municipality", etMunicipality.getText().toString());
            params.put("address_province", etProvince.getText().toString());


            params.put("zip_code", etZipCode.getText().toString());

            params.put("program", etProgram.getText().toString());
            params.put("nationality",etNationality.getText().toString());
            params.put("lrn",etLrn.getText().toString());
            params.put("school_name",etSchoolName.getText().toString());
            params.put("school_address",etSchoolAddress.getText().toString());


            params.put("fathers_last_name", etFatherLastName.getText().toString());
            params.put("fathers_first_name", etFatherFirstName.getText().toString());
            params.put("fathers_middle_name", etFatherMiddleName.getText().toString());
            params.put("fathers_occupation", etFatherOccupation.getText().toString());

            params.put("mothers_last_name", etMotherLastName.getText().toString());
            params.put("mothers_first_name", etMotherFirstName.getText().toString());
            params.put("mothers_middle_name", etMotherMiddleName.getText().toString());
            params.put("mothers_occupation", etMotherOccupation.getText().toString());



            params.put("mothers_occupation", etMotherOccupation.getText().toString());

            params.put("guardians_last_name", etGuardianLastName.getText().toString());
            params.put("guardians_first_name", etGuardianFirstName.getText().toString());
            params.put("guardians_middle_name", etGuardianMiddleName.getText().toString());


            params.put("guardians_contact_number", etGuardianContactNumber.getText().toString().trim().isEmpty() ? "" : etGuardianContactNumber.getText().toString().trim());

            // Get gender
            int selectedGenderId = rgGender.getCheckedRadioButtonId();
            RadioButton selectedGender = findViewById(selectedGenderId);
            params.put("gender", selectedGender.getText().toString());

            // Get SPED
            int selectedSpedId = rgSped.getCheckedRadioButtonId();
            RadioButton selectedSped = findViewById(selectedSpedId);
            params.put("sped", selectedSped.getText().toString());

            // Get PWD
            int selectedPwdId = rgPwd.getCheckedRadioButtonId();
            RadioButton selectedPwd = findViewById(selectedPwdId);
            params.put("pwd", selectedPwd.getText().toString());

            Log.d("JSON Payload", params.toString());

        } catch (JSONException e) {
            e.printStackTrace();
            progressDialog.dismiss();
            Toast.makeText(this, "Error creating JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use JsonObjectRequest to send JSON data
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL_UPDATE,  // Replace with your actual URL
                params,
                response -> {
                    progressDialog.dismiss();
                    try {
                        Log.d("ServerResponse", "Response: " + response.toString());
                        if (response.getBoolean("success")) {
                            Toast.makeText(this, "Information updated successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to update information", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e("VolleyError", "Error: " + error.getMessage());
                    error.printStackTrace();
                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Add the request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }





}
