package com.example.guestureguide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupTabFragment extends Fragment {

    private EditText txt_username, txt_email, txt_password, txt_confirm;
    private Button btn_register;

    private String url_signup = "https://gestureguide.com/auth/mobile/signup.php"; // URL for your signup API
    private static final long DEBOUNCE_TIME = 2000; // 2 seconds debounce time
    private long lastClickTime = 0;
    private boolean isRequestPending = false; // Flag to track if a request is pending
    private RequestQueue requestQueue;
    private Spinner userTypeSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_tab, container, false);

        txt_username = view.findViewById(R.id.signup_name);
        txt_email = view.findViewById(R.id.signup_email);
        txt_password = view.findViewById(R.id.signup_password);
        txt_confirm = view.findViewById(R.id.signup_confirm);
        btn_register = view.findViewById(R.id.signup_btn);
        userTypeSpinner = view.findViewById(R.id.userTypeSpinner);

        // Initialize request queue
        requestQueue = Volley.newRequestQueue(getActivity());

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check debounce time and if a request is already pending
                if (System.currentTimeMillis() - lastClickTime < DEBOUNCE_TIME || isRequestPending) {
                    return; // Ignore the request if clicked too soon or a request is pending
                }

                // Disable button and update last click time
                btn_register.setEnabled(false);
                lastClickTime = System.currentTimeMillis();

                // Register the user
                registerUser();
            }
        });

        return view;
    }

    private void registerUser() {
        String username = txt_username.getText().toString().trim();
        String email = txt_email.getText().toString().trim();
        String password = txt_password.getText().toString().trim();
        String confirm = txt_confirm.getText().toString().trim();
        String user_type = userTypeSpinner.getSelectedItem().toString();

        // Basic validations
        if (username.isEmpty()) {
            showToast("Insert Username");
            enableButton();
            return;
        }
        if (email.isEmpty()) {
            showToast("Insert Email");
            enableButton();
            return;
        }
        if (password.isEmpty()) {
            showToast("Insert Password");
            enableButton();
            return;
        }
        if (confirm.isEmpty()) {
            showToast("Confirm Password");
            enableButton();
            return;
        }
        if (!password.equals(confirm)) {
            showToast("Passwords do not match");
            enableButton();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Invalid Email Address!");
            enableButton();
            return;
        }
        if (!isValidPassword(password)) {
            showToast("Password must be at least 8 characters long, contain one uppercase letter, one lowercase letter, and one symbol");
            enableButton();
            return;
        }

        // Log the signup attempt
        Log.d("SignupRequest", "Sending signup request with email: " + email + ", username: " + username + ", user type: " + user_type);

        // Cancel any previous signup requests
        requestQueue.cancelAll("SIGNUP_REQUEST");

        // Create a new signup request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_signup,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        isRequestPending = false; // Mark request as completed
                        enableButton();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d("SignupResponse", "Response: " + response);

                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");

                            if (status.equals("success")) {
                                String userId = jsonObject.optString("id", ""); // Use optString to avoid exceptions
                                if (userId.isEmpty()) {
                                    showToast("Registration successful but user ID is missing!");
                                    return;
                                }

                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppName", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("email", email);
                                editor.putString("user_id", userId);
                                editor.apply();

                                startActivity(new Intent(getActivity(), EmailVerificationActivity.class));
                                showToast("Registration successful! Please check your email for code");
                            } else {
                                showToast(message);
                            }
                        } catch (Exception e) {
                            showToast("Error parsing response: " + e.getMessage());
                        }

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isRequestPending = false; // Mark request as completed
                        enableButton();
                        showToast("Registration failed! Please try again.");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("user_name", username);
                params.put("password", password);
                params.put("user_type", user_type);

                return params;
            }
        };

        // Set custom retry policy to prevent multiple requests
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0, // Initial timeout in milliseconds
                0, // No retries
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        // Set tag for cancellation and add request to queue
        stringRequest.setTag("SIGNUP_REQUEST");
        requestQueue.add(stringRequest);
        isRequestPending = true;
    }

    private boolean isValidPassword(String password) {
        // Check if the password matches the required pattern
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$";
        return password.matches(passwordPattern);
    }

    private void showToast(String message) {
        if (isAdded()) {  // Ensure fragment is still attached
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }
    }

    private void enableButton() {
        btn_register.setEnabled(true);
    }
}
