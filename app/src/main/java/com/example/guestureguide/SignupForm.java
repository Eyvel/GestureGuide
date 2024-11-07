package com.example.guestureguide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.guestureguide.MainActivity;
import com.example.guestureguide.R;

import java.util.HashMap;


    String url_signup = "http://192.168.100.72/gesture/signup.php";


    private EditText lastName, firstName, middleName, ext, contactNumber, birthday, address, province, city, barangay, zipcode, lrn, program;
    private RadioGroup genderGroup, spedGroup, pwdGroup;
    private EditText fatherLastName, fatherFirstName, fatherOccupation, motherLastName, motherOccupation, guardianName, guardianOccupation;
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
        motherOccupation = findViewById(R.id.mother_occupation);
        guardianName = findViewById(R.id.guardian_name);
        guardianOccupation = findViewById(R.id.guardian_occupation);
        genderGroup = findViewById(R.id.gender_group);
        spedGroup = findViewById(R.id.sped_group);
        pwdGroup = findViewById(R.id.pwd_group);
        signupButton = findViewById(R.id.signup_button);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
            }
        });
    }

    private void signup() {
        // Get the input from the EditText fields
        String lastNameText = lastName.getText().toString().trim();
        String firstNameText = firstName.getText().toString().trim();
        String middleNameText = middleName.getText().toString().trim();
        String extText = ext.getText().toString().trim();
        String contactNumberText = contactNumber.getText().toString().trim();
        String birthdayText = birthday.getText().toString().trim();
        String addressText = address.getText().toString().trim();
        String provinceText = province.getText().toString().trim();
        String cityText = city.getText().toString().trim();
        String barangayText = barangay.getText().toString().trim();
        String zipcodeText = zipcode.getText().toString().trim();
        String lrnText = lrn.getText().toString().trim();
        String programText = program.getText().toString().trim();
        String fatherLastNameText = fatherLastName.getText().toString().trim();
        String fatherFirstNameText = fatherFirstName.getText().toString().trim();
        String fatherOccupationText = fatherOccupation.getText().toString().trim();
        String motherLastNameText = motherLastName.getText().toString().trim();
        String motherOccupationText = motherOccupation.getText().toString().trim();
        String guardianNameText = guardianName.getText().toString().trim();
        String guardianOccupationText = guardianOccupation.getText().toString().trim();

        // Validation (example)
        if (lastNameText.isEmpty() || firstNameText.isEmpty()) {
            Toast.makeText(this, "Please fill out all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Example of getting selected gender
        int selectedGenderId = genderGroup.getCheckedRadioButtonId();
        RadioButton selectedGender = findViewById(selectedGenderId);
        String genderText = selectedGender != null ? selectedGender.getText().toString() : "Not specified";

        // Example of getting selected SPED
        int selectedSpedId = spedGroup.getCheckedRadioButtonId();
        RadioButton selectedSped = findViewById(selectedSpedId);
        String spedText = selectedSped != null ? selectedSped.getText().toString() : "Not specified";

        // Example of getting selected PWD
        int selectedPwdId = pwdGroup.getCheckedRadioButtonId();
        RadioButton selectedPwd = findViewById(selectedPwdId);
        String pwdText = selectedPwd != null ? selectedPwd.getText().toString() : "Not specified";

        // Here you would handle your data (e.g., send it to your backend)
        HashMap<String, String> userData = new HashMap<>();
        userData.put("last_name", lastNameText);
        userData.put("first_name", firstNameText);
        userData.put("middle_name", middleNameText);
        userData.put("ext", extText);
        userData.put("contact_number", contactNumberText);
        userData.put("birthday", birthdayText);
        userData.put("address", addressText);
        userData.put("province", provinceText);
        userData.put("city", cityText);
        userData.put("barangay", barangayText);
        userData.put("zipcode", zipcodeText);
        userData.put("lrn", lrnText);
        userData.put("program", programText);
        userData.put("father_lastname", fatherLastNameText);
        userData.put("father_firstname", fatherFirstNameText);
        userData.put("father_occupation", fatherOccupationText);
        userData.put("mother_lastname", motherLastNameText);
        userData.put("mother_occupation", motherOccupationText);
        userData.put("guardian_name", guardianNameText);
        userData.put("guardian_occupation", guardianOccupationText);
        userData.put("gender", genderText);
        userData.put("sped", spedText);
        userData.put("pwd", pwdText);

        // Show a success message (for demonstration)
        Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show();
        // Optionally start another activity here
        Intent intent = new Intent(SignupForm.this, MainActivity.class);
        startActivity(intent);
    }
}
