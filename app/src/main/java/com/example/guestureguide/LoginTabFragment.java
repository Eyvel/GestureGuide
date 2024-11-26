package com.example.guestureguide;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginTabFragment extends Fragment {

    TextInputEditText txt_email, txt_password;
    MaterialTextView tv_error;
    MaterialButton btn_login;
    SharedPreferences sharedPreferences;

    String url_login = "https://gestureguide.com/auth/mobile/studentLogin.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_tab, container, false);

        txt_email = view.findViewById(R.id.login_email);
        txt_password = view.findViewById(R.id.login_password);
        btn_login = view.findViewById(R.id.login_btn);
        tv_error = view.findViewById(R.id.tv_error);
        sharedPreferences = requireContext().getSharedPreferences("MyAppName", Context.MODE_PRIVATE);

        // Check if the user is already logged in
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            navigateToHome();
        }

        btn_login.setOnClickListener(v -> login());

        // Set OnClickListener for the "Forgot Password?" TextView
        TextView tv_forgot_password = view.findViewById(R.id.tv_forgot_password);
        tv_forgot_password.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ForgotPassword.class);
            startActivity(intent);
        });

        return view;
    }

    private void navigateToHome() {
        loadUserProgress();
        Intent intent = new Intent(getActivity(), NavigationActivity.class);
        startActivity(intent);
        getActivity().finish();  // Close current activity or fragment
    }

    private void login() {
        String email = txt_email.getText().toString().trim();
        String password = txt_password.getText().toString().trim();

        if (email.isEmpty()) {
            tv_error.setText("Enter Email");
        } else if (password.isEmpty()) {
            tv_error.setText("Enter Password");
        } else {
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Logging in...");
            progressDialog.show();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url_login,
                    response -> {
                        Log.d("LoginResponse", response);  // Log the full response for debugging
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            // Log the success to check if the login response was successful
                            Log.d("Login", "Success: " + success);

                            if ("0".equals(success)) {
                                String message = jsonObject.getString("message");
                                if ("Email not verified. Please verify your email first.".equals(message)) {
                                    // Navigate to the Email Verification Activity
                                    Intent intent = new Intent(getActivity(), EmailVerificationActivity.class);
                                    intent.putExtra("email", txt_email.getText().toString().trim()); // Pass the email to the next activity
                                    startActivity(intent);
                                    getActivity().finish(); // Close the current activity
                                } else {
                                    tv_error.setText(message); // Display other error messages if any
                                }
                            }else if ("1".equals(success)) {
                                JSONArray jsonArray = jsonObject.optJSONArray("login");
                                JSONArray jsonPendingArray = jsonObject.optJSONArray("login_pending");

                                // Log the length of both arrays
                                Log.d("Login", "Login array length: " + (jsonArray != null ? jsonArray.length() : 0));
                                Log.d("Login", "Login pending array length: " + (jsonPendingArray != null ? jsonPendingArray.length() : 0));

                                // Process normal login users (not pending)
                                if (jsonArray != null && jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);

                                        String username = object.getString("user_name");
                                        String user_id = object.getString("id");
                                        String userType = object.getString("user_type");
                                        String userEmail = object.getString("email");
                                        String status = object.optString("status", "user");

                                        // Log user details (user type and status)
                                        Log.d("Login", "User type: " + userType + ", Status: " + status);

                                        // Save user data to SharedPreferences
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("username", username);
                                        editor.putString("user_id", user_id);
                                        editor.putString("user_type", userType);
                                        editor.putBoolean("isLoggedIn", true);
                                        editor.putString("email", userEmail);
                                        editor.apply();

                                        // Handle different statuses
                                        if ("approved".equals(status) || "null".equals(status)) {
                                            Log.d("Login", "Navigating to Home due to 'approved' or 'new' status.");
                                            //fetchUserProgress(user_id);
                                            navigateToHome();

                                        } else {
                                            Log.d("Login", "Unknown status encountered.");
                                            tv_error.setText("Unknown status. Please contact support.");
                                        }
                                    }
                                }

                                // Process login_pending users (those with 'pending' status)
                                if (jsonPendingArray != null && jsonPendingArray.length() > 0) {
                                    for (int i = 0; i < jsonPendingArray.length(); i++) {
                                        JSONObject object = jsonPendingArray.getJSONObject(i);

                                        String username = object.getString("user_name");
                                        String user_id = object.getString("id");
                                        String userType = object.getString("user_type");
                                        String userEmail = object.getString("email");
                                        String status = object.optString("status", "pending");

                                        // Log user details (user type and status)
                                        Log.d("Login", "User type (pending): " + userType + ", Status: " + status);

                                        // Save user data to SharedPreferences
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("username", username);
                                        editor.putString("user_id", user_id);
                                        editor.putString("user_type", userType);
                                        editor.putBoolean("isLoggedIn", true);
                                        editor.putString("email", userEmail);
                                        editor.putString("status", status);
                                        editor.apply();

                                        // Navigate to waiting screen if status is 'pending'
                                        if ("pending".equals(status)) {
                                            Log.d("Login", "User is pending, redirecting to WaitingScreen.");
                                            Intent intent = new Intent(getActivity(), WaitingScreen.class);
                                            startActivity(intent);
                                              // Close the current activity
                                        }
                                    }
                                }

                            } else {
                                String message = jsonObject.getString("message");
                                tv_error.setText(message);
                            }
                        } catch (Exception e) {
                            tv_error.setText("Error: " + e.getMessage());
                        }

                        progressDialog.dismiss();
                    },
                    error -> {
                        Toast.makeText(getActivity(), "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", email);
                    params.put("password", password);
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(stringRequest);
        }

    }
