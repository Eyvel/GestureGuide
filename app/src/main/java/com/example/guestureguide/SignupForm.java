package com.example.guestureguide;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
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

public class SignupForm extends AppCompatActivity {

    EditText txt_first_name, txt_last_name, txt_middle, txt_initial, txt_ext, txt_birthday, txt_number, txt_street;
    Button btn_form;
    String username, email, password;
    String url_signup = "http://192.168.8.4/capstone_test/signup.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_form);

        txt_first_name = findViewById(R.id.first_name);
        txt_last_name = findViewById(R.id.last_name);
        txt_middle = findViewById(R.id.middle);
        txt_initial = findViewById(R.id.initial);
        txt_ext = findViewById(R.id.ext);
        txt_birthday = findViewById(R.id.birthday);
        txt_number = findViewById(R.id.number);
        txt_street = findViewById(R.id.street);
        btn_form = findViewById(R.id.form_btn);

        // Get data from previous activity
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");

        btn_form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoRegister();
            }
        });
    }

    private void GoRegister() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        String first_name = txt_first_name.getText().toString().trim();
        String last_name = txt_last_name.getText().toString().trim();
        String middle = txt_middle.getText().toString().trim();
        String initial = txt_initial.getText().toString().trim();
        String ext = txt_ext.getText().toString().trim();
        String birthday = txt_birthday.getText().toString().trim();

        String numberStr = txt_number.getText().toString().trim();
        String street = txt_street.getText().toString().trim();

        // Validate and parse inputs
        int number;
        try {
            number = Integer.parseInt(numberStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format.", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_signup,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if (success.equals("1")) {
                                progressDialog.dismiss();
                                Toast.makeText(SignupForm.this, "Register Successful", Toast.LENGTH_LONG).show();

                                // Start the main activity
                                Intent intent = new Intent(SignupForm.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SignupForm.this, e.toString(), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SignupForm.this, error.toString(), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);
                params.put("first_name", first_name);
                params.put("last_name", last_name);
                params.put("middle", middle);
                params.put("initial", initial);
                params.put("ext", ext);
                params.put("birthday", birthday);

                params.put("number", Integer.toString(number)); // Convert number to string
                params.put("street", street);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
