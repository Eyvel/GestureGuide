package com.example.guestureguide;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;

public class Activity extends AppCompatActivity {

    private TextView questionTextView;
    private VideoView questionVideoView;
    private ImageView option1ImageView, option2ImageView;
    private Button submitButton;

    private ArrayList<Question> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private String quizTitle;
    private String quizId;
    private String userId;
    private SharedPreferences sharedPreferences;
    private Question currentQuestion;
    private int selectedOptionIndex = -1; // -1 means no option is selected
    private Dialog currentDialog; // Dialog reference for showing feedback

    private int currentQuestionScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity);


        initializeViews();
        retrieveIntentData();
        setupBackButton();
        setupOptionClickListeners();
        setupSubmitButton();

        // Fetch questions using quizId
        fetchQuestions(quizId);
    }

    private void initializeViews() {
        questionTextView = findViewById(R.id.questionTextView);
        questionVideoView = findViewById(R.id.questionVideoView);
        option1ImageView = findViewById(R.id.option1ImageView);
        option2ImageView = findViewById(R.id.option2ImageView);
        submitButton = findViewById(R.id.submitButton);
    }

    private void retrieveIntentData() {
        Intent intent = getIntent();
        quizTitle = intent.getStringExtra("quiz_title");
        quizId = intent.getStringExtra("quiz_id"); // Fetch quizId from Intent

        Log.d("Activity", "Quiz Title: " + quizTitle);
        Log.d("Activity", "Quiz ID: " + quizId);

        // Retrieve userId from SharedPreferences
        sharedPreferences = getSharedPreferences("MyAppName", MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", "").trim();

        Log.d("Activity", "Quiz Title: " + quizTitle);
        Log.d("Activity", "Quiz ID: " + quizId);
        Log.d("Activity", "User ID: " + userId);
    }

    private void setupBackButton() {
        ImageButton backButton = findViewById(R.id.back_to_quiz_button);
        backButton.setOnClickListener(v -> finish());
    }

    private void setupOptionClickListeners() {
        option1ImageView.setOnClickListener(v -> selectOption(0));
        option2ImageView.setOnClickListener(v -> selectOption(1));
    }

    private void setupSubmitButton() {
        submitButton.setOnClickListener(v -> checkAnswer());
    }

    private void fetchQuestions(String quizId) {
        String url = "http://192.168.100.72/gesture/getQuestions.php?quiz_id=" + quizId;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                this::parseQuestionsResponse,
                this::handleError
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void parseQuestionsResponse(JSONObject response) {
        try {
            // Adjust based on the actual structure of the response
            JSONArray questionsArray = response.getJSONArray("questions");
            questionList = new ArrayList<>();
            Log.d("Activity", "Questions array length: " + questionsArray.length());

            for (int i = 0; i < questionsArray.length(); i++) {
                JSONObject questionObject = questionsArray.getJSONObject(i);
                int questionId = questionObject.getInt("question_id");
                String questionText = questionObject.getString("question_text");
                String optionA = questionObject.getString("option_1");
                String optionB = questionObject.getString("option_2");
                String correctAnswer = questionObject.getString("is_correct");
                String questionVideo = questionObject.getString("question_video");
                int points = questionObject.getInt("points");

                Log.d("Activity", "Number of points fetched: " + points);

                questionList.add(new Question(questionId, questionText, optionA, optionB, correctAnswer, questionVideo, points));
            }

            loadQuestion();
            Log.d("Activity", "Number of questions fetched: " + questionList.size());
        } catch (JSONException e) {
            Log.e("Activity", "Error parsing JSON response", e);
            Toast.makeText(Activity.this, "Error loading questions", Toast.LENGTH_SHORT).show();
        }
    }


    private void handleError(VolleyError error) {
        Log.e("Activity", "Error fetching questions", error);
        Toast.makeText(Activity.this, "Error fetching questions", Toast.LENGTH_SHORT).show();
    }

    private void loadQuestion() {
        if (questionList != null && !questionList.isEmpty()) {
            currentQuestion = questionList.get(currentQuestionIndex);
            questionTextView.setText(currentQuestion.getQuestionText());
            loadVideo();
            loadOptions();

            submitButton.setText(currentQuestionIndex == questionList.size() - 1 ? "Submit" : "Next");

            // Reset selected option
            selectedOptionIndex = -1;
            option1ImageView.setBackgroundResource(0);
            option2ImageView.setBackgroundResource(0);
        } else {
            Log.e("Activity", "Question list is empty");
            Toast.makeText(this, "No questions available!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadVideo() {
        String videoUrl = "http://192.168.100.72/gesture/" + currentQuestion.getQuestionVideo();
        questionVideoView.setVideoURI(Uri.parse(videoUrl));
        questionVideoView.start();
    }

    private void loadOptions() {
        String optionAUrl = "http://192.168.100.72/gesture/" + currentQuestion.getOptionA();
        String optionBUrl = "http://192.168.100.72/gesture/" + currentQuestion.getOptionB();
        Glide.with(this).load(optionAUrl).into(option1ImageView);
        Glide.with(this).load(optionBUrl).into(option2ImageView);

        option1ImageView.setTag("A");
        option2ImageView.setTag("B");
    }

    private void selectOption(int optionIndex) {
        selectedOptionIndex = optionIndex;

        // Update UI to reflect the selection
        option1ImageView.setBackgroundResource(optionIndex == 0 ? R.drawable.selected_background : 0);
        option2ImageView.setBackgroundResource(optionIndex == 1 ? R.drawable.selected_background : 0);
    }

    private void checkAnswer() {
        if (selectedOptionIndex == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the selected answer based on the tag
        String selectedTag = selectedOptionIndex == 0 ? (String) option1ImageView.getTag() : (String) option2ImageView.getTag();
        String correctAnswer = currentQuestion.getCorrectAnswer();
        currentQuestionScore = (selectedTag.equals(correctAnswer)) ? currentQuestion.getPoints() : 0; // Send 0 if answer is wrong


        // Calculate score based on the answer
        if (selectedTag.equals(correctAnswer)) {
            score += currentQuestion.getPoints();
            showGreatJobDialog(); // Show the "Great Job" dialog
        }  else {
            // Check if it's the last question before showing the dialog
            if (currentQuestionIndex != questionList.size() - 1) {
                showSorryDialog(); // Show the "Sorry" dialog only if not the last question
            }
        }

        // Prepare data to send to the database
        int totalScore = calculateTotalPoints(); // Assuming this is the total points of the quiz
        sendUserResponseToDatabase(userId, quizId, currentQuestion.getQuestionId(), score, selectedTag, totalScore);

        // Check if it's the last question
        if (currentQuestionIndex == questionList.size() - 1) {
            finishQuiz();
        } else {
            currentQuestionIndex++;
            loadQuestion(); // Load the next question
        }
    }

    private void sendUserResponseToDatabase(String userId, String quizId, int questionId, int score, String selectedChoice, int totalScore) {
        String url = "http://192.168.100.72/gesture/saveQuizScore.php"; // Change to your actual endpoint
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("user_id", userId);
            jsonBody.put("quiz_id", quizId);

            jsonBody.put("question_id", questionId);
            jsonBody.put("score", currentQuestionScore);
            jsonBody.put("selected_choice", selectedChoice);
            jsonBody.put("total_score", currentQuestion.getPoints());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonBody,
                    response -> Log.d("Activity", "Response: " + response.toString()),
                    error -> Log.e("Activity", "Error saving response", error)
            );

            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            Log.e("Activity", "Error creating JSON object", e);
            Toast.makeText(this, "Error saving your answer", Toast.LENGTH_SHORT).show();
        }
    }


    private void finishQuiz() {
        Intent intent = new Intent(Activity.this, QuizScoreActivity.class);
        intent.putExtra("quiz_score", score);
        intent.putExtra("total_questions", calculateTotalPoints());
        intent.putExtra("quiz_title", quizTitle);
        intent.putExtra("quiz_id", quizId);
        intent.putExtra("user_id", userId);


        startActivity(intent);
        finish();
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


    private Dialog createDialog(int layoutRes) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(layoutRes);
        dialog.setCancelable(false);
        return dialog;
    }

    private void dismissCurrentDialog() {
        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
            currentDialog = null; // Clear reference
        }
    }

    private int calculateTotalPoints() {
        int totalPoints = 0;
        for (Question question : questionList) {
            totalPoints += question.getPoints();
        }
        return totalPoints;
    }
}
