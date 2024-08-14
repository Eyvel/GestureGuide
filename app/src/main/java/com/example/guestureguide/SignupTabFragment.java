package com.example.guestureguide;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
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

public class SignupTabFragment extends Fragment {

    EditText txt_username, txt_email, txt_password, txt_confirm;
    Button btn_register;

    String url_signup = "http://192.168.100.40/capstone_test/signup.php"; // corrected the URL

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
                GoRegister();
            }
        });

        return view;
    }

    private void GoRegister() {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        String username = txt_username.getText().toString().trim();
        String email = txt_email.getText().toString().trim();
        String password = txt_password.getText().toString().trim();
        String confirm = txt_confirm.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(getActivity(), "Insert Username", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        } else if (email.isEmpty()) {
            Toast.makeText(getActivity(), "Insert Email", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        } else if (password.isEmpty()) {
            Toast.makeText(getActivity(), "Insert Password", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        } else if (confirm.isEmpty()) {
            Toast.makeText(getActivity(), "Confirm Password", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        } else if (!password.equals(confirm)) {
            Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        } else {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url_signup,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String success = jsonObject.getString("success");
                                if (success.equals("1")) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "Register Successful", Toast.LENGTH_LONG).show();

                                    // Start the main activity
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);
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
