package com.example.guestureguide;

public class QuizRecord {
    private String quizTitle;
    private int score;
    private int totalQuestions;
    private String dateTaken; // New field

    public QuizRecord(String quizTitle, int score, int totalQuestions, String dateTaken) {
        this.quizTitle = quizTitle;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.dateTaken = dateTaken; // Initialize new field
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public int getScore() {
        return score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public String getDateTaken() {
        return dateTaken; // Getter for new field
    }
}
