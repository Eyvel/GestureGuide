package com.example.guestureguide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class HomeFragment extends Fragment implements HomeCategoryAdapter.OnCategoryClickListener {

    private RecyclerView recyclerView;
    private HomeCategoryAdapter homeCategoryAdapter;
    private ArrayList<Category> categories;
    private String userId;
    private TextView greeting;
    SharedPreferences sharedPreferences;
    private TextView upcomingEvents, description;

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

        upcomingEvents = view.findViewById(R.id.upcomingEvents);
        description = view.findViewById(R.id.description);
        fetchEventTitles(upcomingEvents,description);
        fetchUsername(userId);
        greeting = view.findViewById(R.id.greeting);

        TextView currentDateTextView = view.findViewById(R.id.currentDate);

// Get the current date in Manila
        String currentDate = getCurrentDateManila();

// Set the date in the TextView
        currentDateTextView.setText(currentDate);

        sharedPreferences = getActivity().getSharedPreferences("MyAppName", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id","");



        // Initialize adapter and set it to the RecyclerView
        homeCategoryAdapter = new HomeCategoryAdapter(getContext(), categories, this);
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
        TextView lessonRecordText =view.findViewById(R.id.lesson_record_text);
        lessonRecords.setOnClickListener(v -> startActivity(new Intent(getActivity(), LessonRecordActivity.class)));
        lessonRecordText.setOnClickListener(v -> startActivity(new Intent(getActivity(), LessonRecordActivity.class)));

        ImageView quizRecords = view.findViewById(R.id.quiz_icon);
        quizRecords.setOnClickListener(v -> startActivity(new Intent(getActivity(), QuizRecordActivity.class)));
        TextView quizRecordText =view.findViewById(R.id.quiz_record_text);
        quizRecordText.setOnClickListener(v -> startActivity(new Intent(getActivity(), QuizRecordActivity.class)));


        // Handle records button click
        LinearLayout recordsButton = view.findViewById(R.id.bottomSection);
        recordsButton.setOnClickListener(v -> navigateToRecordsFragment());

        // Retrieve saved status from SharedPreferences
        String status = sharedPreferences.getString("status", ""); // Default to empty string if not found

        if ("pending".equals(status)) {
            handlePendingUser(); // Redirect to WaitingScreen and logout
        } else {
            Log.d("HomeFragment", "User status: " + status);
            // Handle other statuses if needed
        }

        return view;
    }
    private void fetchEventTitles(TextView upcomingEvents, TextView descriptionTextView) {
        // Check if the user ID is valid
        if (userId == null || userId.isEmpty()) {
            Log.d("HomeFragment", "User ID is not set. Cannot fetch events.");
            upcomingEvents.setText(""); // Clear description
            return;
        }

        // Check cache first
        String cachedEvents = sharedPreferences.getString("cached_events", null);
        if (cachedEvents != null) {
            try {
                // Use cached data
                JSONObject jsonResponse = new JSONObject(cachedEvents);
                updateEventTextView(jsonResponse, upcomingEvents, descriptionTextView);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Fetch from API
        String url = "https://gestureguide.com/auth/mobile/getEvent.php?student_id=" + userId;
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    try {
                        // Cache the response
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("cached_events", response);
                        editor.apply();

                        // Parse and update UI
                        JSONObject jsonResponse = new JSONObject(response);
                        updateEventTextView(jsonResponse, upcomingEvents, descriptionTextView);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        upcomingEvents.setText("Error parsing events.");
                        descriptionTextView.setText("");
                    }
                },
                error -> {
                    error.printStackTrace();
                    upcomingEvents.setText("Error fetching events.");
                    descriptionTextView.setText("");
                }
        );

        requestQueue.add(stringRequest);
    }


    private void updateEventTextView(JSONObject jsonResponse, TextView upcomingEvents, TextView descriptionTextView) throws JSONException {
        JSONArray jsonArray = jsonResponse.getJSONArray("events");

        if (jsonArray.length() == 0) {
            Log.d("response ng event", "No events found for student_id: " + userId);
            upcomingEvents.setText(""); // Clear description if no events
        } else {
            StringBuilder titlesText = new StringBuilder();
            StringBuilder descriptionsText = new StringBuilder();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject event = jsonArray.getJSONObject(i);
                String title = event.getString("title");
                String description = event.getString("description");

                // Append title and description separately
                titlesText.append(" ").append(title).append("\n");
                descriptionsText.append("- ").append(description).append("\n");
            }

            // Set text for both TextViews
            upcomingEvents.setText(titlesText.toString());
            descriptionTextView.setText(descriptionsText.toString());
        }
    }

    public String getCurrentDateManila() {
        // Set the time zone to Manila
        TimeZone manilaTimeZone = TimeZone.getTimeZone("Asia/Manila");

        // Create a SimpleDateFormat instance to format the date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-YYYY");  // You can change the pattern based on your needs
        dateFormat.setTimeZone(manilaTimeZone);

        // Get the current date in Manila
        Calendar calendar = Calendar.getInstance(manilaTimeZone);
        String currentDate = dateFormat.format(calendar.getTime());

        return currentDate;
    }




    private void handlePendingUser() {
        Log.d("HomeFragment", "User status is pending. Logging out and redirecting to WaitingScreen.");
        logoutUser(() -> {
            Intent intent = new Intent(getActivity(), WaitingScreen.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }
    private void fetchUsername(String userId) {
        String url = "https://gestureguide.com/auth/mobile/getUsername.php?user_id=" + userId;

        // Creating a StringRequest
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        // Parsing the JSON response
                        JSONObject jsonResponse = new JSONObject(response);

                        // Check if the request was successful
                        if (jsonResponse.getBoolean("success")) {
                            String userName = jsonResponse.getString("user_name");

                            // Update the greeting TextView with the fetched username
                            greeting.setText(userName);
                        } else {
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Handle error here
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Error fetching username", Toast.LENGTH_SHORT).show();
                }
        );

        // Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        // Adding the request to the queue
        requestQueue.add(stringRequest);
    }


    private void logoutUser(Runnable onSuccess) {
        String url_logout = "https://gestureguide.com/auth/mobile/logout.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_logout,
                response -> {
                    Log.d("LogoutResponse", response.trim());
                    if ("1".equals(response.trim())) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        onSuccess.run();
                    } else {
                        Toast.makeText(getContext(), "Logout failed: " + response, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("LogoutError", "Error during logout", error);
                    Toast.makeText(getContext(), "Logout error. Please try again.", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", sharedPreferences.getString("email", ""));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
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

    @Override
    public void onResume() {
        super.onResume();

        // Update username dynamically
        fetchUsername(userId);




        // Update events dynamically
        fetchEventTitles(upcomingEvents,description);
    }

}

