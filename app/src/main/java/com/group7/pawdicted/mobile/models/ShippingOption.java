package com.group7.pawdicted.mobile.models;

public class ShippingOption {
    private String title;
    private String details;
    private int cost;

    public ShippingOption(String title, String details, int cost) {
        this.title = title;
        this.details = details;
        this.cost = cost;
    }

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }

    public int getCost() {
        return cost;
    }
}