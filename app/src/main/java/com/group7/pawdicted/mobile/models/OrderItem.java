package com.group7.pawdicted.mobile.models;

public class OrderItem {
    private final String name;
    private final String variant;
    private final String price;
    private final int quantity;
    private final int imageResId;

    public OrderItem(String name, String variant, String price, int quantity, int imageResId) {
        this.name = name;
        this.variant = variant;
        this.price = price;
        this.quantity = quantity;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public String getVariant() { return variant; }
    public String getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public int getImageResId() { return imageResId; }
}
