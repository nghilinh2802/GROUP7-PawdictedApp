package com.group7.pawdicted.mobile.models;

public class OrderNotification {
    private String title;
    private String description;
    private String time;
    private int imageResId;

    public OrderNotification(String title, String description, String time, int imageResId) {
        this.title = title;
        this.description = description;
        this.time = time;
        this.imageResId = imageResId;
    }

    // Getters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getTime() { return time; }
    public int getImageResId() { return imageResId; }
}

