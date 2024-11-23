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
    private String quizId;
    private int quizScore;
    private int totalQuestionItems;
    private String user_id;
    private static final String TAG = "QuizScoreActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_score);

        scoreTextView = findViewById(R.id.scoreTextView);
        exitButton = findViewById(R.id.finishButton);

        // Retrieve data from the Intent
        Intent intent = getIntent();
        quizId = intent.getStringExtra("quiz_id");
        quizScore = intent.getIntExtra("quiz_score", 0);
        totalQuestionItems = intent.getIntExtra("total_question_items", 0);
        user_id = intent.getStringExtra("user_id");

        // Set the score display
        scoreTextView.setText(quizScore + "/" + totalQuestionItems);

        // Dialog logic based on intent data
        boolean showGreatJobDialog = intent.getBooleanExtra("showGreatJobDialog", false);
        if (showGreatJobDialog) {
            showGreatJobDialog();
        } else {
            showSorryDialog();
        }


        // Log received data for debugging
        /*
        Log.d(TAG, "quizId: " + quizId);
        Log.d(TAG, "quizScore: " + quizScore);
        Log.d(TAG, "totalQuestions: " + totalQuestionItems);
        Log.d(TAG, "userId: " + user_id);

         */

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Exit button clicked");

                Log.d(TAG, "Logging details - user_id: " + user_id + ", quiz_id: " + quizId + ", total_score: " + quizScore + ", total_items: " + totalQuestionItems);
                saveQuizScoreAndExit(user_id, quizId, quizScore, totalQuestionItems);
            }
        });
    }


    private void saveQuizScoreAndExit(final String user_id, final String quizId, final int totalScore, final int totalItems) {
        String url = "https://gestureguide.com/auth/mobile/saveQuizSummary.php";

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
                params.put("total_score", String.valueOf(totalScore));
                params.put("total_items", String.valueOf(totalItems));

                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void showGreatJobDialog() {
        if (isFinishing()) return;

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.great_job_dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationReport;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ImageButton closeDialog = dialog.findViewById(R.id.closeDialog);
        closeDialog.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showSorryDialog() {
        if (isFinishing()) return;

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.sorry_dialog_box);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationReport;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ImageButton closeDialog = dialog.findViewById(R.id.closeDialog);
        closeDialog.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
