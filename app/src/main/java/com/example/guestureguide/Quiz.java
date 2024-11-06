package com.example.guestureguide;

public class Quiz {
    private String id;
    private String quizTitle;

    public Quiz(String id, String quizTitle) {
        this.id = id;
        this.quizTitle = quizTitle;
    }

    public String getId() {
        return id;
    }

    public String getQuizTitle() {
        return quizTitle;
    }
}
