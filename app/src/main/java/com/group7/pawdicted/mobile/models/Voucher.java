package com.group7.pawdicted.mobile.models;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Voucher implements Serializable {

    private String code;
    private String description;
    private transient Timestamp startDate; // Marked transient to exclude from serialization
    private transient Timestamp endDate;   // Marked transient to exclude from serialization
    private String validity;
    private boolean isSelected;
    private String type; // "merchandise" or "shipping"
    private int discountAmount; // Dưới 100 là %, ngược lại là tiền
    private int minOrderValue;
    private String id;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public Voucher() {
        // Constructor mặc định cho Firestore
    }

    public Voucher(String code, String description, Timestamp startDate, Timestamp endDate, boolean isSelected, String type,
                   int discountAmount, int minOrderValue) {
        this.code = code;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.validity = formatValidity(startDate, endDate);
        this.isSelected = isSelected;
        this.type = type;
        this.discountAmount = discountAmount;
        this.minOrderValue = minOrderValue;
    }

    private String formatValidity(Timestamp start, Timestamp end) {
        if (start == null || end == null) return "Unknown";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss", Locale.ENGLISH);
        return sdf.format(start.toDate()) + " - " + sdf.format(end.toDate());
    }

    public boolean isValidNow() {
        // Since filtering is done in fragments, assume voucher is valid for date if displayed
        return true;
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
    public Timestamp getStartDate() { return startDate; }
    public void setStartDate(Timestamp startDate) { this.startDate = startDate; }
    public Timestamp getEndDate() { return endDate; }
    public void setEndDate(Timestamp endDate) { this.endDate = endDate; }
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