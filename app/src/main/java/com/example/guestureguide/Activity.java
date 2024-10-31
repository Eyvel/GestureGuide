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

import java.util.ArrayList;

public class Activity extends AppCompatActivity {

    private TextView questionTextView;
    private VideoView questionVideoView;
    private ImageView option1ImageView, option2ImageView;
    private Button submitButton;



    private ArrayList<Question> questionList = new ArrayList<>();

    private int currentQuestionIndex = 0;
    private int totalScore = 0;
    private String quizTitle;
    private String quizId;
    private String userId;
    private SharedPreferences sharedPreferences;
    private Question currentQuestion;
    private int selectedOptionIndex = -1;
    private Dialog currentDialog;

    private int currentQuestionScore = 0;


    @Override
    protected void onPause() {
        super.onPause();
        saveQuizState(quizId);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity);

        initializeViews();
        retrieveIntentData();
        setupBackButton();
        setupOptionClickListeners();
        loadSavedQuizState(quizId);
        setupSubmitButton();

        fetchQuestions(quizId);
    }
    private void saveQuizState(String quizId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("current_question_index_" + quizId, currentQuestionIndex);
        editor.putInt("total_score_" + quizId, totalScore);
        editor.putString("current_question_text_" + quizId, currentQuestion != null ? currentQuestion.getQuestionText() : "");
        editor.putInt("selected_option_index_" + quizId, selectedOptionIndex); // Save selected option index
        editor.apply();
    }


    private void loadSavedQuizState(String quizId) {
        currentQuestionIndex = sharedPreferences.getInt("current_question_index_" + quizId, 0);
        totalScore = sharedPreferences.getInt("total_score_" + quizId, 0);
        selectedOptionIndex = sharedPreferences.getInt("selected_option_index_" + quizId, -1);

        if (currentQuestionIndex < questionList.size()) {
            currentQuestion = questionList.get(currentQuestionIndex);
        }
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
        quizId = intent.getStringExtra("quiz_id");

        Log.d("Activity", "Quiz Title: " + quizTitle);
        Log.d("Activity", "Quiz ID: " + quizId);

        sharedPreferences = getSharedPreferences("MyAppName", MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", "").trim();

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
        String url = "http://192.168.8.20/gesture/getQuestions.php?quiz_id=" + quizId;
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
            JSONArray questionsArray = response.getJSONArray("questions");
            questionList = new ArrayList<>();
            for (int i = 0; i < questionsArray.length(); i++) {
                JSONObject questionObject = questionsArray.getJSONObject(i);
                // Populate questionList here
                questionList.add(new Question(
                        questionObject.getInt("question_id"),
                        questionObject.getString("question_text"),
                        questionObject.getString("option_1"),
                        questionObject.getString("option_2"),
                        questionObject.getString("is_correct"),
                        questionObject.getString("question_video"),
                        questionObject.getInt("points")
                ));
            }
            loadSavedQuizState(quizId); // Load saved state after questions are fetched
            loadQuestion(); // Load the first question after populating the list
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
            // Load question based on the current index
            currentQuestion = questionList.get(currentQuestionIndex);
            questionTextView.setText(currentQuestion.getQuestionText());
            loadVideo();
            loadOptions();

            // Highlight the previously selected option
            if (selectedOptionIndex != -1) {
                selectOption(selectedOptionIndex);
            }

            submitButton.setText(currentQuestionIndex == questionList.size() - 1 ? "Submit" : "Next");

        } else {
            Log.e("Activity", "Question list is empty");
            Toast.makeText(this, "No questions available!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private void loadVideo() {
        String videoUrl = "http://192.168.8.20/gesture/" + currentQuestion.getQuestionVideo();
        questionVideoView.setVideoURI(Uri.parse(videoUrl));
        questionVideoView.start();
    }

    private void loadOptions() {
        String optionAUrl = "http://192.168.8.20/gesture/" + currentQuestion.getOptionA();
        String optionBUrl = "http://192.168.8.20/gesture/" + currentQuestion.getOptionB();
        Glide.with(this).load(optionAUrl).into(option1ImageView);
        Glide.with(this).load(optionBUrl).into(option2ImageView);

        option1ImageView.setTag("A");
        option2ImageView.setTag("B");
    }

    private void selectOption(int optionIndex) {
        selectedOptionIndex = optionIndex;

        option1ImageView.setBackgroundResource(optionIndex == 0 ? R.drawable.selected_background : 0);
        option2ImageView.setBackgroundResource(optionIndex == 1 ? R.drawable.selected_background : 0);
    }
    private void clearQuizData(String quizId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("current_question_index_" + quizId);
        editor.remove("total_score_" + quizId);
        editor.remove("current_question_text_" + quizId);
        editor.remove("selected_option_index_" + quizId);
        editor.apply();
    }

    private void checkAnswer() {
        if (selectedOptionIndex == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedTag = selectedOptionIndex == 0 ? (String) option1ImageView.getTag() : (String) option2ImageView.getTag();
        String correctAnswer = currentQuestion.getCorrectAnswer();
        currentQuestionScore = (selectedTag.equals(correctAnswer)) ? currentQuestion.getPoints() : 0;

        if (selectedTag.equals(correctAnswer)) {
            totalScore += currentQuestion.getPoints();
            showGreatJobDialog();
        } else if (currentQuestionIndex != questionList.size() - 1) {
            showSorryDialog();
        }

        int totalPoints = calculateCurrentQuestionPoints();
        sendUserResponseToDatabase(userId, quizId, currentQuestion.getQuestionId(), currentQuestionScore, selectedTag, totalPoints, totalScore);

        if (currentQuestionIndex == questionList.size() - 1) {
            clearQuizData(quizId);  // Clear quiz data before finishing
            finishQuiz(quizId);
        } else {
            currentQuestionIndex++;
            loadQuestion();
        }
    }

    private void sendUserResponseToDatabase(String userId, String quizId, int questionId, int score, String selectedChoice, int totalPoints, int totalScore) {
        String url = "http://192.168.8.20/gesture/saveQuizScore.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("user_id", userId);
            jsonBody.put("quiz_id", quizId);
            jsonBody.put("question_id", questionId);
            jsonBody.put("score", score);
            jsonBody.put("selected_choice", selectedChoice);
            jsonBody.put("total_points", totalPoints);
            jsonBody.put("total_score", totalScore);

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

    private void finishQuiz(String quizId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("current_question_index_" + quizId);
        editor.remove("total_score_" + quizId);
        editor.remove("current_question_text_" + quizId);
        editor.remove("selected_option_index_" + quizId);
        editor.apply();

        Intent intent = new Intent(Activity.this, QuizScoreActivity.class);
        intent.putExtra("quiz_score", totalScore);
        intent.putExtra("total_score", calculateTotalPoints());
        intent.putExtra("total_question_items", calculateTotalPoints());
        intent.putExtra("quiz_title", quizTitle);
        intent.putExtra("quiz_id", quizId);
        intent.putExtra("user_id", userId);

        startActivity(intent);
        finish();
    }


    private int calculateCurrentQuestionPoints() {
        if (currentQuestionIndex < questionList.size()) {
            // Return the points for the current question
            return questionList.get(currentQuestionIndex).getPoints();
        }
        return 0; // In case there are no questions available
    }
    private int calculateTotalPoints() {

            int questionPoint = 0;
            for (int i = 0; i <= currentQuestionIndex; i++) {
                questionPoint += questionList.get(i).getPoints();
            }
            return questionPoint;

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
