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
}
}
