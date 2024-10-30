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

        initializeViews(view);
        setupRecyclerView();
        setupBackButton(view);
        fetchAllQuizzes(); // Fetch all quizzes when the view is created
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

    private void fetchAllQuizzes() {
        String url = "http://192.168.8.20/gesture/getAllQuizzes.php"; // Update URL to your endpoint
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
                String quizTitle = quizObject.getString("quiz_title");
                quizzes.add(new Quiz(id, quizTitle));


            }

            // Log the total number of quiz titles fetched

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
        Intent intent = new Intent(getActivity(), Activity.class);
        intent.putExtra("quiz_title", quiz.getQuizTitle());
        intent.putExtra("quiz_id", quiz.getId());
        startActivity(intent);
    }

    private void startAutoUpdate() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                fetchAllQuizzes(); // Fetch all quizzes periodically
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
