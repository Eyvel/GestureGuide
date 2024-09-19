package com.example.guestureguide;

public class Question {

    private String questionText;
    private String optionA, optionB, optionC, optionD;
    private String correctAnswer;

    public Question(String questionText, String optionA, String optionB, String optionC, String optionD, String correctAnswer) {
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;

    }

    public String getOptionD() {
        return optionD;

    }

    public String getCorrectAnswer() {
        return correctAnswer;

    }
}
