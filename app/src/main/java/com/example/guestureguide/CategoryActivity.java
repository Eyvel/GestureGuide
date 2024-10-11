package com.example.guestureguide;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class CategoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Get category ID passed from ContentActivity
        String categoryId = getIntent().getStringExtra("id");

        if (savedInstanceState == null) {
            // Create a new instance of CategoryFragment
            CategoryFragment categoryFragment = new CategoryFragment();

            // Pass the category ID to the fragment
            Bundle args = new Bundle();
            args.putString("id", categoryId);
            categoryFragment.setArguments(args);

            // Load the CategoryFragment into the activity
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, categoryFragment);
            transaction.commit();
        }
    }
}
