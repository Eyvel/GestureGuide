package com.example.guestureguide;

import static android.content.Context.MODE_PRIVATE;

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
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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

    String url_login = "http://192.168.8.20/gesture/studentLogin.php";

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
            Intent intent = new Intent(getActivity(), ForgetPassword.class);
            startActivity(intent);
        });

        return view;
    }

    private void navigateToHome() {
        Intent intent = new Intent(getActivity(), NavigationActivity.class);
        startActivity(intent);
        getActivity().finish();
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
                        Log.d("LoginResponse", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("login");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String username = object.getString("user_name");
                                    String user_id = object.getString("id");
                                    String userType = object.getString("user_type");
//                                        String userEmail = object.getString("email");
//                                        String userNumber = object.getString("number");
//                                        String userBirthday =object.getString("birthday");
//                                        String userAddress = object.getString("street");
//                                        String userLRN = object.getString("lrn");
//                                        String firstName = object.getString("firstName");
//                                        String lastName = object.getString("lastName");
//                                        String middleName = object.getString("middleName");
//                                        String middleInitial = object.getString("middleInitial");
//                                        String suffix = object.getString("suffix");

                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("username", username);
                                    editor.putString("user_id", user_id);
                                    editor.putString("user_type", userType);
                                    editor.putBoolean("isLoggedIn", true);
                                    //                                        editor.putString("email", userEmail);
//                                        editor.putString("number", userNumber);
//                                        editor.putString("birthday", userBirthday);
//                                        editor.putString("address", userAddress);
//                                        editor.putString("lrn", userLRN);
//                                        editor.putString("logged", "true");
//                                        editor.putString("firstName", firstName);
//                                        editor.putString("lastName", lastName);
//                                        editor.putString("middleName", middleName);
//                                        editor.putString("middleInitial", middleInitial);
//                                        editor.putString("suffix", suffix);
                                    editor.apply();

                                    Toast.makeText(getActivity(), "Login Successful", Toast.LENGTH_LONG).show();
                                    navigateToHome();
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
}