//    private void fetchUserProgress(String userId) {
//        Log.d("FetchUserProgress", "User ID: " + userId);
//
//        String url_progress = "https://gestureguide.com/auth/mobile/get_user_progress.php?user_id=" + userId;
//
//        StringRequest progressRequest = new StringRequest(Request.Method.GET, url_progress,
//                response -> {
//                    Log.d("ProgressResponseLogin", "login"+response);  // Log response for debugging
//                    try {
//                        JSONObject jsonObject = new JSONObject(response);
//                        boolean success = jsonObject.getBoolean("success");
//
//                        if (success) {
//                            JSONArray progressArray = jsonObject.optJSONArray("data");
//
//                            if (progressArray != null) {
//                                // Save progress in SharedPreferences
//                                SharedPreferences.Editor editor = sharedPreferences.edit();
//                                editor.putString("user_progress", progressArray.toString()); // Save the whole array as JSON
//                                editor.apply();
//
//                                // Optional: Process progress for immediate UI updates
//                                for (int i = 0; i < progressArray.length(); i++) {
//                                    JSONObject progressItem = progressArray.getJSONObject(i);
//                                    String contentId = progressItem.getString("content_id");
//                                    int status = progressItem.getInt("status");
//
//                                    Log.d("Progress", "Content ID: " + contentId + ", Status: " + status);
//                                }
//                            }
//                        } else {
//                            String message = jsonObject.getString("message");
//                            Log.e("Progress", "Error: " + message);
//                        }
//                    } catch (Exception e) {
//                        Log.e("Progress", "Error parsing progress: " + e.getMessage());
//                    }
//                },
//                error -> {
//                    Log.e("Progress", "Error fetching progress: " + error.toString());
//                });
//
//        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
//        requestQueue.add(progressRequest);
//    }


    private void loadUserProgress() {
        String savedProgress = sharedPreferences.getString("user_progress", null);
        if (savedProgress != null) {
            try {
                JSONArray progressArray = new JSONArray(savedProgress);

                // Iterate and process saved progress
                for (int i = 0; i < progressArray.length(); i++) {
                    JSONObject progressItem = progressArray.getJSONObject(i);
                    String contentId = progressItem.getString("content_id");
                    int status = progressItem.getInt("status");

                    Log.d("LoadedProgress", "Content ID: " + contentId + ", Status: " + status);
                    // Update UI or local data based on progress
                }
            } catch (Exception e) {
                Log.e("Progress", "Error loading progress: " + e.getMessage());
            }
        } else {
            Log.d("Progress", "No saved progress found.");
        }
    }







}
