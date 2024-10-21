package com.example.guestureguide;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Activity extends AppCompatActivity {

    private TextView questionTextView;
    private RadioGroup radioGroup;
    private RadioButton option1, option2;
    private Button submitButton;
    private ArrayList<Question> questionList;
    private int currentQuestionIndex = 0;
    private Question currentQuestion;
    private int score = 0;  // Track the score
    private int totalQuestions = 0;  // Track total number of questions
    private String quizTitle;
    private String user_id; // User ID variable
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity);  // Ensure the correct layout file is used

        // Initialize views
        questionTextView = findViewById(R.id.questionTextView);
        radioGroup = findViewById(R.id.radioGroup);
        option1 = findViewById(R.id.radioOption1);
        option2 = findViewById(R.id.radioOption2);
        submitButton = findViewById(R.id.submitButton);
        //back for acitivity
        ImageButton backButton = findViewById(R.id.back_to_quiz_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        // Retrieve quiz title from intent
        Intent intent = getIntent();
        quizTitle = intent.getStringExtra("quiz_title");

        // Retrieve userId from SharedPreferences
        sharedPreferences = getSharedPreferences("MyAppName", MODE_PRIVATE);
        user_id = sharedPreferences.getString("user_id", "").trim();

        // Log to check if the quiz title and user ID are fetched correctly
        Log.d("Activity", "Quiz Title: " + quizTitle);
        Log.d("Activity", "User ID: " + user_id);


        // Use quizTitle to fetch related questions
        fetchQuestions(quizTitle);

        // Handle button clicks
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });
    }

    private void fetchQuestions(String quizTitle) {
        // Assuming the API takes quiz_title as a parameter in the URL
        String url = "http://192.168.100.72/gesture/getQuestions.php?quiz_title=" + quizTitle;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray questionsArray = response.getJSONArray("questions");

                            // Initialize question list and set total questions
                            questionList = new ArrayList<>();
                            totalQuestions = questionsArray.length();

                            for (int i = 0; i < questionsArray.length(); i++) {
                                JSONObject questionObject = questionsArray.getJSONObject(i);
                                String question = questionObject.getString("question");
                                String optionA = questionObject.getString("option_a");
                                String optionB = questionObject.getString("option_b");
                                String correctAnswer = questionObject.getString("correct_answer");

                                questionList.add(new Question(question, optionA, optionB, correctAnswer));
                            }

                            // Load the first question
                            loadQuestion();
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

        requestQueue.add(jsonObjectRequest);
    }

    private void loadQuestion() {
        if (currentQuestionIndex < questionList.size()) {
            currentQuestion = questionList.get(currentQuestionIndex);
            questionTextView.setText(currentQuestion.getQuestionText());
            option1.setText(currentQuestion.getOptionA());
            option2.setText(currentQuestion.getOptionB());

            radioGroup.clearCheck();  // Clear previous selection

            if (currentQuestionIndex == questionList.size() - 1) {
                submitButton.setText("Submit");
            } else {
                submitButton.setText("Next");
            }
        } else {
            Toast.makeText(this, "You have completed all questions!", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkAnswer() {
        option1.setTag("A");
        option2.setTag("B");
        int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();

        if (selectedRadioButtonId == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
        //String selectedAnswer = selectedRadioButton.getText().toString();


        String selectedAnswer = selectedRadioButton.getTag().toString();  // Get "A" or "B" value





        // Check if the answer is correct
        boolean isCorrect = selectedAnswer.equals(currentQuestion.getCorrectAnswer());
        if (isCorrect) {
            score++;  // Increment score for correct answers
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            if (currentQuestionIndex < questionList.size() - 1) {
                showGreatJobDialog();
            }
        } else {
            Toast.makeText(this, "Incorrect. The correct answer is: " + currentQuestion.getCorrectAnswer(), Toast.LENGTH_LONG).show();
            if (currentQuestionIndex < questionList.size() - 1) {
                showSorryDialog();
            }
        }

        // Check if it is the last question
        if (currentQuestionIndex == questionList.size() - 1) {
            // Handle the final submission and navigate to QuizScoreActivity
            Intent intent = new Intent(Activity.this, QuizScoreActivity.class);
            intent.putExtra("quiz_score", score);
            intent.putExtra("total_questions", totalQuestions);  // Pass total number of questions
            intent.putExtra("quiz_title", quizTitle);  // Pass the quiz title
            intent.putExtra("user_id", user_id);  // Pass the retrieved userId
            intent.putExtra("showGreatJobDialog", isCorrect);  // Pass whether the last answer was correct or not
            startActivity(intent);
            finish();  // Close the current activity
        } else {
            currentQuestionIndex++;
            loadQuestion();  // Load the next question
        }
    }

    private void showGreatJobDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.great_job_dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationReport;
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
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationReport;
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
