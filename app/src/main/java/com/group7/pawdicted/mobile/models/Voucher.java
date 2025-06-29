package com.group7.pawdicted.mobile.models;

public class Voucher {
    private String code;
    private String minSpend;
    private String validity;
    private String type; // "merchandise" hoặc "shipping"
    private boolean isSelected;

    public Voucher() {} // Firebase cần constructor rỗng

    public Voucher(String code, String minSpend, String validity, String type, boolean isSelected) {
        this.code = code;
        this.minSpend = minSpend;
        this.validity = validity;
        this.type = type;
        this.isSelected = isSelected;
    }

    // Getter & Setter
    public String getCode() { return code; }
    public String getMinSpend() { return minSpend; }
    public String getValidity() { return validity; }
    public String getType() { return type; }
    public boolean isSelected() { return isSelected; }

    public void setCode(String code) { this.code = code; }
    public void setMinSpend(String minSpend) { this.minSpend = minSpend; }
    public void setValidity(String validity) { this.validity = validity; }
    public void setType(String type) { this.type = type; }
    public void setSelected(boolean selected) { isSelected = selected; }
}

