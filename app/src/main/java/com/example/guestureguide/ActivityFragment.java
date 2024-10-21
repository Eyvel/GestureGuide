package com.example.guestureguide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

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

public class ActivityFragment extends Fragment implements QuizAdapter.OnQuizClickListener {

    private RecyclerView recyclerView;
    private QuizAdapter quizAdapter;
    private ArrayList<Quiz> quizzes;
    private Handler handler;
    private Runnable runnable;
    private final int UPDATE_INTERVAL = 5000; // 5 seconds



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);


        recyclerView = view.findViewById(R.id.recyclerViewCategories);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));

        ImageButton backButton = view.findViewById(R.id.back_to_first_quiz_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationActivity activity = (NavigationActivity) getActivity();
                if (activity != null) {
                    activity.binding.bottomNavigationView.setVisibility(View.VISIBLE);
                }
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });

<<<<<<< Updated upstream


=======
>>>>>>> Stashed changes
        quizzes = new ArrayList<>();

        // Initialize quiz adapter
        quizAdapter = new QuizAdapter(getContext(), quizzes, this);
        recyclerView.setAdapter(quizAdapter);

        handler = new Handler();
        startAutoUpdate();

        // Fetch categories when the fragment is created
        fetchCategories();

        return view;
    }

<<<<<<< Updated upstream
    // Fetch quiz titles from API
    private void fetchQuizzes() {
        String url = "http://192.168.100.72/gesture/getQuizTitles.php"; // Adjust API endpoint
=======
    // Fetch quiz titles from API by quiz_id
    private void fetchQuizzesById(int quizId) {
        String url = "http://192.168.100.72/gesture/getQuizTitles.php?quiz_id=" + quizId; // Adjust API endpoint
>>>>>>> Stashed changes
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        quizzes.clear();
                        try {
                            Log.d("ActivityFragment", "User ID from SharedPreferences: " + userId);

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject quizObject = response.getJSONObject(i);
                                String id = quizObject.getString("quiz_id");
                                String quizTitle = quizObject.getString("quiz_title");

                                quizzes.add(new Quiz(id, quizTitle));
                            }
                            quizAdapter.notifyDataSetChanged();
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

    @Override

    public void onQuizClick(Quiz quiz) {
        Intent intent = new Intent(getActivity(), Activity.class);
        intent.putExtra("quiz_title", quiz.getQuizTitle());
        intent.putExtra("quiz_id", quiz.getId()); // Passing the quiz_id
        startActivity(intent);

    }

    private void startAutoUpdate() {
        runnable = new Runnable() {
            @Override
            public void run() {
                int quizId = 1; // Example: fetch quiz with quiz_id = 1
                fetchQuizzesById(quizId);
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
