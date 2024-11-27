package com.example.guestureguide;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TeacherInformation extends AppCompatActivity {

    private String studentId;
    private SharedPreferences sharedPreferences;

    private TextView teacherFullName, teacherContact, teacherSchoolName, teacherSchoolAddress, teacherId;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_information);  // Set your layout file



        // Initialize UI components
        teacherId = findViewById(R.id.teachers_id);
        teacherFullName = findViewById(R.id.teachers_name);
        teacherContact = findViewById(R.id.teachers_contact);
        teacherSchoolName = findViewById(R.id.teacherSchoolName);
        teacherSchoolAddress = findViewById(R.id.teacherSchoolAddress);
        backButton = findViewById(R.id.back_to_profile_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set up SharedPreferences for student ID
        sharedPreferences = getSharedPreferences("MyAppName", Context.MODE_PRIVATE);
        studentId = sharedPreferences.getString("user_id", "").trim();

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);


        // Set up the Toolbar as ActionBar
        setSupportActionBar(toolbar);

        // Set up ActionBarDrawerToggle to link the toolbar with the drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Call the method to fetch teacher info
        fetchTeacherInfo(studentId);
    }

    private void fetchTeacherInfo(String studentId) {
        // URL to fetch teacher information
        String url = "https://gestureguide.com/auth/mobile/getTeacherInfo.php?student_id=" + studentId;

        // Create a new request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Create a string request to get the response from the server
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // Parse the JSON response
                    JSONObject jsonResponse = new JSONObject(response);
                    String status = jsonResponse.getString("status");

                    if (status.equals("success")) {
                        // Extract the teachers array
                        JSONArray teachersArray = jsonResponse.getJSONArray("teachers");

                        // Loop through the teachers array
                        for (int i = 0; i < teachersArray.length(); i++) {
                            JSONObject teacher = teachersArray.getJSONObject(i);

                            int idTeacher = teacher.getInt("teacher_id");
                            String fullName = teacher.getString("full_name");
                            String number = teacher.getString("number");
                            String schoolName = teacher.getString("school_name");
                            String schoolAddress = teacher.getString("school_address");

                            teacherId.setText(String.valueOf(idTeacher));
                            teacherFullName.setText(fullName);
                            teacherContact.setText(number);
                            teacherSchoolName.setText(schoolName);
                            teacherSchoolAddress.setText(schoolAddress);
                        }

                    } else {
                        // Show error message if the status is not success
                        Toast.makeText(TeacherInformation.this, "Error: " + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(TeacherInformation.this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle network errors
                Toast.makeText(TeacherInformation.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the queue
        requestQueue.add(stringRequest);
    }
}
