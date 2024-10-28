package com.example.guestureguide;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;  // For logging
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


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
    private Button exitButton;
    private int quizId;
    private int quizScore;
    private int totalScore;
    private int totalQuestions;
    private String user_id;
    private int categoryId;
    private static final String TAG = "QuizScoreActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_score);

        scoreTextView = findViewById(R.id.scoreTextView);
        exitButton = findViewById(R.id.exitButton);

        // Retrieve data from the Intent
        Intent intent = getIntent();
        quizId = intent.getIntExtra("quiz_id", 0);
        quizScore = intent.getIntExtra("quiz_score", 0);
        totalQuestions = intent.getIntExtra("total_questions", 0); // ensure this is a int
        user_id = intent.getStringExtra("user_id");
        categoryId = intent.getIntExtra("category_id", 0);

        // Example data for selectedChoice, questionId, and totalScore
        String selectedChoice = "A";  // Set default or get from intent if available
        int questionId = 1;           // Set default or get from intent if available
        totalScore = quizScore;       // Assuming totalScore is equivalent to quizScore

        scoreTextView.setText(quizScore + "/" + totalQuestions);

        // Dialog logic based on intent data
        boolean showGreatJobDialog = intent.getBooleanExtra("showGreatJobDialog", false);
        if (showGreatJobDialog) {
            showGreatJobDialog();
        } else {
            showSorryDialog();
        }

        // Log received data for debugging
        Log.d(TAG, "quizId: " + quizId);
        Log.d(TAG, "quizScore: " + quizScore);
        Log.d(TAG, "totalQuestions: " + totalQuestions);
        Log.d(TAG, "userId: " + user_id);
        Log.d(TAG, "categoryId: " + categoryId);


        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Exit button clicked");
                saveQuizScoreAndExit(user_id, quizId, questionId, quizScore, selectedChoice, totalScore);
            }
        });
    }

    private void saveQuizScoreAndExit(final String user_id, final int quizId, final int questionId, final int score, final String selectedChoice, final int totalScore) {
        String url = "http://192.168.100.72/gesture/saveQuizScore.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Response from server: " + response);
                        Intent intent = new Intent(QuizScoreActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error: " + error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("quiz_id", String.valueOf(quizId));
                params.put("question_id", String.valueOf(questionId));
                params.put("score", String.valueOf(score));
                params.put("selected_choice", selectedChoice);
                params.put("total_score", String.valueOf(totalScore));

                return params;
            }
        };

        requestQueue.add(stringRequest);
    }


    private void showGreatJobDialog() {
        if (isFinishing()) return; // Check if Activity is finishing

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.great_job_dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationReport;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ImageButton closeDialog = dialog.findViewById(R.id.closeDialog);
        closeDialog.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showSorryDialog() {
        if (isFinishing()) return; // Check if Activity is finishing

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.sorry_dialog_box);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationReport;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ImageButton closeDialog = dialog.findViewById(R.id.closeDialog);
        closeDialog.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
