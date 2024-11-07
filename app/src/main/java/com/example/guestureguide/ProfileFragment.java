package com.example.guestureguide;

import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView textViewName, textViewEmail, textViewNumber, textViewBirthday, textViewAddress, textViewLrn;
    SharedPreferences sharedPreferences;
    Button logoutBtn;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        drawerLayout = view.findViewById(R.id.drawer_layout);
        navigationView = view.findViewById(R.id.nav_view);
        toolbar = view.findViewById(R.id.toolbar);
        textViewName = view.findViewById(R.id.profile_name);
        textViewEmail = view.findViewById(R.id.profile_email);
        textViewNumber = view.findViewById(R.id.profile_mobile);
        textViewBirthday = view.findViewById(R.id.profile_date);
        textViewAddress = view.findViewById(R.id.profile_address);
        textViewLrn =view.findViewById(R.id.profile_LRN);
        sharedPreferences = requireContext().getSharedPreferences("MyAppName", Context.MODE_PRIVATE);//requireContext() to use sharedPreference

        if(sharedPreferences.getString("logged", "false").equals("false")){//if not logged in
            Intent intent = new Intent(getActivity(), LoginTabFragment.class);//will go to log in fragment
            startActivity(intent);
            getActivity().finish();//to not log in again, get save user that logged in
        };
        textViewName.setText(sharedPreferences.getString("username", ""));
        textViewEmail.setText(sharedPreferences.getString("email", ""));
        textViewNumber.setText(sharedPreferences.getString("number", ""));
        textViewBirthday.setText(sharedPreferences.getString("birthday", ""));
        textViewAddress.setText(sharedPreferences.getString("address", ""));
        textViewLrn.setText(sharedPreferences.getString("lrn", ""));



        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);

        return view;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_logout) {

            //  cProgressDialog progressDialog = new ProgressDialog(getActivity());
            
            String url_logout = "http://192.168.100.40/gesture/logout.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url_logout,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("LoginResponse", response);
                            if(response.equals("1")) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("logged", "false");
                                editor.clear();



                                editor.apply();//to access anywhere sharedprefence
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                                Toast.makeText(getActivity(), "Logout Successful", Toast.LENGTH_LONG).show();

                                // Start the main activity

                            }else{
                                Toast.makeText(requireContext(),response,Toast.LENGTH_SHORT).show();
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
                    params.put("apiKey", sharedPreferences.getString("apiKey", ""));


                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(stringRequest);
        }
        else if(id == R.id.stud_info){
            Intent intent = new Intent(getActivity(), StudentInformation.class);
            startActivity(intent);
            getActivity().finish();

        }

        return true;
    }
}
