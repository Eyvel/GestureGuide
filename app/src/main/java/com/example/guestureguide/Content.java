package com.example.guestureguide;

public class Content {
    private String name;
    private String imageUrl;

    public Content(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
