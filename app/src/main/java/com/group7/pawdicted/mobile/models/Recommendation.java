package com.group7.pawdicted.mobile.models;

public class Recommendation {
    private String productId;
    private String productName;
    private double distance;
    private String imageUrl;

    public Recommendation(String productId, String productName, double distance, String imageUrl) {
        this.productId = productId;
        this.productName = productName;
        this.distance = distance;
        this.imageUrl = imageUrl;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getDistance() {
        return distance;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
