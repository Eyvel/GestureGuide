package com.example.guestureguide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    SharedPreferences sharedPreferences;
    CircleImageView profileCircleImageView;
    TextView fullNameTextView,lrnTextView,emailTextView,mobileNumTextView, birthdayTextView, addressTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        drawerLayout = view.findViewById(R.id.drawer_layout);
        navigationView = view.findViewById(R.id.nav_view);
        toolbar = view.findViewById(R.id.toolbar);
        sharedPreferences = requireContext().getSharedPreferences("MyAppName", Context.MODE_PRIVATE);
        profileCircleImageView = view.findViewById(R.id.profile_circle_image_view);
        // Initialize the TextView here
        fullNameTextView = view.findViewById(R.id.profile_name);
        lrnTextView =view.findViewById(R.id.profile_LRN);
        emailTextView =view.findViewById(R.id.profile_email);
        mobileNumTextView = view.findViewById(R.id.profile_mobile);
        birthdayTextView = view.findViewById(R.id.profile_date);
        addressTextView = view.findViewById(R.id.profile_address);


        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // Fetch student profile and save to SharedPreferences
        fetchStudentProfile();

        return view;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            logoutUser();
        } else if (id == R.id.stud_info) {

            Intent intent = new Intent(requireContext(), StudentInformation.class);
            startActivity(intent);
        } else if(id == R.id.teacher_info){
        }

        else if(id == R.id.acc_settings){
        }else if(id == R.id.change_password){
        }

        return true;
    }


    private void fetchStudentProfile() {
        String studentId = sharedPreferences.getString("user_id", ""); // Assume student_id is saved
        if (studentId.isEmpty()) {
            Toast.makeText(requireContext(), "No Student ID found in SharedPreferences", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://gestureguide.com/auth/mobile/fetch_student_profile.php?student_id=" + studentId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("error")) {
                                Toast.makeText(requireContext(), response.getString("error"), Toast.LENGTH_SHORT).show();
                            } else {
                                // Save response data to SharedPreferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                Log.d("StudentProfile", "Student ID: " + response.getString("student_id"));
                                Log.d("StudentProfile", "First Name: " + response.getString("first_name"));
                                Log.d("StudentProfile", "Last Name: " + response.getString("last_name"));
                                Log.d("StudentProfile", "Profile Image: " + response.getString("profile_img"));

                                editor.putString("lrn", response.optString("lrn", ""));
                                editor.putString("program", response.optString("program", ""));
                                editor.putString("first_name", response.optString("first_name", ""));
                                editor.putString("middle_name", response.optString("middle_name", ""));
                                editor.putString("last_name", response.optString("last_name", ""));
                                editor.putString("middle_initial", response.optString("middle_initial", ""));
                                editor.putString("suffix", response.optString("suffix", ""));
                                editor.putString("number", response.optString("number", ""));
                                editor.putString("gender", response.optString("gender", ""));
                                editor.putString("birthday", response.optString("birthday", ""));
                                editor.putString("age", response.optString("age", ""));
                                editor.putString("sped", response.optString("sped", ""));
                                editor.putString("pwd", response.optString("pwd", ""));
                                editor.putString("address_house", response.optString("address_house", ""));
                                editor.putString("address_barangay", response.optString("address_barangay", ""));
                                editor.putString("address_municipality", response.optString("address_municipality", ""));
                                editor.putString("address_province", response.optString("address_province", ""));
                                editor.putString("zip_code", response.optString("zip_code", ""));
                                editor.putString("nationality", response.optString("nationality", ""));
                                editor.putString("school_name", response.optString("school_name", ""));
                                editor.putString("school_address", response.optString("school_address", ""));
                                editor.putString("fathers_last_name", response.optString("fathers_last_name", ""));
                                editor.putString("fathers_first_name", response.optString("fathers_first_name", ""));
                                editor.putString("fathers_middle_name", response.optString("fathers_middle_name", ""));
                                editor.putString("fathers_occupation", response.optString("fathers_occupation", ""));
                                editor.putString("mothers_last_name", response.optString("mothers_last_name", ""));
                                editor.putString("mothers_first_name", response.optString("mothers_first_name", ""));
                                editor.putString("mothers_middle_name", response.optString("mothers_middle_name", ""));
                                editor.putString("mothers_occupation", response.optString("mothers_occupation", ""));
                                editor.putString("civil_status", response.optString("civil_status", ""));
                                editor.putString("guardians_last_name", response.optString("guardians_last_name", ""));
                                editor.putString("guardians_first_name", response.optString("guardians_first_name", ""));
                                editor.putString("guardians_middle_name", response.optString("guardians_middle_name", ""));
                                editor.putString("guardians_contact_number", response.optString("guardians_contact_number", ""));
                                editor.putString("profile_img", response.optString("profile_img", ""));


                                // Add more fields as needed
                                editor.apply();

                                String firstName = response.getString("first_name");
                                String lastName = response.getString("last_name");
                                String middleInitial = response.getString("middle_initial");
                                String suffix = response.getString("suffix");
                                String fullName = firstName + " "+middleInitial+ ". "+lastName + " "+suffix;


                                // Update TextView with full name

                                fullNameTextView.setText(fullName);

                                String lrn = response.getString("lrn");
                                lrnTextView.setText(lrn);


                                String emailAddress = sharedPreferences.getString("email", "").trim();
                                emailTextView.setText(emailAddress);

                                String mobileNumber = response.getString("number");
                                mobileNumTextView.setText(mobileNumber);

                                String birthday = response.getString("birthday");
                                birthdayTextView.setText(birthday);

                                String addressHouse = response.getString("address_house");
                                String addressBaranggay = response.getString("address_barangay");
                                String addressMunicipality = response.getString("address_municipality");
                                String addressProvince = response.getString("address_province");

                                String completeAddress = addressHouse+", "+addressBaranggay +", "+addressMunicipality+", "
                                        + addressProvince;

                                addressTextView.setText(completeAddress);

                                String profileImagePath = response.getString("profile_img");
                                String baseUrl = "https://gestureguide.com/"; // Base URL for images
                                String profileImageUrl = baseUrl + profileImagePath;
                                Glide.with(requireContext())
                                        .load(profileImageUrl) // URL of the image
                                        .into(profileCircleImageView); // ImageView to load the image into

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Failed to parse profile data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(requireContext(), "Error fetching profile: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(jsonObjectRequest);
    }

    private void logoutUser() {
        String url_logout = "https://gestureguide.com/auth/mobile/logout.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_logout,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("SharedPreferences", "Email: " + sharedPreferences.getString("email", ""));

                        Log.d("LogoutResponse", response); // Log the response for debugging
                        if (response.trim().equals("1")) {  // Use trim() to remove any whitespace
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.apply();

                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                            Toast.makeText(getActivity(), "Logout Successful", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(requireContext(), "Logout failed: " + response, Toast.LENGTH_SHORT).show();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", sharedPreferences.getString("email", ""));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }
}
