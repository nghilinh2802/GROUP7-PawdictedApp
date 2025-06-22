package com.group7.pawdicted.mobile.models;

public class Voucher {
    private String title;
    private String minSpend;
    private String validity;
    private boolean isSelected;

    public Voucher(String title, String minSpend, String validity, boolean isSelected) {
        this.title = title;
        this.minSpend = minSpend;
        this.validity = validity;
        this.isSelected = isSelected;
    }

    public String getTitle() {
        return title;
    }
    public String getMinSpend() {
        return minSpend;
    }
    public String getValidity() {
        return validity;
    }
    public boolean isSelected() {
        return isSelected;
    }
}
