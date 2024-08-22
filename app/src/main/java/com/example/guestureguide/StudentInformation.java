package com.example.guestureguide;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class StudentInformation extends AppCompatActivity {


    EditText txt_first_name, txt_last_name, txt_middle, txt_initial, txt_ext, txt_birthday, txt_number, txt_street, txt_lrn;
    Button btn_update;
    String username, email, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_information);


        txt_first_name = findViewById(R.id.stud_firstname);
        txt_last_name = findViewById(R.id.stud_lastname);
        txt_middle = findViewById(R.id.stud_middle);
        txt_initial = findViewById(R.id.stud_initial);
        txt_ext = findViewById(R.id.stud_ext);
        txt_birthday = findViewById(R.id.stud_birthday);
        txt_number = findViewById(R.id.stud_number);
        txt_street = findViewById(R.id.stud_address);

        btn_update = findViewById(R.id.stud_update_btn);

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }


        });
    }
    private void update() {
    final String first_name = txt_first_name.getText().toString().trim();
    final String last_name = txt_last_name.getText().toString().trim();
    final String middle = txt_middle.getText().toString().trim();
    final String initial = txt_initial.getText().toString().trim();
    final String ext = txt_ext.getText().toString().trim();
    final String birthday = txt_birthday.getText().toString().trim();
    final String numberStr = txt_number.getText().toString().trim();
    final String street = txt_street.getText().toString().trim();


        if (TextUtils.isEmpty(first_name) || TextUtils.isEmpty(last_name) || TextUtils.isEmpty(middle) ||
                TextUtils.isEmpty(initial) || TextUtils.isEmpty(ext) || TextUtils.isEmpty(birthday) ||
                TextUtils.isEmpty(numberStr) || TextUtils.isEmpty(street)) {



            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String url_update = "http://192.168.8.8/capstone_test/update.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_update,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(StudentInformation.this, "Update Successful", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(StudentInformation.this, "Update Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("first_name", first_name);
                params.put("last_name",  last_name);
                params.put("middle_name",middle);
                params.put("initial",    initial);
                params.put("ext",        ext);
                params.put("birthday",   birthday);
                params.put("number",     numberStr);
                params.put("address",    street);

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }
}
