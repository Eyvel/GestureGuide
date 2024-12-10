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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
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
    private RecyclerView quizRecyclerView;
    private ArrayList<Quiz> quizzes;
    private Handler handler;
    private Runnable runnable;
    private final int UPDATE_INTERVAL = 5000; // 5 seconds
    private String userId;
    private String userType;
    private TextView addTeacherText;
    private Button addTeacherButton;    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);

        quizRecyclerView = view.findViewById(R.id.recyclerviewQuiz);

        addTeacherText = view.findViewById(R.id.addTeacherTextQuiz);
        addTeacherButton = view.findViewById(R.id.addTeacherButtonQuiz);

        // Retrieve userId and userType from SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyAppName", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", "");

        userType = sharedPreferences.getString("user_type", "");

        // Initialize and set up views
        initializeViews(view);
        setupRecyclerView();
        setupBackButton(view);

        // Fetch quizzes if userId and userType are valid
        if (!userId.isEmpty() && !userType.isEmpty()) {
            fetchAllQuizzes();
            startAutoUpdate();
        } else {
            Toast.makeText(getContext(), "User ID or Type is missing", Toast.LENGTH_SHORT).show();
        }
        SharedPreferences sharedPreferencesUserType = requireContext().getSharedPreferences("MyAppName", Context.MODE_PRIVATE);

        String userType = sharedPreferencesUserType.getString("user_type","");


        if ("user".equals(userType)) {
            Log.d("usertypeNgProfile", userType);
            addTeacherText.setVisibility(View.VISIBLE);
            addTeacherButton.setVisibility(View.VISIBLE);
            addTeacherButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkUserExists(userId); // Assuming you meant to use `studentId` here
                }
            });
            quizRecyclerView.setVisibility(View.GONE);

        } else {

            addTeacherText.setVisibility(View.GONE);
            addTeacherButton.setVisibility(View.GONE);
            quizRecyclerView.setVisibility(View.VISIBLE);




        }

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
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    private void fetchAllQuizzes() {
        String url = "https://gestureguide.com/auth/mobile/getQuizTitles.php?student_id=" + userId + "&user_type=" + userType;
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

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
                String quizTitle = quizObject.getString("quiz_title").trim();
                quizzes.add(new Quiz(id, quizTitle));
            }

            // Sort quizzes by numeric value in the title
            quizzes.sort((quiz1, quiz2) -> {
                String numStr1 = quiz1.getQuizTitle().replaceAll("\\D+", "");
                String numStr2 = quiz2.getQuizTitle().replaceAll("\\D+", "");

                // Use default value 0 if no number is found
                int num1 = numStr1.isEmpty() ? 0 : Integer.parseInt(numStr1);
                int num2 = numStr2.isEmpty() ? 0 : Integer.parseInt(numStr2);

                return Integer.compare(num1, num2);
            });

            quizAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            Log.e("ParseError", "Error parsing JSON: " + e.getMessage());
        }
    }


    private void handleQuizError(VolleyError error) {
        Log.e("VolleyError", "Error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error"));
        Toast.makeText(getContext(), "Failed to fetch quizzes", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onQuizClick(Quiz quiz) {
        if (!userId.isEmpty()) {
            checkUserRecordExists(quiz);
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUserRecordExists(Quiz quiz) {
        String url = "https://gestureguide.com/auth/mobile/checkUserResponse.php?user_id=" + userId + "&quiz_id=" + quiz.getId();
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        if (response.has("record_exists")) {
                            boolean recordExists = response.getBoolean("record_exists");
                            navigateToNextActivity(recordExists, quiz);
                        } else {
                            Toast.makeText(getContext(), "Invalid server response", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("ParseError", "Error parsing response: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e("NetworkError", "Error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error"));
                    Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void navigateToNextActivity(boolean recordExists, Quiz quiz) {
        Intent intent;
        if (recordExists) {
            intent = new Intent(getActivity(), QuizSummary.class);
        } else {
            intent = new Intent(getActivity(), Activity.class);
        }
        intent.putExtra("quiz_title", quiz.getQuizTitle());
        intent.putExtra("quiz_id", quiz.getId());
        startActivity(intent);
    }

    private void startAutoUpdate() {
        handler = new Handler();
        runnable = () -> {
            fetchAllQuizzes();
            handler.postDelayed(runnable, UPDATE_INTERVAL);
        };
        handler.postDelayed(runnable, UPDATE_INTERVAL);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }
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
