package com.example.guestureguide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

public class StudyFragment extends Fragment {

    private ImageButton lessonButton;
    private ImageButton quizButton;

    public StudyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_study, container, false);

        // Initialize buttons
        lessonButton = view.findViewById(R.id.lesson);
        quizButton = view.findViewById(R.id.quiz);

        /*
        ImageButton backButton = view.findViewById(R.id.back_to_home_screen_button);
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

         */

        // Set click listener for lessonButton
        lessonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationActivity activity = (NavigationActivity) getActivity();
                if (activity != null) {
                    // Hide the navigation bar
                    activity.binding.bottomNavigationView.setVisibility(View.GONE);

                    // Navigate to CategoryFragment
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame_layout, new CategoryFragment()) // Assuming your frame layout ID
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        // Set click listener for quizButton
        quizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationActivity activity = (NavigationActivity) getActivity();
                if (activity != null) {
                    // Hide the navigation bar
                    activity.binding.bottomNavigationView.setVisibility(View.GONE);

                    // Navigate to ActivityFirstScreenFragment
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame_layout, new ActivityFirstScreenFragment()) // Assuming your frame layout ID
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        return view;
    }
}
