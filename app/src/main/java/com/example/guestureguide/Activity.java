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
    private int score = 0;
    private int totalQuestions = 0;
    private String quizTitle;
    private String quizId;
    private String user_id;
    private SharedPreferences sharedPreferences;
    private Question currentQuestion; // Define currentQuestion


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
<<<<<<< Updated upstream

        setContentView(R.layout.activity_activity);  // Ensure the correct layout file is used
=======
        setContentView(R.layout.activity_activity);
>>>>>>> Stashed changes


        // Initialize views
        questionTextView = findViewById(R.id.questionTextView);
        radioGroup = findViewById(R.id.radioGroup);
        option1 = findViewById(R.id.radioOption1);
        option2 = findViewById(R.id.radioOption2);
        submitButton = findViewById(R.id.submitButton);

        // Back button functionality
        ImageButton backButton = findViewById(R.id.back_to_quiz_button);
        backButton.setOnClickListener(v -> finish());

<<<<<<< Updated upstream

            }
        });

        // Retrieve quiz title from intent
=======
        // Retrieve quiz title and quiz ID from intent
>>>>>>> Stashed changes
        Intent intent = getIntent();
        quizTitle = intent.getStringExtra("quiz_title");
        quizId = intent.getStringExtra("quiz_id");

        // Retrieve userId from SharedPreferences
        sharedPreferences = getSharedPreferences("MyAppName", MODE_PRIVATE);
        user_id = sharedPreferences.getString("user_id", "").trim();

        // Log information for debugging
        Log.d("Activity", "Quiz Title: " + quizTitle);
        Log.d("Activity", "Quiz ID: " + quizId);
        Log.d("Activity", "User ID: " + user_id);

        // Fetch questions using quizId
        fetchQuestions(quizId);

        // Handle button clicks
        submitButton.setOnClickListener(v -> checkAnswer());
    }

<<<<<<< Updated upstream
    private void fetchQuestions(String quizTitle) {
        // Assuming the API takes quiz_title as a parameter in the URL
        String url = "http://192.168.100.72/gesture/getQuestions.php?quiz_title=" + quizTitle;
=======
    private void fetchQuestions(String quizId) {
        String url = "http://192.168.100.72/gesture/getQuestions.php?quiz_id=" + quizId;
>>>>>>> Stashed changes

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
<<<<<<< Updated upstream
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray questionsArray = response.getJSONArray("questions");
=======
                response -> {
                    try {
                        JSONArray questionsArray = response.getJSONArray("questions");
>>>>>>> Stashed changes

                        // Initialize question list and set total questions
                        questionList = new ArrayList<>();
                        totalQuestions = questionsArray.length();

<<<<<<< Updated upstream

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
=======
                        for (int i = 0; i < totalQuestions; i++) {
                            JSONObject questionObject = questionsArray.getJSONObject(i);
                            String question = questionObject.getString("question_text");
                            String optionA = questionObject.getString("option_1");
                            String optionB = questionObject.getString("option_2");
                            String correctAnswer = questionObject.getString("is_correct");

                            questionList.add(new Question(question, optionA, optionB, correctAnswer));
>>>>>>> Stashed changes
                        }

                        // Load the first question
                        loadQuestion();
                    } catch (JSONException e) {
                        Log.e("Activity", "Error parsing JSON response", e);
                        Toast.makeText(Activity.this, "Error loading questions", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("Activity", "Error fetching questions", error);
                    Toast.makeText(Activity.this, "Error fetching questions", Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void loadQuestion() {
        if (currentQuestionIndex < questionList.size()) {
            currentQuestion = questionList.get(currentQuestionIndex); // Set currentQuestion
            questionTextView.setText(currentQuestion.getQuestionText());
            option1.setText(currentQuestion.getOptionA());
            option2.setText(currentQuestion.getOptionB());

            option1.setTag("A");
            option2.setTag("B");

            radioGroup.clearCheck();
            submitButton.setText(currentQuestionIndex == questionList.size() - 1 ? "Submit" : "Next");
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
<<<<<<< Updated upstream
        //String selectedAnswer = selectedRadioButton.getText().toString();


        String selectedAnswer = selectedRadioButton.getTag().toString();  // Get "A" or "B" value





=======
        String selectedAnswer = selectedRadioButton.getText().toString();
        String selectedTag = (String) selectedRadioButton.getTag();
>>>>>>> Stashed changes

        boolean isCorrect = selectedTag.equals(currentQuestion.getCorrectAnswer()); // Use currentQuestion here
        if (isCorrect) {
            score++;
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
<<<<<<< Updated upstream
            if (currentQuestionIndex < questionList.size() - 1) {
                showGreatJobDialog();
            }

=======
            showGreatJobDialog();
>>>>>>> Stashed changes
        } else {
            Toast.makeText(this, "Incorrect. The correct answer is: " + currentQuestion.getCorrectAnswer(), Toast.LENGTH_LONG).show();
            showSorryDialog();
        }

        // Move to the next question or show the score
        if (currentQuestionIndex == questionList.size() - 1) {
<<<<<<< Updated upstream

            // Handle the final submission and navigate to QuizScoreActivity
=======
>>>>>>> Stashed changes
            Intent intent = new Intent(Activity.this, QuizScoreActivity.class);
            intent.putExtra("quiz_score", score);
            intent.putExtra("total_questions", totalQuestions);
            intent.putExtra("quiz_title", quizTitle);
            intent.putExtra("quiz_id", quizId);
            intent.putExtra("user_id", user_id);
            startActivity(intent);
<<<<<<< Updated upstream
            finish();  // Close the current activity

=======
            finish();
>>>>>>> Stashed changes
        } else {
            currentQuestionIndex++;
            loadQuestion();
        }
    }

    private void showGreatJobDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.great_job_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.findViewById(R.id.closeDialog).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showSorryDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.sorry_dialog_box);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.findViewById(R.id.closeDialog).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
