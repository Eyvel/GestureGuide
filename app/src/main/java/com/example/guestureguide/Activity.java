package com.example.guestureguide;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class Activity extends AppCompatActivity {

    private TextView questionTextView;
    private RadioGroup radioGroup;
    private RadioButton option1, option2;
    private Button submitButton;
    private String categoryId;
    private ArrayList<Question> questionList;
    private int currentQuestionIndex = 0;
    private Question currentQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity);

        // Get category ID from the intent
        categoryId = getIntent().getStringExtra("id");

        // Initialize UI components
        questionTextView = findViewById(R.id.questionTextView);
        radioGroup = findViewById(R.id.radioGroup);
        option1 = findViewById(R.id.radioOption1);
        option2 = findViewById(R.id.radioOption2);

        submitButton = findViewById(R.id.submitButton);

        // Fetch questions for the category
        if (categoryId != null) {
            fetchQuestions(categoryId);
        } else {
            Toast.makeText(this, "Category ID is missing", Toast.LENGTH_SHORT).show();
        }

        // Handle button clicks (Next or Submit)
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });
    }

    private void fetchQuestions(String categoryId) {
        String url = "http://192.168.8.7/gesture/getQuestions.php?category_id=" + categoryId;  // Adjust URL as needed
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d("Activity", response.toString());
                        questionList = new ArrayList<>();
                        try {
                            Log.d("Activity", "Questions fetched: " + questionList.size());

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject questionObject = response.getJSONObject(i);
                                String question = questionObject.getString("question");
                                String optionA = questionObject.getString("option_a");
                                String optionB = questionObject.getString("option_b");



                                String correctAnswer = questionObject.getString("correct_answer");

                                questionList.add(new Question(question, optionA, optionB, correctAnswer));
                            }
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
        requestQueue.add(jsonArrayRequest);
    }

    private void loadQuestion() {
        if (currentQuestionIndex < questionList.size()) {
            currentQuestion = questionList.get(currentQuestionIndex);
            questionTextView.setText(currentQuestion.getQuestionText());
            option1.setText(currentQuestion.getOptionA());
            option2.setText(currentQuestion.getOptionB());

            radioGroup.clearCheck();  // Clear previous selection

            // Check if this is the last question
            if (currentQuestionIndex == questionList.size() - 1) {
                submitButton.setText("Submit");  // Change text to "Submit"
            } else {
                submitButton.setText("Next");  // Change text to "Next"
            }
        } else {
            Toast.makeText(this, "You have completed all questions!", Toast.LENGTH_SHORT).show();
            // You can navigate back or show results here
        }
    }

    private void checkAnswer() {
        int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        if (selectedRadioButtonId == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
        String selectedAnswer = selectedRadioButton.getText().toString();

        if (selectedAnswer.equals(currentQuestion.getCorrectAnswer())) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Incorrect. The correct answer is: " + currentQuestion.getCorrectAnswer(), Toast.LENGTH_LONG).show();
        }

        // Check if it is the last question
        if (currentQuestionIndex == questionList.size() - 1) {
            // This is the last question, so handle the final submission
            Toast.makeText(this, "Activity completed!", Toast.LENGTH_SHORT).show();
            // You can submit results or navigate to another activity here
            finish();
        } else {
            currentQuestionIndex++;
            loadQuestion();  // Load the next question
        }
    }
}
