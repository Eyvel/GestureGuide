package com.example.guestureguide;

import java.io.Serializable;

public class Content implements Serializable {
    //private String contentId;
    private String name;
    private String imageUrl;
    private String videoUrl;

    public Content(String name, String imageUrl, String videoUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
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
}


