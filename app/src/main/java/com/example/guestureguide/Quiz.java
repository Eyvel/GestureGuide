package com.example.guestureguide;

public class Quiz {
    private String id;
    private String quizTitle;
    private boolean isAnswered;

    public Quiz(String id, String quizTitle) {
        this.id = id;
        this.quizTitle = quizTitle;
        this.isAnswered = false;
    }

    public String getId() {
        return id;
    }

    public String getQuizTitle() {
        return quizTitle;
    }
    public boolean isAnswered() {
        return isAnswered;
    }

    public void setAnswered(boolean answered) {
        isAnswered = answered;
    }
}
