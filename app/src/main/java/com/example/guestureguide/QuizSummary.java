package com.example.guestureguide;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class QuizSummary extends AppCompatActivity {
    private TextView titleTextView;
    private TextView scoreTextView;
    private TextView correctAnswersTextView;
    private TextView incorrectAnswersTextView;
    private TextView answerPercentTextView;
    private TextView answeredDateTextView;
    private Button exitButton;// Add a TextView for answered date

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_summary);

        // Initialize UI components
        exitButton = findViewById(R.id.finishButton);
        titleTextView = findViewById(R.id.quiz_name);
        scoreTextView = findViewById(R.id.quiz_score);
        correctAnswersTextView = findViewById(R.id.correctAnswers);
        incorrectAnswersTextView = findViewById(R.id.incorrectAnswers);
        answerPercentTextView = findViewById(R.id.answerPercentage);
        answeredDateTextView = findViewById(R.id.dateTaken);  // Initialize the answered date TextView
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppName", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id",""); // Retrieve the user_id

        // Assume user_id and quiz_id are passed from intent
        //String userId = getIntent().getStringExtra("user_id");
        String quizId = getIntent().getStringExtra("quiz_id");

        // Fetch quiz summary from the server
        fetchQuizSummary(userId, quizId);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("quizsummary", "clicked");
                finish();


            }
        });


    }



    private void fetchQuizSummary(String userId, String quizId) {
        String url = "https://gestureguide.com/auth/mobile/getQuizSummary.php?user_id=" + userId + "&quiz_id=" + quizId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("success")) {
                                String quizTitle = response.getString("quiz_title");
                                int correctCount = response.getInt("correct_count");
                                int incorrectCount = response.getInt("incorrect_count");
                                int totalScore = response.getInt("total_score");
                                int totalItems = response.getInt("total_items");
                                String quizAnsweredDate = response.getString("quiz_answered_date");  // Get answered date

                                // Calculate score percentage
                                int scoreToPercent = (int) (((double) totalScore / totalItems) * 100);

                                // Update the UI with the fetched data
                                titleTextView.setText(quizTitle);
                                scoreTextView.setText("Score: " + totalScore + "/" + totalItems);
                                correctAnswersTextView.setText("Correct Answers: " + correctCount);
                                incorrectAnswersTextView.setText("Incorrect Answers: " + incorrectCount);
                                answerPercentTextView.setText("Answer Percentage: " + scoreToPercent + "%");
                                answeredDateTextView.setText("Answered Date: " + quizAnsweredDate);  // Set answered date
                            } else {
                                Toast.makeText(QuizSummary.this, "Error: " + response.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(QuizSummary.this, "Error parsing data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(QuizSummary.this, "Network Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the RequestQueue.
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
}
