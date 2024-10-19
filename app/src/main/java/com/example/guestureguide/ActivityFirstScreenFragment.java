package com.example.guestureguide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class ActivityFirstScreenFragment extends Fragment {
    private Button continueButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_first_screen, container, false);

        // Initialize the continue button
        continueButton = view.findViewById(R.id.continueButton);
        ImageButton backButton = view.findViewById(R.id.back_to_study_screen_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationActivity activity = (NavigationActivity) getActivity();
                if (activity != null) {
                    activity.binding.bottomNavigationView.setVisibility(View.VISIBLE); // Show the navigation view
                }
                // Use FragmentManager to navigate back to the previous fragment
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();  // Go back to the previous fragment in the back stack
            }
        });

        // Set an onClickListener for the button
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get a reference to the NavigationActivity
                NavigationActivity activity = (NavigationActivity) getActivity();

                if (activity != null) {
                    // Use the replaceFragment method to switch to the ActivityFragment and hide the bottom navigation
                    activity.replaceFragment(new ActivityFragment(), "ActivityFragment");
                }
            }
        });

        return view;
    }
}
