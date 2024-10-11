package com.example.guestureguide;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class QuizActivity extends AppCompatActivity {

    private TextView quizTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        quizTitleTextView = findViewById(R.id.quizTitleTextView);

        // Retrieve the quiz title passed from the previous activity
        String quizTitle = getIntent().getStringExtra("quiz_title");

        // Display the quiz title
        quizTitleTextView.setText(quizTitle);
    }
}
