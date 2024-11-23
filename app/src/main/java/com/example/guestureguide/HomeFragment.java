package com.example.guestureguide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements HomeCategoryAdapter.OnCategoryClickListener {

    private RecyclerView recyclerView;
    private HomeCategoryAdapter homeCategoryAdapter;
    private ArrayList<Category> categories;
    private String username;
    private TextView greeting;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewCategoriesHome);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        categories = new ArrayList<>();

        greeting = view.findViewById(R.id.greeting);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppName", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

        greeting.setText(username);

        // Initialize adapter and set it to the RecyclerView
        homeCategoryAdapter = new HomeCategoryAdapter(getContext(), categories, this, username);
        recyclerView.setAdapter(homeCategoryAdapter);

        // Fetch categories immediately
        fetchCategories();

        // Handle "See All" button click
        TextView seeAll = view.findViewById(R.id.seeAll);
        seeAll.setOnClickListener(v -> navigateToCategoryFragment());

        // Handle "Learn More" button click
        Button learnMore = view.findViewById(R.id.learn_more_button);
        learnMore.setOnClickListener(v -> navigateToCategoryFragment());

        // Handle Lesson and Quiz Records icons
        ImageView lessonRecords = view.findViewById(R.id.lesson_icon);
        lessonRecords.setOnClickListener(v -> startActivity(new Intent(getActivity(), LessonRecordActivity.class)));

        ImageView quizRecords = view.findViewById(R.id.quiz_icon);
        quizRecords.setOnClickListener(v -> startActivity(new Intent(getActivity(), QuizRecordActivity.class)));

        // Handle records button click
        LinearLayout recordsButton = view.findViewById(R.id.bottomSection);
        recordsButton.setOnClickListener(v -> navigateToRecordsFragment());

        return view;
    }

    // Fetch categories from API
    private void fetchCategories() {
        if (isAdded()) { // Ensure fragment is attached
            String url = "https://gestureguide.com/auth/mobile/getCategories.php";  // Your API endpoint
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    url,
                    null,
                    response -> {
                        categories.clear();
                        try {
                            // Assume the response has at least 2 categories
                            for (int i = 0; i < 2; i++) {
                                JSONObject categoryObject = response.getJSONObject(i);
                                String id = categoryObject.getString("id");
                                String name = categoryObject.getString("category_name");
                                String imageUrl = categoryObject.getString("category_image");

                                categories.add(new Category(id, name, imageUrl));
                            }
                            // Notify adapter that data has changed
                            homeCategoryAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> error.printStackTrace()
            );

            requestQueue.add(jsonArrayRequest);
        }
    }

    // Navigate to CategoryFragment
    private void navigateToCategoryFragment() {
        NavigationActivity activity = (NavigationActivity) getActivity();
        if (activity != null) {
            activity.binding.bottomNavigationView.setVisibility(View.GONE); // Hide the navigation view
        }
        if (getActivity() != null) {
            // Replace current fragment with CategoryFragment
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, new CategoryFragment()) // Assuming your frame layout ID
                    .addToBackStack(null)
                    .commit();
        }
    }

    // Navigate to RecordsFragment
    private void navigateToRecordsFragment() {
        NavigationActivity activity = (NavigationActivity) getActivity();
        if (activity != null) {
            activity.binding.bottomNavigationView.setVisibility(View.GONE); // Hide the navigation view
        }
        if (getActivity() != null) {
            // Replace current fragment with RecordsFragment
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, new RecordsFragment()) // Assuming your frame layout ID
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onCategoryClick(Category category) {
        // Create an intent to start ContentActivity
        Intent intent = new Intent(getActivity(), ContentActivity.class);
        intent.putExtra("category_name", category.getName());
        intent.putExtra("id", category.getId());

        // Start the ContentActivity
        startActivity(intent);
    }
}

