package com.example.guestureguide;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class QuizScoreActivity extends AppCompatActivity {

    private TextView scoreTextView;
    private static final String TAG = "QuizScoreActivity";  //llog


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_score);

        scoreTextView = findViewById(R.id.scoreTextView);

        // Get the score from the Intent
        int score = getIntent().getIntExtra("score", 0);
        int totalQuestions = getIntent().getIntExtra("totalQuestions", 0);

        int user_id = getIntent().getIntExtra("userId", 0);  // Get the userId from Intent
        int categoryId = getIntent().getIntExtra("categoryId", 0);  // Get the categoryId from Intent

        // Display the score
        scoreTextView.setText("Score: " + score + "/" + totalQuestions);

        Log.d(TAG, "User ID: " + user_id);
        Log.d(TAG, "Category ID: " + categoryId);
        Log.d(TAG, "Score: " + score);
        Log.d(TAG, "Total Questions: " + totalQuestions);
        // Send the score to the server
        sendQuizScoreToServer(user_id, categoryId, score, totalQuestions);
    }

    private void sendQuizScoreToServer(int user_id, int categoryId, int score, int totalQuestions) {
        String url = "http://192.168.8.7/gesture/saveQuizScore.php";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(QuizScoreActivity.this, "Score saved successfully", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(QuizScoreActivity.this, "Failed to save score", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(user_id));
                params.put("category_id", String.valueOf(categoryId));
                params.put("score", String.valueOf(score));
                params.put("total_questions", String.valueOf(totalQuestions));
                return params;
            }
        };

        queue.add(stringRequest);
    }

}
