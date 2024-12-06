package com.example.guestureguide;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;

import com.example.guestureguide.databinding.ActivityNavigationBinding;

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
            } else if (itemId == R.id.Activity_nav) {
                replaceFragment(new StudyFragment(), "StudyFragment");
            } else if (itemId == R.id.profile_nav) {
                replaceFragment(new ProfileFragment(), "ProfileFragment");
            }
            return true;
        });

        // Handle back press using OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fragmentManager = getSupportFragmentManager();
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

                // Handle drawer close if open
                if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return;
                }

                int backStackEntryCount = fragmentManager.getBackStackEntryCount();

                if (backStackEntryCount > 1) {
                    // Pop the back stack
                    fragmentManager.popBackStack();
                    fragmentManager.executePendingTransactions();

                    // Find the current fragment and update UI
                    Fragment fragment = fragmentManager.findFragmentById(R.id.frame_layout);
                    if (fragment != null) {
                        updateBottomNavigationView(fragment);
                        showOrHideBottomNav(fragment);
                    }
                } else {
                    // Allow default back press behavior
                    setEnabled(false); // Disable the callback to allow default behavior
                    NavigationActivity.super.onBackPressed();
                }
            }
        });
    }

    public void replaceFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();

        // Adjust bottom navigation visibility
        if (fragment instanceof HomeFragment || fragment instanceof StudyFragment || fragment instanceof ProfileFragment) {
            binding.bottomNavigationView.setVisibility(View.VISIBLE); // Show bottom navigation
        } else {
            binding.bottomNavigationView.setVisibility(View.GONE); // Hide bottom navigation
        }
    }

    private void updateBottomNavigationView(Fragment fragment) {
        if (fragment instanceof HomeFragment) {
            binding.bottomNavigationView.setSelectedItemId(R.id.home_nav);
        } else if (fragment instanceof StudyFragment) {
            binding.bottomNavigationView.setSelectedItemId(R.id.Activity_nav);
        } else if (fragment instanceof ProfileFragment) {
            binding.bottomNavigationView.setSelectedItemId(R.id.profile_nav);
        }
    }

    private void showOrHideBottomNav(Fragment fragment) {
        if (fragment instanceof HomeFragment || fragment instanceof StudyFragment || fragment instanceof ProfileFragment) {
            binding.bottomNavigationView.setVisibility(View.VISIBLE);  // Show bottom navigation
        } else {
            binding.bottomNavigationView.setVisibility(View.GONE);  // Hide bottom navigation
        }
    }
}
