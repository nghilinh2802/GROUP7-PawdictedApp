package com.group7.pawdicted.mobile.models;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartItem {
    public String name;
    public int price;
    public String imageUrl;
    public List<String> options;
    public String selectedOption;
    public int quantity;
    public boolean isSelected;
    public Map<String, Integer> optionPrices;

    public CartItem(String name, int price, String imageUrl, List<String> options, String selectedOption) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.options = options;
        this.selectedOption = selectedOption;
        this.quantity = 1;
        this.isSelected = false;
        this.optionPrices = new HashMap<>();
    }
}


