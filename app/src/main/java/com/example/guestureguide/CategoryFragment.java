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
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoryFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener {
    private TextView addTeacherText;
    private Button addTeacherBtn;
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private ArrayList<Category> categories;
    private Handler handler;
    private Runnable runnable;
    private final int UPDATE_INTERVAL = 5000; // 5 seconds
    private String username, userId, userType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);


        recyclerView = view.findViewById(R.id.recyclerViewCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        addTeacherText = view.findViewById(R.id.addTeacherText);
        addTeacherBtn = view.findViewById(R.id.addTeacherButton);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2); // Use requireContext() if you want to avoid null context
        recyclerView.setLayoutManager(gridLayoutManager);
        categories = new ArrayList<>();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppName", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

        userId = sharedPreferences.getString("user_id", "").trim();
        userType = sharedPreferences.getString("user_type", "").trim();
        fetchCategories();
        // Check if the user exists


        Log.d("HomeFragment", "Retrieved username: " + username);
//back for framgnent
        ImageButton backButton = view.findViewById(R.id.back_to_home_button);
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


        if ("user".equals(userType)) {
            addTeacherText.setVisibility(View.VISIBLE);
            addTeacherBtn.setVisibility(View.VISIBLE);
            addTeacherBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkUserExists(userId);


                }
            });
            recyclerView.setVisibility(View.GONE);
        } else {
            addTeacherText.setVisibility(View.GONE);
            addTeacherBtn.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            // Initialize and set the adapter
            categoryAdapter = new CategoryAdapter(getContext(), categories, this, username);
            recyclerView.setAdapter(categoryAdapter);

            // Initialize Handler for periodic updates
            handler = new Handler();
            startAutoUpdate();
        }

        return view;
    }

    // Fetch categories from API
    private void fetchCategories() {


        String url = "https://gestureguide.com/auth/mobile/category.api.php?user_type=" + userType + "&student_id=" + userId;  // Your API endpoint

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
        // Create an intent to start ContentActivity
        Intent intent = new Intent(getActivity(), ContentActivity.class);

        // Pass the category name as an extra
        intent.putExtra("category_name", category.getName());

        intent.putExtra("id", category.getId());

        // Start the ContentActivity
        startActivity(intent);
        Log.d("ContentActivity", "Starting ContentActivity with category: " + category.getName());


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
    public void onDestroy() {
        super.onDestroy();
        stopAutoUpdate();
    }

    private void stopAutoUpdate() {
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }


    // Check if the user exists (you can replace this with an actual network request if needed)
    private void checkUserExists(final String userId) {
        String url = "https://gestureguide.com/auth/mobile/checkUserId.php?user_id=" + userId;  // Replace with actual API endpoint

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        // Use JsonObjectRequest since the response is a JSONObject
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Log the entire response from the server
                        Log.d("ServerResponse", "Response: " + response.toString());

                        try {
                            // Check if the response contains an "error" field
                            if (response.has("status") && "error".equals(response.getString("status"))) {
                                String errorMessage = response.getString("message");
                                Log.d("Error", errorMessage);

                                // Proceed to SignupForm activity if error is present
                                Intent intent = new Intent(getActivity(), SignupForm.class);
                                startActivity(intent);
                            } else {
                                // If no error, the user exists, so proceed to TeacherIDInputActivity
                                Intent intent = new Intent(getActivity(), TeacherIDInputActivity.class);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Handle error, proceed to signup if error occurs
                            Intent intent = new Intent(getActivity(), SignupForm.class);
                            startActivity(intent);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("UserExistsError", "Error: " + error.getMessage());
                        error.printStackTrace();
                        Toast.makeText(getActivity(), "Error checking user existence.", Toast.LENGTH_SHORT).show();

                        // Proceed to SignupForm in case of request error
                        Intent intent = new Intent(getActivity(), SignupForm.class);
                        startActivity(intent);
                    }
                }
        );

        // Add the request to the queue
        requestQueue.add(jsonObjectRequest);
    }






}
