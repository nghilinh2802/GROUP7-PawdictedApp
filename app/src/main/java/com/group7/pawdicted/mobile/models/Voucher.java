// Voucher.java
package com.group7.pawdicted.mobile.models;

import java.io.Serializable;

public class Voucher implements Serializable {

    private String code;
    private String description;
    private String validity;
    private boolean isSelected;
    private String type; // "merchandise" or "shipping"
    private int discountAmount; // Dưới 100 là %, ngược lại là tiền
    private int minOrderValue;

    public Voucher() {
        // Constructor mặc định cho Firestore
    }

    public Voucher(String code, String description, String validity, boolean isSelected, String type,
                   int discountAmount, int minOrderValue) {
        this.code = code;
        this.description = description;
        this.validity = validity;
        this.isSelected = isSelected;
        this.type = type;
        this.discountAmount = discountAmount;
        this.minOrderValue = minOrderValue;
    }

    public int getDiscountValue(int baseAmount) {
        if (discountAmount < 100) {
            return Math.min((int) (baseAmount * discountAmount / 100.0), baseAmount);
        } else {
            return Math.min(discountAmount, baseAmount);
        }
    }

    public String getMinSpend() {
        return "Min. Spend đ" + String.format("%,d", minOrderValue);
    }

    // Getters and setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getValidity() { return validity; }
    public void setValidity(String validity) { this.validity = validity; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(int discountAmount) { this.discountAmount = discountAmount; }
    public int getMinOrderValue() { return minOrderValue; }
    public void setMinOrderValue(int minOrderValue) { this.minOrderValue = minOrderValue; }
}
