package com.example.guestureguide;

public class Question {

    private int questionId;
    private String questionText;
    private String optionA, optionB;
    private String correctAnswer;
    private String questionVideo;
    private int points;

    public Question(int questionId, String questionText, String optionA, String optionB,  String correctAnswer, String questionVideo, int points) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.points = points;


        this.correctAnswer = correctAnswer;
        this.questionVideo = questionVideo;
    }
    public int getQuestionId() {
        return questionId;
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

    public String getQuestionVideo() {
        return questionVideo;
    }




    public String getCorrectAnswer() {
        return correctAnswer;

    }
    public int getPoints() { // Add getter for points
        return points;
    }
}
