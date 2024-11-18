package com.example.guestureguide;

import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityFragment extends Fragment implements QuizAdapter.OnQuizClickListener {

    private RecyclerView recyclerView;
    private QuizAdapter quizAdapter;
    private ArrayList<Quiz> quizzes;
    private Handler handler;
    private Runnable runnable;
    private final int UPDATE_INTERVAL = 5000; // 5 seconds

    private String userId;
    private String userType;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyAppName", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id",""); // Retrieve the user_id
        String userType = sharedPreferences.getString("user_type","");



        initializeViews(view);
        setupRecyclerView();
        setupBackButton(view);
        fetchAllQuizzes(userId, userType); // Fetch all quizzes when the view is created
        startAutoUpdate();

        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerviewQuiz);
        quizzes = new ArrayList<>();
        quizAdapter = new QuizAdapter(getContext(), quizzes, this);
        recyclerView.setAdapter(quizAdapter);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
    }

    private void setupBackButton(View view) {
        ImageButton backButton = view.findViewById(R.id.back_to_first_quiz_button);
        backButton.setOnClickListener(v -> {
            NavigationActivity activity = (NavigationActivity) getActivity();
            if (activity != null) {
                activity.binding.bottomNavigationView.setVisibility(View.VISIBLE);
            }
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        });
    }

    private void fetchAllQuizzes(String userId, String userType) {

        String url = "https://gestureguide.com/auth/mobile/getAllQuizzes.php?user_type="+ userType+"&student_id="+userId; // Update URL to your endpoint
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                this::parseQuizResponse,
                this::handleQuizError
        );

        requestQueue.add(jsonArrayRequest);
    }

    private void parseQuizResponse(JSONArray response) {
        quizzes.clear();
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject quizObject = response.getJSONObject(i);
                String id = quizObject.getString("quiz_id");
                String quizTitle = quizObject.getString("quiz_title").trim(); // Trim whitespace
                quizzes.add(new Quiz(id, quizTitle));
            }

            // Sort quizzes by the numeric part of the title
            quizzes.sort((quiz1, quiz2) -> {
                int num1 = Integer.parseInt(quiz1.getQuizTitle().replaceAll("\\D+", ""));
                int num2 = Integer.parseInt(quiz2.getQuizTitle().replaceAll("\\D+", ""));
                return Integer.compare(num1, num2);
            });

            // Log the total number of quiz titles fetched
            for (Quiz quiz : quizzes) {
                Log.d("QuizTitle", quiz.getQuizTitle()); // Log the titles
            }

            // Notify adapter about data change
            quizAdapter.notifyDataSetChanged();


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void handleQuizError(VolleyError error) {
        error.printStackTrace();
    }

    @Override
    public void onQuizClick(Quiz quiz) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyAppName", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id",""); // Retrieve the user_id
        String userType = sharedPreferences.getString("user_type","");

        if (userId != "") {
            checkUserRecordExists(userId, quiz); // Check if the user already has a record for this quiz
        } else {
            // Handle case where user_id is not available
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUserRecordExists(String userId, Quiz quiz) {
        String url = "https://gestureguide.com/auth/mobile/checkUserResponse.php?user_id=" + userId + "&quiz_id=" + quiz.getId();

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,

                response -> {
                    try {
                        Log.d("ServerResponse", "Response: " + response.toString());

                        if (response.has("record_exists")) {
                            boolean recordExists = response.getBoolean("record_exists");
                            Log.d("RecordExists", "Record exists: " + recordExists);

                            if (recordExists) {
                                // Navigate to QuizSummary if the record exists
                                Intent intent = new Intent(getActivity(), QuizSummary.class);
                                intent.putExtra("quiz_title", quiz.getQuizTitle());
                                intent.putExtra("quiz_id", quiz.getId());
                                startActivity(intent);
                            } else {
                                // Notify user that there is no data and navigate to Activity.class
                                Toast.makeText(getContext(), "No previous records found for this quiz.", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getActivity(), Activity.class); // Navigate to Activity.class
                                intent.putExtra("quiz_title", quiz.getQuizTitle());
                                intent.putExtra("quiz_id", quiz.getId());
                                startActivity(intent);

                            }
                        } else {
                            Log.e("ResponseError", "Missing 'record_exists' field in response");
                            Toast.makeText(getContext(), "Invalid server response", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("NetworkError", "Error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error"));
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Network error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(jsonObjectRequest);
    }





    private void startAutoUpdate() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                fetchAllQuizzes(userId, userType); // Fetch all quizzes periodically
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        };
        handler.postDelayed(runnable, UPDATE_INTERVAL);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable);
    }
}