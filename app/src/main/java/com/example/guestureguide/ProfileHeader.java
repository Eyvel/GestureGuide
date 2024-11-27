package com.example.guestureguide;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileHeader extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView profileNameTextView, profileLRNTextView;
    private String studentId;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile); // Use your actual activity layout here

        // Initialize SharedPreferences to retrieve student ID
        sharedPreferences = getSharedPreferences("MyAppName", MODE_PRIVATE);
        studentId = sharedPreferences.getString("user_id", "");


    }


}
