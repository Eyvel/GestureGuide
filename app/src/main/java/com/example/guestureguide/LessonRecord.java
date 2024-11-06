package com.example.guestureguide;

public class LessonRecord {
    private String categoryName;
    private int totalCompleted;
    private int overallContent;
    private String dateTaken; // New field

    public LessonRecord(String categoryName, int totalCompleted, int overallContent, String dateTaken) {
        this.categoryName = categoryName;
        this.totalCompleted = totalCompleted;
        this.overallContent = overallContent;
        this.dateTaken = dateTaken; // Initialize new field
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getTotalCompleted() {
        return totalCompleted;
    }

    public int getOverallContent() {
        return overallContent;
    }

    public String getDateTaken() {
        return dateTaken; // Getter for new field
    }
}
