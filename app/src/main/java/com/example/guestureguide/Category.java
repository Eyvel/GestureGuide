package com.example.guestureguide;

public class Category {
    private String id;
    private String category_name;
    private String category_image;

    public Category(String id, String category_name, String category_image) {
        this.id = id;
        this.category_name = category_name;
        this.category_image = category_image;
    }

    public String getId() {
        return id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getCategory_image() {
        return category_image;
    }
}
