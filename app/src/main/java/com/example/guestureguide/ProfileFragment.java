package com.example.guestureguide;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PICK_IMAGE_REQUEST = 1;
    private TextView profileNameTextView, profileLRNTextView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    LinearLayout profileLinearLayout;
    Toolbar toolbar;
    SharedPreferences sharedPreferences;
    CircleImageView profileCircleImageView;
    TextView fullNameTextView,lrnTextView,emailTextView,mobileNumTextView, birthdayTextView, addressTextView;
    TextView addTeacherText;
    Button addTeacherButton;
    private String studentId;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        drawerLayout = view.findViewById(R.id.drawer_layout);
        navigationView = view.findViewById(R.id.nav_view);
        toolbar = view.findViewById(R.id.toolbar);

        sharedPreferences = requireContext().getSharedPreferences("MyAppName", Context.MODE_PRIVATE);
        studentId = sharedPreferences.getString("user_id","");
        profileCircleImageView = view.findViewById(R.id.profile_circle_image_view);
        // Initialize the TextView here
        fullNameTextView = view.findViewById(R.id.profile_name);
        lrnTextView =view.findViewById(R.id.profile_LRN);
        emailTextView =view.findViewById(R.id.profile_email);
        mobileNumTextView = view.findViewById(R.id.profile_mobile);
        birthdayTextView = view.findViewById(R.id.profile_date);
        addressTextView = view.findViewById(R.id.profile_address);

        addTeacherText = view.findViewById(R.id.addTeacherTextProfile);
        addTeacherButton = view.findViewById(R.id.addTeacherButtonProfile);

        profileLinearLayout= view.findViewById(R.id.profileLinearLayout);

        profileCircleImageView = view.findViewById(R.id.profile_circle_image_view);
        // Initialize DrawerLayout and NavigationView
        drawerLayout = view.findViewById(R.id.drawer_layout);
        navigationView = view.findViewById(R.id.nav_view);

        if (navigationView != null) {
            Log.d("navview", "navview not nutll");
            // Access the header view inside the NavigationView
            android.view.View headerView = navigationView.getHeaderView(0);

            // Find views in the header layout
            profileNameTextView = headerView.findViewById(R.id.profile_name_header);
            profileLRNTextView = headerView.findViewById(R.id.profile_LRN_header);

            // Fetch and display profile details
            fetchProfileDetails(studentId);
        } else {
            Log.e("ProfileFragment", "NavigationView is null.");
        }



        // Set OnClickListener on the CircleImageView
        profileCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeImageDialog();
            }
        });


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
        String userType = sharedPreferences.getString("user_type","");


        if ("user".equals(userType)) {
            Log.d("usertype", userType);

        }

        return view;
    }
    private void showChangeImageDialog() {
        // Create an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Profile Picture")
                .setItems(new CharSequence[]{"Change Image","Remove Image", "Back"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            // "Change Image" button clicked
                            // Implement your image change logic here (e.g., open gallery or camera)
                            Toast.makeText(requireContext(), "Change Image clicked", Toast.LENGTH_SHORT).show();
                            openImagePicker();

                        } if (which == 1) { // "Remove Image" clicked

                            // Default image URL
                            String defaultImageUrl = "https://gestureguide.com/auth/mobile/uploads/profile_images/profile.png";

                            // Update the database with the default image
                            updateProfileImageInDatabase(defaultImageUrl);

                            // Load the default profile image using Glide
                            Glide.with(requireContext())
                                    .load(defaultImageUrl)
                                   // Optional placeholder
                                    .into(profileCircleImageView); // Replace with your ImageView ID
                        }
                        else {
                            // "Back" button clicked
                            dialog.dismiss();  // Dismiss the dialog
                        }
                    }
                });

        // Show the dialog
        builder.create().show();
    }

    private void updateProfileImageInDatabase(String imageUrl) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyAppName", Context.MODE_PRIVATE);
        String studentId = sharedPreferences.getString("user_id", "");

        String url = "https://gestureguide.com/auth/mobile/remove_profile_img.php"; // Your PHP script URL



        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        Log.d("id stud", studentId);
                        JSONObject jsonResponse = new JSONObject(response);

                        Log.d("response ng server", response);
                        if (jsonResponse.getString("status").equals("success")) {
                            Toast.makeText(requireContext(), "Profile image set to default.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "JSON error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(requireContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("student_id", studentId);
                params.put("profile_img", imageUrl);
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(stringRequest);
    }

    // Opens the image picker to select an image from the gallery
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri fileUri = data.getData();  // Get the URI of the selected image
            uploadProfilePicture(fileUri); // Call uploadProfilePicture to upload the image
        }
    }



    private void uploadProfilePicture(Uri fileUri) {
        // Convert Uri to File object
        File file = new File(getRealPathFromURI(fileUri));

        // Retrieve student ID from SharedPreferences
        SharedPreferences preferences = requireContext().getSharedPreferences("MyAppName", Context.MODE_PRIVATE);
         studentId = preferences.getString("user_id", "");
        Log.d("id ng user", studentId);

        if (file.exists()) {
            MultipartRequest request = new MultipartRequest(
                    "https://gestureguide.com/auth/mobile/upload_profile_pic.php",
                    file,
                    studentId,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                // Parse the JSON response from the server
                                JSONObject jsonResponse = new JSONObject(response);
                                String status = jsonResponse.getString("status");
                                String message = jsonResponse.getString("message");
                                String profileImgUrl = jsonResponse.getString("profile_img");

                                if (status.equals("success")) {
                                    // Show success message
                                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();

                                    // Load the new profile image using Glide
                                    Glide.with(requireContext())
                                            .load(profileImgUrl)  // Use the URL from the response
                                            .into(profileCircleImageView);  // Update the ImageView
                                } else {
                                    // Show failure message if status is not "success"
                                    Toast.makeText(requireContext(), "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(requireContext(), "Error parsing server response", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(requireContext(), "Error uploading image", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            // Add the request to the Volley queue
            RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
            requestQueue.add(request);
        } else {
            Toast.makeText(requireContext(), "File not found", Toast.LENGTH_SHORT).show();
        }
    }


    // Convert URI to File path
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = requireContext().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        } else {
            return contentUri.getPath(); // Fall back if the cursor is null
        }
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
            Intent intent = new Intent(requireContext(), TeacherInformation.class);
            startActivity(intent);
        }

       else if(id == R.id.change_password){
            Intent intent = new Intent(requireContext(), ChangePassword.class);
            startActivity(intent);
        }

        return true;
    }
    private void fetchProfileDetails(String studentId) {
        if (studentId == null || studentId.isEmpty()) {
            Log.e("ProfileFragment", "Student ID is missing.");
            return;
        }
        Log.e("ProfileFragment", "Student ID is not missing.");
        String url = "https://gestureguide.com/auth/mobile/getProfile.php?student_id=" + studentId;
        Log.d("ProfileFragment", "Fetching profile from: " + url);

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.has("fullname") && jsonObject.has("lrn")) {
                            String fullname = jsonObject.getString("fullname");
                            String lrn = jsonObject.getString("lrn");

                            // Update header TextViews
                            profileNameTextView.setText(fullname);
                            profileLRNTextView.setText(lrn);
                        } else if (jsonObject.has("error")) {
                            Log.e("ProfileError", "Error: " + jsonObject.getString("error"));
                        }
                    } catch (JSONException e) {
                        Log.e("ProfileFragment", "JSON parsing error.", e);
                    }
                },
                error -> Log.e("ProfileFragment", "Volley error.", error)
        );

        requestQueue.add(stringRequest);
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
