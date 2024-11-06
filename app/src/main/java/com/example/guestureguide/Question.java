package com.example.guestureguide;

public class Question {

    private String questionText;
    private String optionA, optionB;
    private String correctAnswer;

    public Question(String questionText, String optionA, String optionB,  String correctAnswer) {
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;

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



    public String getCorrectAnswer() {
        return correctAnswer;

    }
}
