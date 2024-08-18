package com.example.guestureguide;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignupTabFragment extends Fragment {

    EditText txt_username, txt_email, txt_password, txt_confirm;
    Button btn_register;


    String url_signup = "http://192.168.8.4/capstone_test/signup.php"; // corrected the URL


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
                GoToForm();
            }
        });

        return view;
    }

    private void GoToForm() {
        String username = txt_username.getText().toString().trim();
        String email = txt_email.getText().toString().trim();
        String password = txt_password.getText().toString().trim();
        String confirm = txt_confirm.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(getActivity(), "Insert Username", Toast.LENGTH_LONG).show();
        } else if (email.isEmpty()) {
            Toast.makeText(getActivity(), "Insert Email", Toast.LENGTH_LONG).show();
        } else if (password.isEmpty()) {
            Toast.makeText(getActivity(), "Insert Password", Toast.LENGTH_LONG).show();
        } else if (confirm.isEmpty()) {
            Toast.makeText(getActivity(), "Confirm Password", Toast.LENGTH_LONG).show();
        } else if (!password.equals(confirm)) {
            Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_LONG).show();
        } else {
            // Pass data to next activity
            Intent intent = new Intent(getActivity(), SignupForm.class);
            intent.putExtra("username", username);
            intent.putExtra("email", email);
            intent.putExtra("password", password);
            startActivity(intent);
        }
    }
}
