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
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
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

public class ActivityFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener {

    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private ArrayList<Category> categories;
    private Handler handler;
    private Runnable runnable;
    private final int UPDATE_INTERVAL = 5000; // 5 seconds
    private String userId; // Store the user ID
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_activity, container, false);

        // Retrieve user_id from SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("MyAppName", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", null); // Get the user_id from SharedPreferences


        if (userId == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            // Redirect to login if user_id is null
            Intent intent = new Intent(getActivity(), LoginTabFragment.class);
            startActivity(intent);
            getActivity().finish();
            return view;
        }

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewCategories);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        categories = new ArrayList<>();

        // Back button logic
        ImageButton backButton = view.findViewById(R.id.back_to_first_act_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationActivity activity = (NavigationActivity) getActivity();
                if (activity != null) {
                    activity.binding.bottomNavigationView.setVisibility(View.VISIBLE); // Show the navigation view
                }
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();  // Go back to the previous fragment in the back stack
            }
        });

        // Pass 'this' as the OnCategoryClickListener
        categoryAdapter = new CategoryAdapter(getContext(), categories, this, userId);
        recyclerView.setAdapter(categoryAdapter);

        // Initialize Handler for periodic updates
        handler = new Handler();
        startAutoUpdate();

        // Fetch categories when the fragment is created
        fetchCategories();

        return view;
    }

    // Fetch categories from API
    private void fetchCategories() {
        String url = "http://192.168.8.7/gesture/getCategories.php";  // Your API endpoint
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        categories.clear();
                        try {
                            Log.d("ActivityFragment", "User ID from SharedPreferences: " + userId);

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject categoryObject = response.getJSONObject(i);
                                String id = categoryObject.getString("id");
                                String name = categoryObject.getString("category_name");
                                String imageUrl = categoryObject.getString("category_image");

                                categories.add(new Category(id, name, imageUrl));
                            }
                            // Notify adapter about data change
                            categoryAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    // Handle category click event
    @Override
    public void onCategoryClick(Category category) {
        // Create an intent to start Activity
        Intent intent = new Intent(getActivity(), Activity.class);

        // Pass the category name, id, and user_id as extras
        intent.putExtra("category_name", category.getName());
        intent.putExtra("id", category.getId());
        intent.putExtra("user_id", userId); // Pass user_id to the activity

        // Start the Activity
        startActivity(intent);
        Log.d("ActivityFragment", "Starting Activity with category: " + category.getName());

        Toast.makeText(getContext(), "Clicked: " + category.getName(), Toast.LENGTH_SHORT).show();
    }

    // Start auto-update by calling fetchCategories() every X seconds
    private void startAutoUpdate() {
        runnable = new Runnable() {
            @Override
            public void run() {
                fetchCategories();
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        };
        handler.postDelayed(runnable, UPDATE_INTERVAL);
    }

    // Stop auto-update when fragment is destroyed
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopAutoUpdate();
    }

    private void stopAutoUpdate() {
        handler.removeCallbacks(runnable);
    }
}
