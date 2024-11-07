package com.example.guestureguide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

    EditText txt_username, txt_email, txt_password, txt_confirm;
    Button btn_register;

    String url_signup = "http://192.168.8.20/gesture/signup.php"; // URL for your signup API

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_tab, container, false);

        txt_username = view.findViewById(R.id.signup_name);
        txt_email = view.findViewById(R.id.signup_email);
        txt_password = view.findViewById(R.id.signup_password);
        txt_confirm = view.findViewById(R.id.signup_confirm);
        btn_register = view.findViewById(R.id.signup_btn);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        String user_type = "user"; // or any default value you need

        if (username.isEmpty()) {
            showToast("Insert Username");
        } else if (email.isEmpty()) {
            showToast("Insert Email");
        } else if (password.isEmpty()) {
            showToast("Insert Password");
        } else if (confirm.isEmpty()) {
            showToast("Confirm Password");
        } else if (!password.equals(confirm)) {
            showToast("Passwords do not match");
        } else if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Invalid Email Address!");
        } else {
            // Proceed with sending data to the server
            sendSignupRequest(username, email, password, user_type);
        }
    }

    private void showToast(String message) {
        if (isAdded()) {  // Check if the fragment is still attached
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }
    }

    private void sendSignupRequest(final String username, final String email, final String password, final String user_type) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_signup,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Parse the JSON response
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                // Registration successful, get user_id from the response
                                String user_id = jsonObject.getString("user_id");

                                // Store user_id in SharedPreferences
                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppName", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("user_id", user_id);
                                editor.apply();  // Commit changes to SharedPreferences

                                // Show a success message
                                Toast.makeText(getActivity(), "Registration successful!", Toast.LENGTH_LONG).show();

                                // Start the TeacherIDInputActivity after successful registration
                                Intent intent = new Intent(getActivity(), TeacherIDInputActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            } else {
                                // Registration failed, get the error message
                                String message = jsonObject.getString("message");
                                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Registration failed!", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Prepare parameters to send to PHP
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("user_name", username);
                params.put("password", password);
                params.put("user_type", user_type);
                return params;
            }
        };

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }
}