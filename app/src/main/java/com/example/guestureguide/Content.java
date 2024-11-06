package com.example.guestureguide;

import java.io.Serializable;

public class Content implements Serializable {
    //private String contentId;
    private String name;
    private String imageUrl;
    private String videoUrl;
    private String categoryName;

    public Content(String name, String imageUrl, String videoUrl, String categoryName) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.categoryName = categoryName;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getCategoryName() {
        return categoryName;
    }

}


