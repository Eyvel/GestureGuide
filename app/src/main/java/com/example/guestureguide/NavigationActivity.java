package com.example.guestureguide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.guestureguide.databinding.ActivityNavigationBinding;
import androidx.core.view.GravityCompat;

public class NavigationActivity extends AppCompatActivity {

    ActivityNavigationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initial fragment
        replaceFragment(new HomeFragment(), "HomeFragment");

        // Set up bottom navigation
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home_nav) {
                replaceFragment(new HomeFragment(), "HomeFragment");
            } else if (itemId == R.id.quiz_nav) {
                replaceFragment(new QuizFragment(), "QuizFragment");
            } else if (itemId == R.id.profile_nav) {
                replaceFragment(new ProfileFragment(), "ProfileFragment");
            }
            return true;
        });
    }

    public void replaceFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Access the DrawerLayout directly
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        // Check if the drawer is open and close it
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return; // Exit onBackPressed() so it doesn't process further
        }

        int backStackEntryCount = fragmentManager.getBackStackEntryCount();

        if (backStackEntryCount > 1) {
            // Pop the back stack
            fragmentManager.popBackStack();
            Fragment fragment = fragmentManager.findFragmentById(R.id.frame_layout);
            if (fragment != null) {
                updateBottomNavigationView(fragment);
            }
        } else {
            super.onBackPressed(); // Handle back press normally if back stack is empty
        }
    }

    private void updateBottomNavigationView(Fragment fragment) {
        if (fragment instanceof HomeFragment) {
            binding.bottomNavigationView.setSelectedItemId(R.id.home_nav);
        } else if (fragment instanceof QuizFragment) {
            binding.bottomNavigationView.setSelectedItemId(R.id.quiz_nav);
        } else if (fragment instanceof ProfileFragment) {
            binding.bottomNavigationView.setSelectedItemId(R.id.profile_nav);
        }
    }
}
