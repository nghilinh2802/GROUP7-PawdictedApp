package com.group7.pawdicted.mobile.models;
import java.util.List;

public class CartItem {
    public String name;
    public int price;
    public int imageResId;
    public List<String> options;
    public String selectedOption;
    public int quantity;
    public boolean isSelected;

    public CartItem(String name, int price, int imageResId, List<String> options, String selectedOption) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.options = options;
        this.selectedOption = selectedOption;
        this.quantity = 1;
        this.isSelected = false;
    }
}


