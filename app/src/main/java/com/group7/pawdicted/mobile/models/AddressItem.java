package com.group7.pawdicted.mobile.models;

import java.io.Serializable;

public class AddressItem implements Serializable {
    private String name;
    private String phone;
    private String address;
    private boolean isDefault;

    public AddressItem(String name, String phone, String address, boolean isDefault) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.isDefault = isDefault;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    @Override
    public String toString() {
        return name + " - " + phone + " - " + address + (isDefault ? " (Default)" : "");
    }
}