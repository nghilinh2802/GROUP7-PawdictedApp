package com.group7.pawdicted.mobile.models;

import com.google.firebase.Timestamp;

public class PromotionNotification {
    private String title;
    private String description;
    private String imageUrl;
    private Timestamp time; // ✅ Sửa từ String -> Timestamp

    public PromotionNotification() {}

    public PromotionNotification(String title, String description, String imageUrl, Timestamp time) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.time = time;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public Timestamp getTime() { return time; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setTime(Timestamp time) { this.time = time; }
}
