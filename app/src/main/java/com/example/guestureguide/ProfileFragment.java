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

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

public class ProfileFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView textViewName, textViewEmail;
    SharedPreferences sharedPreferences;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        drawerLayout = view.findViewById(R.id.drawer_layout);
        navigationView = view.findViewById(R.id.nav_view);
        toolbar = view.findViewById(R.id.toolbar);
        textViewName = view.findViewById(R.id.profile_name);
        textViewEmail = view.findViewById(R.id.profile_email);
        sharedPreferences = requireContext().getSharedPreferences("MyAppName", Context.MODE_PRIVATE);//requireContext() to use sharedPreference

        if(sharedPreferences.getString("logged", "false").equals("false")){//if not logged in
            Intent intent = new Intent(getActivity(), LoginTabFragment.class);//will go to log in fragment
            startActivity(intent);
            getActivity().finish();//to not log in again, get save user that logged in
        };
        textViewName.setText(sharedPreferences.getString("username", ""));
        textViewEmail.setText(sharedPreferences.getString("email", ""));

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
        return true;
    }
}
