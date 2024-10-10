package com.example.guestureguide;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * A fragment for displaying records options and navigating to respective activities.
 */
public class RecordsFragment extends Fragment {

    private ImageButton lessonRecordButton;
    private ImageButton quizRecordButton;

    public RecordsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_records, container, false);

        // Initialize buttons
        lessonRecordButton = view.findViewById(R.id.lesson_record);
        quizRecordButton = view.findViewById(R.id.quiz_record);

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

        // Set click listeners for each button
        lessonRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to LessonRecordActivity
                Intent intent = new Intent(getActivity(), LessonRecordActivity.class);
                startActivity(intent);
            }
        });

        quizRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to QuizRecordActivity
                Intent intent = new Intent(getActivity(), QuizRecordActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
