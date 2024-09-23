package com.example.guestureguide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ActivityFirstScreenFragment extends Fragment {
    private Button continueButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_first_screen, container, false);

        continueButton = view.findViewById(R.id.continueButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationActivity activity = (NavigationActivity) getActivity();
                if (activity != null) {
                    activity.binding.bottomNavigationView.setVisibility(View.GONE); // Hide the navigation view
                }
                if (getActivity() != null) {
                    // Replace current fragment with ActivityFragment
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame_layout, new ActivityFragment()) // Assuming your frame layout ID
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        return view;
    }
}
