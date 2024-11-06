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
    private String quizTitle;
    private int quizScore;
    private int totalQuestions;
    private String user_id;
    private int categoryId;
    private static final String TAG = "QuizScoreActivity";  // For logging


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_quiz_score); // Ensure correct layout file

        // Initialize the views
        scoreTextView = findViewById(R.id.scoreTextView);
        exitButton = findViewById(R.id.exitButton);  // Add the exit button to your layout

        // Get the intent data
        Intent intent = getIntent();
        quizTitle = intent.getStringExtra("quiz_title");
        quizScore = intent.getIntExtra("quiz_score", 0);
        totalQuestions = intent.getIntExtra("total_questions", 0);
        user_id = intent.getStringExtra("user_id");  // Make sure to pass user ID
        categoryId = intent.getIntExtra("category_id", 0); // Make sure to pass category ID

        // Display the score
        scoreTextView.setText(quizScore + "/" + totalQuestions);

        boolean showGreatJobDialog = intent.getBooleanExtra("showGreatJobDialog", false);


        if (showGreatJobDialog) {
            showGreatJobDialog();  // Show the great job dialog if the last answer was correct
        } else {
            showSorryDialog();  // Show the sorry dialog if the last answer was worng
        }




                // Log the received data for debugging
        Log.d(TAG, "quizTitle: " + quizTitle);
        Log.d(TAG, "quizScore: " + quizScore);
        Log.d(TAG, "totalQuestions: " + totalQuestions);
        Log.d(TAG, "userId: " + user_id);
        Log.d(TAG, "categoryId: " + categoryId);

        // Set the click listener for the exit button
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Exit button clicked");  // Log when the button is clicked
                saveQuizScoreAndExit(user_id, categoryId, quizTitle, quizScore, totalQuestions);
            }
        });
    }

    private void saveQuizScoreAndExit(final String user_id, final int categoryId, final String quizTitle, final int quizScore, final int totalQuestions) {
        String url = "http://192.168.100.72/gesture/saveQuizScore.php";  // Your backend URL

        RequestQueue requestQueue = Volley.newRequestQueue(this);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Log the response from the server
                        Log.d(TAG, "Response from server: " + response);

                        // Navigate to the correct activity after saving
                        Intent intent = new Intent(QuizScoreActivity.this, MainActivity.class);  // Change 'MainActivity' to the correct activity
                        startActivity(intent);
                        finish();  // Close the current activity
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log error response
                        Log.e(TAG, "Error: " + error.getMessage());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(user_id));
                params.put("category_id", String.valueOf(categoryId));

                params.put("quiz_title", quizTitle);  // Pass quiz title
                params.put("score", String.valueOf(quizScore));  // Pass score
                params.put("total_questions", String.valueOf(totalQuestions));  // Pass total questions

                return params;
            }
        };


        // Add the request to the RequestQueue
        requestQueue.add(stringRequest);
    }
    private void showGreatJobDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.great_job_dialog);
        dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimationReport;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ImageButton closeDialog = dialog.findViewById(R.id.closeDialog);

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showSorryDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.sorry_dialog_box);
        dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimationReport;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ImageButton closeDialog = dialog.findViewById(R.id.closeDialog);
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
