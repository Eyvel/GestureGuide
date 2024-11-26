package com.example.guestureguide;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
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
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.MediaItem;


import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Activity extends AppCompatActivity {

    private TextView questionTextView,questionIndexTextView;
    private PlayerView questionVideoView;
    private ImageView option1ImageView, option2ImageView;
    private Button submitButton;

    private MediaPlayer mediaPlayer;
    private ExoPlayer exoPlayer;



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
    private TextView quizTitleTextView;


    private int currentQuestionScore = 0;


    @Override
    protected void onPause() {
        super.onPause();
        if (exoPlayer != null) {
            exoPlayer.stop();     // Stop the video playback
            exoPlayer.release();  // Release resources
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (exoPlayer != null) {
            exoPlayer.release();
        }
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
        Log.d("saveQuizState", "saveQuizState method called");

        String url = "https://gestureguide.com/auth/mobile/saveQuizState.php";  // Adjust the URL to your server endpoint
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Prepare the data to send
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
            jsonBody.put("quiz_id", quizId);
            jsonBody.put("current_question_index", currentQuestionIndex);
            jsonBody.put("total_score", totalScore);
            jsonBody.put("selected_option_index", selectedOptionIndex);

            Log.d("saveQuizState", "User ID: " + userId);
            Log.d("saveQuizState", "Quiz ID: " + quizId);
            Log.d("saveQuizState", "Current Question Index: " + currentQuestionIndex);
            Log.d("saveQuizState", "Total Score: " + totalScore);
            Log.d("saveQuizState", "Selected Option Index: " + selectedOptionIndex);


            Log.d("saveQuizState", "Request Body: " + jsonBody.toString());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonBody,
                    response -> {
                        Log.d("saveQuizState", "Response: " + response.toString());
                    },
                    error -> {
                        Log.e("saveQuizState", "Error saving quiz state", error);
                        if (error.networkResponse != null) {
                            Log.e("saveQuizState", "Error Code: " + error.networkResponse.statusCode);
                            Log.e("saveQuizState", "Error Body: " + new String(error.networkResponse.data));
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json"); // Add any other required headers here
                    return headers;
                }
            };


            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            Log.e("saveQuizState", "Error creating JSON object", e);
            Toast.makeText(this, "Error saving quiz state", Toast.LENGTH_SHORT).show();
        }
    }




    private void loadSavedQuizState(String quizId) {
        // Construct the URL to fetch quiz state from the server
        String url = "https://gestureguide.com/auth/mobile/getQuizState.php?quiz_id=" + quizId + "&user_id=" + userId;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    Log.d("Activity", "Response: " + response.toString());

                    try {
                        // Check if quiz_state is null or empty
                        if (response.isNull("quiz_state") || response.getJSONObject("quiz_state").length() == 0) {
                            // Handle empty or null state, e.g., reset quiz
                            Log.d("loadSavedQuizState", "No quiz state found. Starting a new quiz.");

                            // Reset quiz-related data to default values
                            currentQuestionIndex = 0;
                            totalScore = 0;
                            selectedOptionIndex = -1;
                            // Reset any other quiz-related data as needed

                        } else {
                            // Quiz state exists and is valid
                            JSONObject quizState = response.getJSONObject("quiz_state");

                            // Extract values from the quiz state and update the UI
                            currentQuestionIndex = quizState.getInt("current_question_index");
                            totalScore = quizState.getInt("total_score");
                            selectedOptionIndex = quizState.getInt("selected_option_index");

                            // Load the current question after setting the state
                            if (currentQuestionIndex < questionList.size()) {
                                currentQuestion = questionList.get(currentQuestionIndex);
                            }

                            loadQuestion(); // Load the question after loading state
                        }
                    } catch (JSONException e) {
                        Log.e("Activity", "Error parsing quiz state", e);
                    }
                },
                error -> {
                    Log.e("Activity", "Error fetching quiz state", error);
                }
        );

        // Add the request to the request queue
        requestQueue.add(jsonObjectRequest);
    }





    private void initializeViews() {
        quizTitleTextView = findViewById(R.id.title_quiz_text_view);
        questionTextView = findViewById(R.id.questionTextView);
        questionVideoView = findViewById(R.id.questionVideoView);
        option1ImageView = findViewById(R.id.option1ImageView);
        option2ImageView = findViewById(R.id.option2ImageView);
        submitButton = findViewById(R.id.submitButton);
        questionIndexTextView = findViewById(R.id.questionIndexTextView);
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
        quizTitleTextView.setText(quizTitle);
    }

    private void setupBackButton() {
        ImageButton backButton = findViewById(R.id.back_to_quiz_button);
        backButton.setOnClickListener(v -> {
            // Stop the video when the back button is pressed
            if (exoPlayer != null) {
                exoPlayer.stop();     // Stop the video playback
                exoPlayer.release();  // Release resources
                exoPlayer = null;     // Clear the ExoPlayer reference
            }

            // Finish the activity (navigate back)
            finish();
        });
    }



    private void setupOptionClickListeners() {
        option1ImageView.setOnClickListener(v -> selectOption(0));
        option2ImageView.setOnClickListener(v -> selectOption(1));
    }

    private void setupSubmitButton() {
        submitButton.setOnClickListener(v -> {
            checkAnswer();
            saveQuizState(quizId);
                }

        );
    }


    private void fetchQuestions(String quizId) {
        String url = "https://gestureguide.com/auth/mobile/getQuestions.php?quiz_id=" + quizId;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    // Parse the response and populate the question list
                    parseQuestionsResponse(response);

                    // Only proceed if the question list is not empty
                    if (questionList != null && !questionList.isEmpty()) {
                        // Now load the saved quiz state since questions are available
                        loadSavedQuizState(quizId);
                    } else {
                        Log.e("Activity", "Failed to fetch questions or list is empty");
                        Toast.makeText(this, "No questions available for this quiz!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                },
                this::handleError
        );

        requestQueue.add(jsonObjectRequest);
    }








    private void parseQuestionsResponse(JSONObject response) {
        try {
            if (response.has("questions") && !response.isNull("questions")) {
                JSONArray questionsArray = response.getJSONArray("questions");
                questionList = new ArrayList<>();
                for (int i = 0; i < questionsArray.length(); i++) {
                    JSONObject questionObject = questionsArray.getJSONObject(i);
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
                if (!questionList.isEmpty()) {
                    loadQuestion(); // Load the first question if the list is populated
                } else {
                    Log.e("Activity", "No questions found in the response");
                    Toast.makeText(this, "No questions available for this quiz!", Toast.LENGTH_SHORT).show();
                    finish(); // Exit the activity if no questions are available
                }
            } else {
                Log.e("Activity", "Questions key missing in the response");
                Toast.makeText(this, "Error: No questions data found!", Toast.LENGTH_SHORT).show();
                finish(); // Exit the activity if the response is invalid
            }
        } catch (JSONException e) {
            Log.e("Activity", "Error parsing JSON response", e);
            Toast.makeText(this, "Error loading questions", Toast.LENGTH_SHORT).show();
        }
    }




    private void handleError(VolleyError error) {
        Log.e("Activity", "Error fetching questions", error);
        if (error.networkResponse != null && error.networkResponse.data != null) {
            String errorMessage = new String(error.networkResponse.data);
            Log.e("Activity", "Error response body: " + errorMessage);
        }
        Toast.makeText(this, "Error fetching questions", Toast.LENGTH_SHORT).show();
    }


    private void loadQuestion() {
        try {
            if (questionList != null && !questionList.isEmpty() && currentQuestionIndex >= 0 && currentQuestionIndex < questionList.size()) {
                currentQuestion = questionList.get(currentQuestionIndex);

                if (currentQuestion != null) {
                    questionTextView.setText(currentQuestion.getQuestionText());
                    loadVideo();
                    loadOptions();

                    // Highlight the previously selected option
                    if (selectedOptionIndex != -1) {
                        selectOption(selectedOptionIndex);
                    }

                    submitButton.setText(currentQuestionIndex == questionList.size() - 1 ? "Submit" : "Next");

                    updateQuestionIndex();
                } else {
                    Log.e("Activity", "currentQuestion is null");
                }
            } else {
                Log.e("Activity", "Invalid question list or index");
            }
        } catch (IndexOutOfBoundsException e) {
            Log.e("Activity", "IndexOutOfBoundsException: " + e.getMessage());
            Toast.makeText(this, "An error occurred while loading the question.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("Activity", "Unexpected error: " + e.getMessage());
            Toast.makeText(this, "An unexpected error occurred.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadVideo() {
        String videoUrl = "https://gestureguide.com/" + currentQuestion.getQuestionVideo();

        // Initialize ExoPlayer using the builder
        exoPlayer = new ExoPlayer.Builder(this).build(); // Directly using ExoPlayer

        // Set the ExoPlayer to the PlayerView
        questionVideoView.setPlayer(exoPlayer);

        // Set a listener to loop the video
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    if (exoPlayer != null) { // Add null check here
                        exoPlayer.seekTo(0);
                        exoPlayer.play();
                    }
                }
            }
        });

        // Create a MediaItem for the video URL
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));

        // Set the MediaItem to the player
        exoPlayer.setMediaItem(mediaItem);



        // Prepare and start the video
        exoPlayer.prepare();
        exoPlayer.play();
    }

    private void loadOptions() {
        String optionAUrl = "https://gestureguide.com/" + currentQuestion.getOptionA();
        String optionBUrl = "https://gestureguide.com/" + currentQuestion.getOptionB();
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
        boolean isCorrect = selectedTag.equals(correctAnswer);

        currentQuestionScore = isCorrect ? currentQuestion.getPoints() : 0;

        if (isCorrect) {
            totalScore += currentQuestion.getPoints();
            showGreatJobDialog(); // Show "Great Job" dialog for correct answers
        } else {
            showSorryDialog(); // Show "Sorry" dialog for incorrect answers
        }

        int totalPoints = calculateCurrentQuestionPoints();
        sendUserResponseToDatabase(userId, quizId, currentQuestion.getQuestionId(), currentQuestionScore, selectedTag, totalPoints, totalScore);

        if (currentQuestionIndex == questionList.size() - 1) {
            clearQuizData(quizId); // Clear quiz data before finishing
            finishQuiz(quizId, isCorrect);
        } else {
            currentQuestionIndex++;
            loadQuestion();

        }
    }



    private void sendUserResponseToDatabase(String userId, String quizId, int questionId, int score, String selectedChoice, int totalPoints, int totalScore) {
        String url = "https://gestureguide.com/auth/mobile/saveQuizScore.php";
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
            Log.d("Activity", "user Id:"+userId);
            Log.d("Activity", "quiz Id:"+quizId);

            Log.d("Activity", "question Id:"+questionId);

            Log.d("Activity", "score:"+score);

            Log.d("Activity", "selected choice:"+selectedChoice);

            Log.d("Activity", "total poitisn:"+totalPoints);
            Log.d("Activity", "total score :"+totalScore);


        } catch (JSONException e) {
            Log.e("Activity", "Error creating JSON object", e);
            Toast.makeText(this, "Error saving your answer", Toast.LENGTH_SHORT).show();
        }
    }

    private void finishQuiz(String quizId, boolean showGreatJobDialog) {
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
        intent.putExtra("showGreatJobDialog", showGreatJobDialog);
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

        // Play sound effect
        playSound(R.raw.correct_sound);

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

        // Play sound effect
        playSound(R.raw.wrong_sound);

        ImageButton closeDialog = dialog.findViewById(R.id.closeDialog);
        closeDialog.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void playSound(int soundResourceId) {
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Release any existing MediaPlayer
        }
        mediaPlayer = MediaPlayer.create(this, soundResourceId);
        mediaPlayer.setOnCompletionListener(mp -> {
            mp.release(); // Release the MediaPlayer once the sound is complete
            mediaPlayer = null; // Set it to null after release
        });
        mediaPlayer.start();
    }

    private void updateQuestionIndex() {
        if (questionList != null && !questionList.isEmpty()) {
            String questionIndexText = (currentQuestionIndex + 1) + "/" + questionList.size();
            questionIndexTextView.setText(questionIndexText);
        }
    }


}
